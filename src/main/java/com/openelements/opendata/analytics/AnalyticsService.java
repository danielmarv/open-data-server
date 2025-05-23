package com.openelements.opendata.analytics;

import com.openelements.opendata.base.Language;
import com.openelements.opendata.issues.IssueService;
import com.openelements.opendata.metrics.ContributorMetricService;
import com.openelements.opendata.metrics.MetricType;
import com.openelements.opendata.project.ProjectDTO;
import com.openelements.opendata.project.ProjectService;
import com.openelements.opendata.pullrequests.PullRequestService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final ProjectService projectService;
    private final PullRequestService pullRequestService;
    private final IssueService issueService;
    private final ContributorMetricService metricService;

    public AnalyticsService(
            ProjectService projectService,
            PullRequestService pullRequestService,
            IssueService issueService,
            ContributorMetricService metricService) {
        this.projectService = Objects.requireNonNull(projectService);
        this.pullRequestService = Objects.requireNonNull(pullRequestService);
        this.issueService = Objects.requireNonNull(issueService);
        this.metricService = Objects.requireNonNull(metricService);
    }

    public List<ProjectActivitySummary> getProjectActivitySummaries() {
        List<ProjectDTO> projects = projectService.getAll(Language.EN);
        
        return projects.stream().map(project -> {
            String projectId = project.uuid();
            
            // Get repositories for this project
            List<String> repositories = project.matchingRepos().stream().toList();
            
            // Count PRs and issues for each repository
            long totalPRs = 0;
            long openPRs = 0;
            long mergedPRs = 0;
            long totalIssues = 0;
            long openIssues = 0;
            
            for (String repo : repositories) {
                String[] parts = repo.split("/");
                if (parts.length == 2) {
                    String org = parts[0];
                    String repository = parts[1];
                    
                    // Count PRs
                    totalPRs += pullRequestService.getAll(Language.EN).stream()
                            .filter(pr -> pr.org().equals(org) && pr.repository().equals(repository))
                            .count();
                    
                    openPRs += pullRequestService.getAll(Language.EN).stream()
                            .filter(pr -> pr.org().equals(org) && pr.repository().equals(repository) && pr.open())
                            .count();
                    
                    mergedPRs += pullRequestService.getAll(Language.EN).stream()
                            .filter(pr -> pr.org().equals(org) && pr.repository().equals(repository) && pr.merged())
                            .count();
                    
                    // Count issues
                    totalIssues += issueService.getIssuesByRepository(org, repository).size();
                    openIssues += issueService.countOpenIssuesByRepository(org, repository);
                }
            }
            
            return new ProjectActivitySummary(
                    projectId,
                    project.name(),
                    totalPRs,
                    openPRs,
                    mergedPRs,
                    totalIssues,
                    openIssues
            );
        }).collect(Collectors.toList());
    }
    
    public ProjectActivitySummary getProjectActivitySummary(String projectId) {
        String projectPrefix = "Project-";
        if (!projectId.startsWith(projectPrefix)) {
            projectId = projectPrefix + projectId;
        }
        
        final String finalProjectId = projectId;
        ProjectDTO project = projectService.getAll(Language.EN).stream()
                .filter(p -> p.uuid().equals(finalProjectId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + finalProjectId));
        
        // Get repositories for this project
        List<String> repositories = project.matchingRepos().stream().toList();
        
        // Count PRs and issues for each repository
        long totalPRs = 0;
        long openPRs = 0;
        long mergedPRs = 0;
        long totalIssues = 0;
        long openIssues = 0;
        
        for (String repo : repositories) {
            String[] parts = repo.split("/");
            if (parts.length == 2) {
                String org = parts[0];
                String repository = parts[1];
                
                // Count PRs
                totalPRs += pullRequestService.getAll(Language.EN).stream()
                        .filter(pr -> pr.org().equals(org) && pr.repository().equals(repository))
                        .count();
                
                openPRs += pullRequestService.getAll(Language.EN).stream()
                        .filter(pr -> pr.org().equals(org) && pr.repository().equals(repository) && pr.open())
                        .count();
                
                mergedPRs += pullRequestService.getAll(Language.EN).stream()
                        .filter(pr -> pr.org().equals(org) && pr.repository().equals(repository) && pr.merged())
                        .count();
                
                // Count issues
                totalIssues += issueService.getIssuesByRepository(org, repository).size();
                openIssues += issueService.countOpenIssuesByRepository(org, repository);
            }
        }
        
        return new ProjectActivitySummary(
                finalProjectId,
                project.name(),
                totalPRs,
                openPRs,
                mergedPRs,
                totalIssues,
                openIssues
        );
    }
    
    public ContributorActivitySummary getContributorActivitySummary(String contributor, int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        
        Map<MetricType, Double> metrics = metricService.getAll(Language.EN).stream()
                .filter(metric -> metric.contributor().equals(contributor) && 
                        metric.date().isAfter(startDate.minusDays(1)) && 
                        metric.date().isBefore(endDate.plusDays(1)))
                .collect(Collectors.groupingBy(
                        metric -> metric.metricType(),
                        Collectors.summingDouble(metric -> metric.value())
                ));
        
        // Get PRs created by this contributor
        long prsCreated = pullRequestService.getAll(Language.EN).stream()
                .filter(pr -> pr.author().equals(contributor))
                .count();
        
        // Get PRs merged by this contributor
        long prsMerged = pullRequestService.getAll(Language.EN).stream()
                .filter(pr -> pr.author().equals(contributor) && pr.merged())
                .count();
        
        // Get issues created by this contributor
        long issuesCreated = issueService.getIssuesByAuthor(contributor).size();
        
        // Get issues assigned to this contributor
        long issuesAssigned = issueService.getIssuesByAssignee(contributor).size();
        
        return new ContributorActivitySummary(
                contributor,
                year,
                prsCreated,
                prsMerged,
                issuesCreated,
                issuesAssigned,
                metrics.getOrDefault(MetricType.COMMIT_COUNT, 0.0),
                metrics.getOrDefault(MetricType.CODE_REVIEW_COUNT, 0.0),
                metrics.getOrDefault(MetricType.COMMENT_COUNT, 0.0)
        );
    }
    
    public TeamPerformanceSummary getTeamPerformanceSummary(List<String> teamMembers, int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        
        // Collect individual contributor summaries
        List<ContributorActivitySummary> memberSummaries = teamMembers.stream()
                .map(member -> getContributorActivitySummary(member, year))
                .collect(Collectors.toList());
        
        // Aggregate team metrics
        long totalPRsCreated = memberSummaries.stream().mapToLong(ContributorActivitySummary::prsCreated).sum();
        long totalPRsMerged = memberSummaries.stream().mapToLong(ContributorActivitySummary::prsMerged).sum();
        long totalIssuesCreated = memberSummaries.stream().mapToLong(ContributorActivitySummary::issuesCreated).sum();
        long totalIssuesAssigned = memberSummaries.stream().mapToLong(ContributorActivitySummary::issuesAssigned).sum();
        double totalCommits = memberSummaries.stream().mapToDouble(ContributorActivitySummary::commitCount).sum();
        double totalCodeReviews = memberSummaries.stream().mapToDouble(ContributorActivitySummary::codeReviewCount).sum();
        double totalComments = memberSummaries.stream().mapToDouble(ContributorActivitySummary::commentCount).sum();
        
        // Calculate average metrics per team member
        double avgPRsCreated = (double) totalPRsCreated / teamMembers.size();
        double avgPRsMerged = (double) totalPRsMerged / teamMembers.size();
        double avgIssuesCreated = (double) totalIssuesCreated / teamMembers.size();
        double avgIssuesAssigned = (double) totalIssuesAssigned / teamMembers.size();
        double avgCommits = totalCommits / teamMembers.size();
        double avgCodeReviews = totalCodeReviews / teamMembers.size();
        double avgComments = totalComments / teamMembers.size();
        
        // Calculate PR merge rate
        double mergeRate = totalPRsCreated > 0 ? (double) totalPRsMerged / totalPRsCreated : 0;
        
        // Get top contributor for each metric
        Map<String, String> topContributors = new HashMap<>();
        
        if (!memberSummaries.isEmpty()) {
            topContributors.put("prsCreated", memberSummaries.stream()
                    .max((a, b) -> Long.compare(a.prsCreated(), b.prsCreated()))
                    .map(ContributorActivitySummary::contributor)
                    .orElse(""));
            
            topContributors.put("prsMerged", memberSummaries.stream()
                    .max((a, b) -> Long.compare(a.prsMerged(), b.prsMerged()))
                    .map(ContributorActivitySummary::contributor)
                    .orElse(""));
            
            topContributors.put("issuesCreated", memberSummaries.stream()
                    .max((a, b) -> Long.compare(a.issuesCreated(), b.issuesCreated()))
                    .map(ContributorActivitySummary::contributor)
                    .orElse(""));
            
            topContributors.put("commits", memberSummaries.stream()
                    .max((a, b) -> Double.compare(a.commitCount(), b.commitCount()))
                    .map(ContributorActivitySummary::contributor)
                    .orElse(""));
            
            topContributors.put("codeReviews", memberSummaries.stream()
                    .max((a, b) -> Double.compare(a.codeReviewCount(), b.codeReviewCount()))
                    .map(ContributorActivitySummary::contributor)
                    .orElse(""));
        }
        
        return new TeamPerformanceSummary(
                teamMembers,
                year,
                totalPRsCreated,
                totalPRsMerged,
                totalIssuesCreated,
                totalIssuesAssigned,
                totalCommits,
                totalCodeReviews,
                totalComments,
                avgPRsCreated,
                avgPRsMerged,
                avgIssuesCreated,
                avgIssuesAssigned,
                avgCommits,
                avgCodeReviews,
                avgComments,
                mergeRate,
                topContributors
        );
    }
}
