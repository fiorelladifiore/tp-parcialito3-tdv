package com.tp.githubapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PullRequestSummary(
        Long number,
        String title,
        String state,
        @JsonProperty("html_url") String htmlUrl
) {
}
