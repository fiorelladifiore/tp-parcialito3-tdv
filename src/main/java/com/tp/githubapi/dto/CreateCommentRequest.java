package com.tp.githubapi.dto;

/** Body para POST /repos/{owner}/{repo}/issues/{issue_number}/comments (los PR son issues en GitHub). */
public record CreateCommentRequest(String body) {
}
