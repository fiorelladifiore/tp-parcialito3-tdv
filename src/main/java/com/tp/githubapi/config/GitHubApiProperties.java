package com.tp.githubapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "github.api")
public record GitHubApiProperties(String baseUrl, String version, String token) {
}
