package com.openelements.opendata.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

@Schema(title = "TeamPerformanceSummary", description = "Summary of performance for a team")
public record TeamPerformanceSummary(
        @NonNull @Schema(description = "List of team members", example = "[\"octocat\", \"monalisa\"]", required = true) List<String> teamMembers,
        @Schema(description = "Year of the summary", example = "2023", required = true) int year,
        @Schema(description = "Total number of pull requests created", example = "120", required = true) long totalPRsCreated,
        @Schema(description = "Total number of pull requests merged", example = "95", required = true) long totalPRsMerged,
        @Schema(description = "Total number of issues created", example = "85", required = true) long totalIssuesCreated,
        @Schema(description = "Total number of issues assigned", example = "65", required = true) long totalIssuesAssigned,
        @Schema(description = "Total number of commits", example = "450", required = true) double totalCommits,
        @Schema(description = "Total number of code reviews", example = "110", required = true) double totalCodeReviews,
        @Schema(description = "Total number of comments", example = "320", required = true) double totalComments,
        @Schema(description = "Average pull requests created per team member", example = "40.0", required = true) double avgPRsCreated,
        @Schema(description = "Average pull requests merged per team member", example = "31.7", required = true) double avgPRsMerged,
        @Schema(description = "Average issues created per team member", example = "28.3", required = true) double avgIssuesCreated,
        @Schema(description = "Average issues assigned per team member", example = "21.7", required = true) double avgIssuesAssigned,
        @Schema(description = "Average commits per team member", example = "150.0", required = true) double avgCommits,
        @Schema(description = "Average code reviews per team member", example = "36.7", required = true) double avgCodeReviews,
        @Schema(description = "Average comments per team member", example = "106.7", required = true) double avgComments,
        @Schema(description = "Pull request merge rate", example = "0.79", required = true) double prMergeRate,
        @NonNull @Schema(description = "Top contributors for each metric", required = true) Map<String, String> topContributors
) {
    public TeamPerformanceSummary {
        Objects.requireNonNull(teamMembers, "teamMembers cannot be null");
        Objects.requireNonNull(topContributors, "topContributors cannot be null");
    }
}
