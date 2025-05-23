package com.openelements.opendata.metrics;

/**
 * Defines the types of metrics that can be tracked in the system.
 */
public enum MetricType {
    // Pull Request metrics
    PR_CREATED,
    PR_MERGED,
    PR_CLOSED,
    PR_REVIEWED,
    PR_COMMENTS,
    PR_ADDITIONS,
    PR_DELETIONS,
    PR_CHANGED_FILES,
    
    // Issue metrics
    ISSUE_CREATED,
    ISSUE_CLOSED,
    ISSUE_REOPENED,
    ISSUE_COMMENTS,
    ISSUE_ASSIGNED,
    ISSUE_LABELED,
    
    // Contribution metrics
    COMMIT_COUNT,
    CODE_REVIEW_COUNT,
    COMMENT_COUNT,
    
    // Repository metrics
    FORK_COUNT,
    STAR_COUNT,
    WATCH_COUNT,
    
    // Time-based metrics
    TIME_TO_FIRST_RESPONSE,
    TIME_TO_MERGE,
    TIME_TO_CLOSE,
    
    // Quality metrics
    TEST_COVERAGE,
    BUILD_SUCCESS_RATE,
    CODE_QUALITY_SCORE
}
