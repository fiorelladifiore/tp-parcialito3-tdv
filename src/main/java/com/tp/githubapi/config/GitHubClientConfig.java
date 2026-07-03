package com.tp.githubapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP hacia la API REST de GitHub.
 * El Bearer token se inyecta desde GitHubApiProperties, que a su vez lo lee
 * de la variable de entorno GITHUB_TOKEN (ver application.properties). Nunca
 * se hardcodea en el codigo fuente.
 */
@Configuration
public class GitHubClientConfig {

    @Bean
    public RestClient gitHubRestClient(GitHubApiProperties properties) {
        if (properties.token() == null || properties.token().isBlank()) {
            throw new IllegalStateException(
                    "GITHUB_TOKEN no esta definido. Exportalo como variable de entorno antes de levantar la app.");
        }
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.token())
                .defaultHeader("X-GitHub-Api-Version", properties.version())
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
