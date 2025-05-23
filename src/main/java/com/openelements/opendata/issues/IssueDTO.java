package com.openelements.opendata.issues;

import com.openelements.opendata.base.DTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Schema(title = "Issue", description = "A GitHub issue")
public record IssueDTO(
        @NonNull @Schema(description = "UUID of the issue", example = "b14763b0-60e0-4861-ba30-53f17cc4471e", required = true) String uuid,
        @NonNull @Schema(description = "Name of the GitHub org", example = "OpenElements", required = true) String org,
        @NonNull @Schema(description = "Name of the GitHub repository", example = "open-elements-website", required = true) String repository,
        @NonNull @Schema(description = "ID of the issue in GitHub", example = "123456", required = true) long gitHubId,
        @NonNull @Schema(description = "Title of the issue", example = "Fix navigation bug", required = true) String title,
        @NonNull @Schema(description = "Body of the issue", example = "The navigation menu doesn't work on mobile devices", required = true) String body,
        @Schema(description = "Whether the issue is open", required = true) boolean open,
        @NonNull @Schema(description = "GitHub username of the author", example = "octocat", required = true) String author,
        @Nullable @Schema(description = "GitHub username of the assignee", example = "octocat", required = false) String assignee,
        @NonNull @Schema(description = "Labels attached to the issue", example = "[\"bug\", \"priority\"]", required = true) Set<String> labels,
        @Schema(description = "Number of comments on the issue", example = "5", required = true) int commentCount,
        @NonNull @Schema(description = "Creation time of the issue in GitHub", example = "2023-05-15T14:30:15.123Z", required = true) ZonedDateTime createdAtInGitHub,
        @NonNull @Schema(description = "Last update time of the issue in GitHub", example = "2023-05-16T10:45:30.456Z", required = true) ZonedDateTime lastUpdateInGitHub,
        @Nullable @Schema(description = "Closing time of the issue in GitHub", example = "2023-05-20T16:20:10.789Z", required = false) ZonedDateTime closedAtInGitHub
) implements DTO {

    public IssueDTO {
        Objects.requireNonNull(uuid, "uuid cannot be null");
        Objects.requireNonNull(org, "org cannot be null");
        Objects.requireNonNull(repository, "repository cannot be null");
        Objects.requireNonNull(title, "title cannot be null");
        Objects.requireNonNull(body, "body cannot be null");
        Objects.requireNonNull(author, "author cannot be null");
        Objects.requireNonNull(labels, "labels cannot be null");
        Objects.requireNonNull(createdAtInGitHub, "createdAtInGitHub cannot be null");
        Objects.requireNonNull(lastUpdateInGitHub, "lastUpdateInGitHub cannot be null");
    }
}
