package com.openelements.opendata.issues;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends JpaRepository<Issue, String> {

    Optional<Issue> findByOrgAndRepositoryAndGitHubId(String org, String repository, long gitHubId);
    
    List<Issue> findByOrgAndRepository(String org, String repository);
    
    List<Issue> findByOrgAndRepositoryAndOpen(String org, String repository, boolean open);
    
    List<Issue> findByAuthor(String author);
    
    List<Issue> findByAssignee(String assignee);
    
    @Query("SELECT i FROM Issue i JOIN i.labels l WHERE l = ?1")
    List<Issue> findByLabel(String label);
    
    List<Issue> findByCreatedAtInGitHubBetween(ZonedDateTime start, ZonedDateTime end);
    
    @Query("SELECT i FROM Issue i WHERE i.org = ?1 AND i.repository = ?2 AND ?3 MEMBER OF i.labels")
    List<Issue> findByOrgAndRepositoryAndLabel(String org, String repository, String label);
    
    @Query("SELECT COUNT(i) FROM Issue i WHERE i.org = ?1 AND i.repository = ?2 AND i.open = ?3")
    long countByOrgAndRepositoryAndOpen(String org, String repository, boolean open);
}
