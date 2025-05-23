package com.openelements.opendata.issues;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/issues")
@Tag(name = "IssuesEndpoint", description = "Endpoint to get information about GitHub issues")
public class IssueEndpoint {

    private final IssueService issueService;

    public IssueEndpoint(@NonNull final IssueService issueService) {
        this.issueService = Objects.requireNonNull(issueService);
    }

    @GetMapping("/repository/{org}/{repository}")
    @Operation(summary = "Get all issues for a specific repository")
    public ResponseEntity<List<IssueDTO>> getIssuesByRepository(
            @PathVariable String org,
            @PathVariable String repository) {
        List<IssueDTO> issues = issueService.getIssuesByRepository(org, repository);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/repository/{org}/{repository}/open")
    @Operation(summary = "Get open issues for a specific repository")
    public ResponseEntity<List<IssueDTO>> getOpenIssuesByRepository(
            @PathVariable String org,
            @PathVariable String repository) {
        List<IssueDTO> issues = issueService.getOpenIssuesByRepository(org, repository);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/author/{author}")
    @Operation(summary = "Get issues created by a specific author")
    public ResponseEntity<List<IssueDTO>> getIssuesByAuthor(
            @PathVariable String author) {
        List<IssueDTO> issues = issueService.getIssuesByAuthor(author);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/assignee/{assignee}")
    @Operation(summary = "Get issues assigned to a specific user")
    public ResponseEntity<List<IssueDTO>> getIssuesByAssignee(
            @PathVariable String assignee) {
        List<IssueDTO> issues = issueService.getIssuesByAssignee(assignee);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/label/{label}")
    @Operation(summary = "Get issues with a specific label")
    public ResponseEntity<List<IssueDTO>> getIssuesByLabel(
            @PathVariable String label) {
        List<IssueDTO> issues = issueService.getIssuesByLabel(label);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/repository/{org}/{repository}/label")
    @Operation(summary = "Get issues for a specific repository with a specific label")
    public ResponseEntity<List<IssueDTO>> getIssuesByRepositoryAndLabel(
            @PathVariable String org,
            @PathVariable String repository,
            @RequestParam String label) {
        List<IssueDTO> issues = issueService.getIssuesByRepositoryAndLabel(org, repository, label);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all issues for a specific project")
    public ResponseEntity<List<IssueDTO>> getIssuesByProject(
            @PathVariable String projectId) {
        List<IssueDTO> issues = issueService.getIssuesByProject(projectId);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/repository/{org}/{repository}/count/open")
    @Operation(summary = "Count open issues for a specific repository")
    public ResponseEntity<Long> countOpenIssuesByRepository(
            @PathVariable String org,
            @PathVariable String repository) {
        long count = issueService.countOpenIssuesByRepository(org, repository);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/repository/{org}/{repository}/count/closed")
    @Operation(summary = "Count closed issues for a specific repository")
    public ResponseEntity<Long> countClosedIssuesByRepository(
            @PathVariable String org,
            @PathVariable String repository) {
        long count = issueService.countClosedIssuesByRepository(org, repository);
        return ResponseEntity.ok(count);
    }
}
