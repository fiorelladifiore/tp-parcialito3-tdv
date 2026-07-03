package com.tp.githubapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommentSummary(
        Long id,
        String body,
        @JsonProperty("html_url") String htmlUrl
) {
}
