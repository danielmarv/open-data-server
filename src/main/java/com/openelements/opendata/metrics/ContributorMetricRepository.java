package com.openelements.opendata.metrics;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContributorMetricRepository extends JpaRepository<ContributorMetric, String> {

    List<ContributorMetric> findByContributor(String contributor);
    
    List<ContributorMetric> findByOrgAndRepository(String org, String repository);
    
    List<ContributorMetric> findByProjectId(String projectId);
    
    List<ContributorMetric> findByMetricType(MetricType metricType);
    
    List<ContributorMetric> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<ContributorMetric> findByContributorAndDateBetween(String contributor, LocalDate startDate, LocalDate endDate);
    
    List<ContributorMetric> findByProjectIdAndDateBetween(String projectId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT m FROM ContributorMetric m WHERE m.projectId = ?1 AND m.metricType = ?2 AND m.date BETWEEN ?3 AND ?4")
    List<ContributorMetric> findByProjectIdAndMetricTypeAndDateBetween(String projectId, MetricType metricType, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(m.value) FROM ContributorMetric m WHERE m.projectId = ?1 AND m.metricType = ?2 AND m.date BETWEEN ?3 AND ?4")
    Double sumValueByProjectIdAndMetricTypeAndDateBetween(String projectId, MetricType metricType, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT m.contributor, SUM(m.value) FROM ContributorMetric m WHERE m.projectId = ?1 AND m.metricType = ?2 AND m.date BETWEEN ?3 AND ?4 GROUP BY m.contributor ORDER BY SUM(m.value) DESC")
    List<Object[]> findTopContributorsByProjectIdAndMetricTypeAndDateBetween(String projectId, MetricType metricType, LocalDate startDate, LocalDate endDate);
}
