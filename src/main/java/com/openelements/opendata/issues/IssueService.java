package com.openelements.opendata.issues;

import com.openelements.opendata.base.Language;
import com.openelements.opendata.base.db.AbstractEntityBasedService;
import com.openelements.opendata.metrics.ContributorMetricService;
import com.openelements.opendata.metrics.MetricType;
import com.openelements.opendata.project.ProjectService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IssueService extends AbstractEntityBasedService<IssueDTO, Issue> {

    @PersistenceContext
    private EntityManager entityManager;
    
    private final IssueRepository issueRepository;
    private final ProjectService projectService;
    private final ContributorMetricService metricService;

    public IssueService(IssueRepository issueRepository, 
                       ProjectService projectService,
                       ContributorMetricService metricService) {
        super(IssueMapper.class, Issue.class);
        this.issueRepository = Objects.requireNonNull(issueRepository);
        this.projectService = Objects.requireNonNull(projectService);
        this.metricService = Objects.requireNonNull(metricService);
    }

    @NonNull
    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
    
    @Transactional
    public IssueDTO createOrUpdateIssue(String org, String repository, long gitHubId, String title, 
                                      String body, boolean open, String author, String assignee,
                                      Set<String> labels, int commentCount, 
                                      ZonedDateTime createdAt, ZonedDateTime updatedAt,
                                      ZonedDateTime closedAt) {
        
        Optional<Issue> existingIssue = issueRepository.findByOrgAndRepositoryAndGitHubId(org, repository, gitHubId);
        
        Issue issue;
        boolean isNew = false;
        
        if (existingIssue.isPresent()) {
            issue = existingIssue.get();

            if (issue.isOpen() && !open) {
                metricService.recordMetric(author, org, repository, MetricType.ISSUE_CLOSED, 1);
            }

            if (issue.getCommentCount() < commentCount) {
                int newComments = commentCount - issue.getCommentCount();
                metricService.recordMetric(author, org, repository, MetricType.ISSUE_COMMENTS, newComments);
            }
        } else {
            issue = new Issue();
            issue.setOrg(org);
            issue.setRepository(repository);
            issue.setGitHubId(gitHubId);
            issue.setCreatedAtInGitHub(createdAt);
            
            isNew = true;
        }
        
        issue.setTitle(title);
        issue.setBody(body);
        issue.setOpen(open);
        issue.setAuthor(author);
        issue.setAssignee(assignee);
        issue.setLabels(labels);
        issue.setCommentCount(commentCount);
        issue.setLastUpdateInGitHub(updatedAt);
        issue.setClosedAtInGitHub(closedAt);
        
        Issue savedIssue = issueRepository.save(issue);

        if (isNew) {
            metricService.recordMetric(author, org, repository, MetricType.ISSUE_CREATED, 1);
        }
        
        return getMapper().entityToDto(savedIssue);
    }
    
    public List<IssueDTO> getIssuesByRepository(String org, String repository) {
        return issueRepository.findByOrgAndRepository(org, repository).stream()
                .map(getMapper()::entityToDto)
                .collect(Collectors.toList());
    }
    
    public List<IssueDTO> getOpenIssuesByRepository(String org, String repository) {
        return issueRepository.findByOrgAndRepositoryAndOpen(org, repository, true).stream()
                .map(getMapper()::entityToDto)
                .collect(Collectors.toList());
    }
    
    public List<IssueDTO> getIssuesByAuthor(String author) {
        return issueRepository.findByAuthor(author).stream()
                .map(getMapper()::entityToDto)
                .collect(Collectors.toList());
    }
    
    public List<IssueDTO> getIssuesByAssignee(String assignee) {
        return issueRepository.findByAssignee(assignee).stream()
                .map(getMapper()::entityToDto)
                .collect(Collectors.toList());
    }
    
    public List<IssueDTO> getIssuesByLabel(String label) {
        return issueRepository.findByLabel(label).stream()
                .map(getMapper()::entityToDto)
                .collect(Collectors.toList());
    }
    
    public List<IssueDTO> getIssuesByRepositoryAndLabel(String org, String repository, String label) {
        return issueRepository.findByOrgAndRepositoryAndLabel(org, repository, label).stream()
                .map(getMapper()::entityToDto)
                .collect(Collectors.toList());
    }
    
    public long countOpenIssuesByRepository(String org, String repository) {
        return issueRepository.countByOrgAndRepositoryAndOpen(org, repository, true);
    }
    
    public long countClosedIssuesByRepository(String org, String repository) {
        return issueRepository.countByOrgAndRepositoryAndOpen(org, repository, false);
    }
    
    public List<IssueDTO> getIssuesByProject(String projectId) {
        String projectPrefix = "Project-";
        if (!projectId.startsWith(projectPrefix)) {
            projectId = projectPrefix + projectId;
        }
        
        final String finalProjectId = projectId;
        Set<String> repositories = projectService.getAll(Language.EN).stream()
                .filter(project -> project.uuid().equals(finalProjectId))
                .flatMap(project -> project.matchingRepos().stream())
                .collect(Collectors.toSet());
        
        return repositories.stream()
                .map(repo -> {
                    String[] parts = repo.split("/");
                    if (parts.length == 2) {
                        return issueRepository.findByOrgAndRepository(parts[0], parts[1]);
                    }
                    return List.<Issue>of();
                })
                .flatMap(List::stream)
                .map(getMapper()::entityToDto)
                .collect(Collectors.toList());
    }
}
