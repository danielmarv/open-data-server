package com.openelements.opendata.metrics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
@Tag(name = "MetricsEndpoint", description = "Endpoint to get metrics about projects, repositories, and contributors")
public class MetricsEndpoint {

    private final ContributorMetricService metricService;

    public MetricsEndpoint(@NonNull final ContributorMetricService metricService) {
        this.metricService = Objects.requireNonNull(metricService);
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all metrics for a specific project")
    public ResponseEntity<List<ContributorMetricDTO>> getProjectMetrics(
            @PathVariable String projectId) {
        List<ContributorMetricDTO> metrics = metricService.getMetricsByProject(projectId);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/project/{projectId}/period")
    @Operation(summary = "Get metrics for a specific project within a date range")
    public ResponseEntity<List<ContributorMetricDTO>> getProjectMetricsByPeriod(
            @PathVariable String projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ContributorMetricDTO> metrics = metricService.getMetricsByProjectAndPeriod(projectId, startDate, endDate);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/project/{projectId}/summary")
    @Operation(summary = "Get a summary of metrics for a specific project")
    public ResponseEntity<Map<MetricType, Double>> getProjectMetricSummary(
            @PathVariable String projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<MetricType, Double> summary = metricService.getProjectMetricSummary(projectId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/project/{projectId}/top-contributors")
    @Operation(summary = "Get top contributors for a specific project and metric type")
    public ResponseEntity<List<ContributorSummary>> getTopContributors(
            @PathVariable String projectId,
            @RequestParam MetricType metricType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        List<ContributorSummary> topContributors = metricService.getTopContributors(
                projectId, metricType, startDate, endDate, limit);
        return ResponseEntity.ok(topContributors);
    }
}
