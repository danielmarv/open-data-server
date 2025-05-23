package com.openelements.opendata.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

@Schema(title = "ContributorActivitySummary", description = "Summary of activity for a contributor")
public record ContributorActivitySummary(
        @NonNull @Schema(description = "GitHub username of the contributor", example = "octocat", required = true) String contributor,
        @Schema(description = "Year of the summary", example = "2023", required = true) int year,
        @Schema(description = "Number of pull requests created", example = "45", required = true) long prsCreated,
        @Schema(description = "Number of pull requests merged", example = "38", required = true) long prsMerged,
        @Schema(description = "Number of issues created", example = "27", required = true) long issuesCreated,
        @Schema(description = "Number of issues assigned", example = "15", required = true) long issuesAssigned,
        @Schema(description = "Number of commits", example = "156", required = true) double commitCount,
        @Schema(description = "Number of code reviews performed", example = "32", required = true) double codeReviewCount,
        @Schema(description = "Number of comments made", example = "87", required = true) double commentCount
) {
    public ContributorActivitySummary {
        Objects.requireNonNull(contributor, "contributor cannot be null");
    }
}
