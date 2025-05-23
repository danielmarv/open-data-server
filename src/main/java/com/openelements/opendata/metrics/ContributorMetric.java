package com.openelements.opendata.metrics;

import com.openelements.opendata.base.db.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import org.jspecify.annotations.NonNull;

/**
 * Entity representing a metric for a contributor in a specific project.
 */
@Entity
public class ContributorMetric extends AbstractEntity {

    @Column(nullable = false)
    private String contributor;
    
    @Column(nullable = false)
    private String org;
    
    @Column(nullable = false)
    private String repository;
    
    @Column(nullable = true)
    private String projectId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetricType metricType;
    
    @Column(nullable = false)
    private double value;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private ZonedDateTime timestamp;

    public String getContributor() {
        return contributor;
    }

    public void setContributor(@NonNull String contributor) {
        this.contributor = contributor;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(@NonNull String org) {
        this.org = org;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(@NonNull String repository) {
        this.repository = repository;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public void setMetricType(@NonNull MetricType metricType) {
        this.metricType = metricType;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(@NonNull LocalDate date) {
        this.date = date;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NonNull ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
