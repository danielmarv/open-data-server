package com.openelements.opendata.metrics;

import com.openelements.opendata.base.Language;
import com.openelements.opendata.base.db.AbstractEntityBasedService;
import com.openelements.opendata.project.ProjectService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContributorMetricService extends AbstractEntityBasedService<ContributorMetricDTO, ContributorMetric> {

    @PersistenceContext
    private EntityManager entityManager;
    
    private final ContributorMetricRepository metricRepository;
    private final ProjectService projectService;

    public ContributorMetricService(ContributorMetricRepository metricRepository, ProjectService projectService) {
        super(ContributorMetricMapper.class, ContributorMetric.class);
        this.metricRepository = Objects.requireNonNull(metricRepository);
        this.projectService = Objects.requireNonNull(projectService);
    }

    @NonNull
    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
    
    @Transactional
    public ContributorMetricDTO recordMetric(String contributor, String org, String repository, 
                                           MetricType metricType, double value) {
        ContributorMetric metric = new ContributorMetric();
        metric.setContributor(contributor);
        metric.setOrg(org);
        metric.setRepository(repository);
        metric.setMetricType(metricType);
        metric.setValue(value);
        metric.setDate(LocalDate.now());
        metric.setTimestamp(ZonedDateTime.now());
        
        // Try to find a matching project
        String projectId = findProjectIdForRepository(org, repository);
        if (projectId != null) {
            metric.setProjectId(projectId);
        }
        
        ContributorMetric savedMetric = metricRepository.save(metric);
        return getMapper().entityToDto(savedMetric);
    }
    
    private String findProjectIdForRepository(String org, String repository) {
        String repoFullName = org + "/" + repository;
        return projectService.getAll(Language.EN).stream()
                .filter(project -> project.matchingRepos().contains(repoFullName))
                .map(project -> project.uuid())
                .findFirst()
                .orElse(null);
    }
    
    public List<ContributorMetricDTO> getMetricsByProject(String projectId) {
        return metricRepository.findByProjectId(projectId).stream()
                .map(getMapper()::entityToDto)
                .collect(Collectors.toList());
    }
    
    public List<ContributorMetricDTO> getMetricsByProjectAndPeriod(String projectId, LocalDate startDate, LocalDate endDate) {
        return metricRepository.findByProjectIdAndDateBetween(projectId, startDate, endDate).stream()
                .map(getMapper()::entityToDto)
                .collect(Collectors.toList());
    }
    
    public Map<MetricType, Double> getProjectMetricSummary(String projectId, LocalDate startDate, LocalDate endDate) {
        return metricRepository.findByProjectIdAndDateBetween(projectId, startDate, endDate).stream()
                .collect(Collectors.groupingBy(
                        ContributorMetric::getMetricType,
                        Collectors.summingDouble(ContributorMetric::getValue)
                ));
    }
    
    public List<ContributorSummary> getTopContributors(String projectId, MetricType metricType, LocalDate startDate, LocalDate endDate, int limit) {
        List<Object[]> results = metricRepository.findTopContributorsByProjectIdAndMetricTypeAndDateBetween(
                projectId, metricType, startDate, endDate);
        
        return results.stream()
                .limit(limit)
                .map(result -> new ContributorSummary((String) result[0], ((Number) result[1]).doubleValue()))
                .collect(Collectors.toList());
    }
}
