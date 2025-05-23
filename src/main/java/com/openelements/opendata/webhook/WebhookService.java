package com.openelements.opendata.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openelements.opendata.issues.IssueService;
import com.openelements.opendata.metrics.ContributorMetricService;
import com.openelements.opendata.metrics.MetricType;
import com.openelements.opendata.pullrequests.PullRequestService;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WebhookService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final IssueService issueService;
    private final PullRequestService pullRequestService;
    private final ContributorMetricService metricService;
    
    @Value("${github.api.token:}")
    private String githubToken;

    public WebhookService(
            IssueService issueService,
            PullRequestService pullRequestService,
            ContributorMetricService metricService,
            ObjectMapper objectMapper) {
        this.issueService = Objects.requireNonNull(issueService);
        this.pullRequestService = Objects.requireNonNull(pullRequestService);
        this.metricService = Objects.requireNonNull(metricService);
        this.objectMapper = Objects.requireNonNull(objectMapper);
        
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
    
    public boolean configureWebhook(String org, String repo, String webhookUrl, String secret) {
        try {
            String apiUrl = String.format("https://api.github.com/repos/%s/%s/hooks", org, repo);
            
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "name", "web",
                    "active", true,
                    "events", List.of("push", "pull_request", "issues", "issue_comment"),
                    "config", Map.of(
                            "url", webhookUrl,
                            "content_type", "json",
                            "secret", secret,
                            "insecure_ssl", "0"
                    )
            ));
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("Authorization", "token " + githubToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            return response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (Exception e) {
            return false;
        }
    }
    
    public JsonNode getRepositoryMetrics(String org, String repo) {
        try {
            String apiUrl = String.format("https://api.github.com/repos/%s/%s", org, repo);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("Authorization", "token " + githubToken)
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonNode repoData = objectMapper.readTree(response.body());
                
                // Record repository metrics
                if (repoData.has("stargazers_count")) {
                    metricService.recordMetric("system", org, repo, MetricType.STAR_COUNT, 
                            repoData.get("stargazers_count").asDouble());
                }
                
                if (repoData.has("forks_count")) {
                    metricService.recordMetric("system", org, repo, MetricType.FORK_COUNT, 
                            repoData.get("forks_count").asDouble());
                }
                
                if (repoData.has("subscribers_count")) {
                    metricService.recordMetric("system", org, repo, MetricType.WATCH_COUNT, 
                            repoData.get("subscribers_count").asDouble());
                }
                
                return repoData;
            }
            
            return objectMapper.createObjectNode();
        } catch (Exception e) {
            return objectMapper.createObjectNode();
        }
    }
}
