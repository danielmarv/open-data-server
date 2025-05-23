package com.openelements.opendata.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.openelements.opendata.issues.IssueService;
import com.openelements.opendata.metrics.ContributorMetricService;
import com.openelements.opendata.metrics.MetricType;
import com.openelements.opendata.pullrequests.PullRequestService;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final IssueService issueService;
    private final PullRequestService pullRequestService;
    private final ContributorMetricService metricService;

    public WebhookController(
            IssueService issueService,
            PullRequestService pullRequestService,
            ContributorMetricService metricService) {
        this.issueService = Objects.requireNonNull(issueService);
        this.pullRequestService = Objects.requireNonNull(pullRequestService);
        this.metricService = Objects.requireNonNull(metricService);
    }

    @PostMapping("/github")
    public ResponseEntity<String> handleGitHubWebhook(@RequestBody JsonNode payload) {
        String eventType = payload.has("action") ? payload.get("action").asText() : "unknown";
        
        if (payload.has("issue")) {
            handleIssueEvent(payload, eventType);
        } else if (payload.has("pull_request")) {
            handlePullRequestEvent(payload, eventType);
        } else if (payload.has("commits")) {
            handlePushEvent(payload);
        }
        
        return ResponseEntity.ok("Webhook processed successfully");
    }
    
    private void handleIssueEvent(JsonNode payload, String eventType) {
        JsonNode issue = payload.get("issue");
        JsonNode repository = payload.get("repository");
        
        String org = repository.get("owner").get("login").asText();
        String repo = repository.get("name").asText();
        long issueId = issue.get("number").asLong();
        String title = issue.get("title").asText();
        String body = issue.get("body") != null && !issue.get("body").isNull() ? 
                issue.get("body").asText() : "";
        boolean isOpen = "open".equals(issue.get("state").asText());
        String author = issue.get("user").get("login").asText();
        
        String assignee = null;
        if (issue.has("assignee") && !issue.get("assignee").isNull()) {
            assignee = issue.get("assignee").get("login").asText();
        }
        
        Set<String> labels = new HashSet<>();
        if (issue.has("labels") && issue.get("labels").isArray()) {
            for (JsonNode label : issue.get("labels")) {
                labels.add(label.get("name").asText());
            }
        }
        
        int commentCount = issue.get("comments").asInt();
        
        ZonedDateTime createdAt = ZonedDateTime.parse(issue.get("created_at").asText());
        ZonedDateTime updatedAt = ZonedDateTime.parse(issue.get("updated_at").asText());
        
        ZonedDateTime closedAt = null;
        if (issue.has("closed_at") && !issue.get("closed_at").isNull()) {
            closedAt = ZonedDateTime.parse(issue.get("closed_at").asText());
        }
        
        issueService.createOrUpdateIssue(
                org, repo, issueId, title, body, isOpen, author, assignee,
                labels, commentCount, createdAt, updatedAt, closedAt);
        
        // Record metrics based on event type
        switch (eventType) {
            case "opened":
                metricService.recordMetric(author, org, repo, MetricType.ISSUE_CREATED, 1);
                break;
            case "closed":
                metricService.recordMetric(author, org, repo, MetricType.ISSUE_CLOSED, 1);
                break;
            case "reopened":
                metricService.recordMetric(author, org, repo, MetricType.ISSUE_REOPENED, 1);
                break;
            case "assigned":
                metricService.recordMetric(author, org, repo, MetricType.ISSUE_ASSIGNED, 1);
                break;
            case "labeled":
                metricService.recordMetric(author, org, repo, MetricType.ISSUE_LABELED, 1);
                break;
            case "commented":
                metricService.recordMetric(author, org, repo, MetricType.ISSUE_COMMENTS, 1);
                break;
        }
    }
    
    private void handlePullRequestEvent(JsonNode payload, String eventType) {
        JsonNode pullRequest = payload.get("pull_request");
        JsonNode repository = payload.get("repository");
        
        String org = repository.get("owner").get("login").asText();
        String repo = repository.get("name").asText();
        long prId = pullRequest.get("number").asLong();
        String title = pullRequest.get("title").asText();
        boolean isOpen = "open".equals(pullRequest.get("state").asText());
        boolean isMerged = pullRequest.has("merged") && pullRequest.get("merged").asBoolean();
        boolean isDraft = pullRequest.has("draft") && pullRequest.get("draft").asBoolean();
        String author = pullRequest.get("user").get("login").asText();
        
        ZonedDateTime createdAt = ZonedDateTime.parse(pullRequest.get("created_at").asText());
        ZonedDateTime updatedAt = ZonedDateTime.parse(pullRequest.get("updated_at").asText());
        
        // Record metrics based on event type
        switch (eventType) {
            case "opened":
                metricService.recordMetric(author, org, repo, MetricType.PR_CREATED, 1);
                break;
            case "closed":
                if (isMerged) {
                    metricService.recordMetric(author, org, repo, MetricType.PR_MERGED, 1);
                } else {
                    metricService.recordMetric(author, org, repo, MetricType.PR_CLOSED, 1);
                }
                break;
            case "review_requested":
                metricService.recordMetric(author, org, repo, MetricType.PR_REVIEWED, 1);
                break;
            case "commented":
                metricService.recordMetric(author, org, repo, MetricType.PR_COMMENTS, 1);
                break;
        }
        
        // Record code changes if available
        if (pullRequest.has("additions") && pullRequest.has("deletions") && pullRequest.has("changed_files")) {
            int additions = pullRequest.get("additions").asInt();
            int deletions = pullRequest.get("deletions").asInt();
            int changedFiles = pullRequest.get("changed_files").asInt();
            
            metricService.recordMetric(author, org, repo, MetricType.PR_ADDITIONS, additions);
            metricService.recordMetric(author, org, repo, MetricType.PR_DELETIONS, deletions);
            metricService.recordMetric(author, org, repo, MetricType.PR_CHANGED_FILES, changedFiles);
        }
    }
    
    private void handlePushEvent(JsonNode payload) {
        JsonNode repository = payload.get("repository");
        String org = repository.get("owner").get("login").asText();
        String repo = repository.get("name").asText();
        String author = payload.get("sender").get("login").asText();
        
        // Count commits in this push
        if (payload.has("commits") && payload.get("commits").isArray()) {
            int commitCount = payload.get("commits").size();
            metricService.recordMetric(author, org, repo, MetricType.COMMIT_COUNT, commitCount);
        }
    }
}
