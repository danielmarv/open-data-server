package com.openelements.opendata.issues;

import com.openelements.opendata.base.db.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Entity
public class Issue extends AbstractEntity {

    @Column(nullable = false)
    private String org;

    @Column(nullable = false)
    private String repository;

    @Column(nullable = false)
    private long gitHubId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String body;

    @Column(nullable = false)
    private boolean open;

    @Column(nullable = false)
    private String author;

    @Column(nullable = true)
    private String assignee;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> labels = new HashSet<>();

    @Column(nullable = false)
    private int commentCount;

    @Column(nullable = false)
    private ZonedDateTime createdAtInGitHub;

    @Column(nullable = false)
    private ZonedDateTime lastUpdateInGitHub;

    @Column(nullable = true)
    private ZonedDateTime closedAtInGitHub;

    public String getOrg() {
        return org;
    }

    public void setOrg(@NonNull final String org) {
        this.org = org;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(@NonNull final String repository) {
        this.repository = repository;
    }

    public long getGitHubId() {
        return gitHubId;
    }

    public void setGitHubId(final long gitHubId) {
        this.gitHubId = gitHubId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull final String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(@NonNull final String body) {
        this.body = body;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(final boolean open) {
        this.open = open;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(@NonNull final String author) {
        this.author = author;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(@Nullable final String assignee) {
        this.assignee = assignee;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public void setLabels(@NonNull final Set<String> labels) {
        this.labels = labels;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(final int commentCount) {
        this.commentCount = commentCount;
    }

    public ZonedDateTime getCreatedAtInGitHub() {
        return createdAtInGitHub;
    }

    public void setCreatedAtInGitHub(@NonNull final ZonedDateTime createdAtInGitHub) {
        this.createdAtInGitHub = createdAtInGitHub;
    }

    public ZonedDateTime getLastUpdateInGitHub() {
        return lastUpdateInGitHub;
    }

    public void setLastUpdateInGitHub(@NonNull final ZonedDateTime lastUpdateInGitHub) {
        this.lastUpdateInGitHub = lastUpdateInGitHub;
    }

    public ZonedDateTime getClosedAtInGitHub() {
        return closedAtInGitHub;
    }

    public void setClosedAtInGitHub(@Nullable final ZonedDateTime closedAtInGitHub) {
        this.closedAtInGitHub = closedAtInGitHub;
    }
}
