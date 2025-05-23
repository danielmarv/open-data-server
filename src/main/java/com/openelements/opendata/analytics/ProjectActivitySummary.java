package com.openelements.opendata.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

@Schema(title = "ProjectActivitySummary", description = "Summary of activity for a project")
public record ProjectActivitySummary(
        @NonNull @Schema(description = "UUID of the project", example = "Project-open-elements-website", required = true) String projectId,
        @NonNull @Schema(description = "Name of the project", example = "Open Elements Website", required = true) String projectName,
        @Schema(description = "Total number of pull requests", example = "120", required = true) long totalPullRequests,
        @Schema(description = "Number of open pull requests", example = "15", required = true) long openPullRequests,
        @Schema(description = "Number of merged pull requests", example = "95", required = true) long mergedPullRequests,
        @Schema(description = "Total number of issues", example = "85", required = true) long totalIssues,
        @Schema(description = "Number of open issues", example = "25", required = true) long openIssues
) {
    public ProjectActivitySummary {
        Objects.requireNonNull(projectId, "projectId cannot be null");
        Objects.requireNonNull(projectName, "projectName cannot be null");
    }
}
