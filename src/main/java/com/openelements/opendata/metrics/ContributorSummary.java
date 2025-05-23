package com.openelements.opendata.metrics;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

@Schema(title = "ContributorSummary", description = "Summary of a contributor's metrics")
public record ContributorSummary(
        @NonNull @Schema(description = "GitHub username of the contributor", example = "octocat", required = true) String username,
        @Schema(description = "Value of the metric", example = "42.0", required = true) double value
) {
    public ContributorSummary {
        Objects.requireNonNull(username, "username cannot be null");
    }
}
