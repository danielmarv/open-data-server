package com.openelements.opendata.metrics;

import com.openelements.opendata.base.DTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Schema(title = "ContributorMetric", description = "A metric for a contributor in a specific project")
public record ContributorMetricDTO(
        @NonNull @Schema(description = "UUID of the metric", example = "b14763b0-60e0-4861-ba30-53f17cc4471e", required = true) String uuid,
        @NonNull @Schema(description = "GitHub username of the contributor", example = "octocat", required = true) String contributor,
        @NonNull @Schema(description = "Name of the GitHub org", example = "OpenElements", required = true) String org,
        @NonNull @Schema(description = "Name of the GitHub repository", example = "open-elements-website", required = true) String repository,
        @Nullable @Schema(description = "ID of the project", example = "Project-open-elements-website", required = false) String projectId,
        @NonNull @Schema(description = "Type of metric", example = "PR_MERGED", required = true) MetricType metricType,
        @Schema(description = "Value of the metric", example = "5.0", required = true) double value,
        @NonNull @Schema(description = "Date of the metric", example = "2023-05-15", required = true) LocalDate date,
        @NonNull @Schema(description = "Timestamp when the metric was recorded", example = "2023-05-15T14:30:15.123Z", required = true) ZonedDateTime timestamp
) implements DTO {

    public ContributorMetricDTO {
        Objects.requireNonNull(uuid, "uuid cannot be null");
        Objects.requireNonNull(contributor, "contributor cannot be null");
        Objects.requireNonNull(org, "org cannot be null");
        Objects.requireNonNull(repository, "repository cannot be null");
        Objects.requireNonNull(metricType, "metricType cannot be null");
        Objects.requireNonNull(date, "date cannot be null");
        Objects.requireNonNull(timestamp, "timestamp cannot be null");
    }
}
