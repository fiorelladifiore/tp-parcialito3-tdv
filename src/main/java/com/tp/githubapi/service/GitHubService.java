package com.tp.githubapi.service;

import com.tp.githubapi.dto.CommentSummary;
import com.tp.githubapi.dto.CreateCommentRequest;
import com.tp.githubapi.dto.CreatePullRequestRequest;
import com.tp.githubapi.dto.CreateRepositoryRequest;
import com.tp.githubapi.dto.PullRequestSummary;
import com.tp.githubapi.dto.RepositorySummary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Encapsula las llamadas a la API REST oficial de GitHub usadas en el TP:
 * 2) listado de repos propios, 3) creacion de repo, 4) creacion de PR, 5) comentario en PR.
 * La autenticacion (Bearer token) ya viene resuelta en el RestClient inyectado (ver GitHubClientConfig).
 */
@Service
public class GitHubService {

    private final RestClient gitHubRestClient;

    public GitHubService(RestClient gitHubRestClient) {
        this.gitHubRestClient = gitHubRestClient;
    }

    /** GET /user/repos - repositorios del usuario autenticado. */
    public List<RepositorySummary> listMyRepositories() {
        return gitHubRestClient.get()
                .uri("/user/repos?per_page=100&sort=created&direction=desc")
                .retrieve()
                .body(new ParameterizedTypeReference<List<RepositorySummary>>() {
                });
    }

    /** POST /user/repos - crea un repositorio nuevo para el usuario autenticado. */
    public RepositorySummary createRepository(CreateRepositoryRequest request) {
        return gitHubRestClient.post()
                .uri("/user/repos")
                .body(request)
                .retrieve()
                .body(RepositorySummary.class);
    }

    /** POST /repos/{owner}/{repo}/pulls - crea un Pull Request entre una rama nueva y main. */
    public PullRequestSummary createPullRequest(String owner, String repo, CreatePullRequestRequest request) {
        return gitHubRestClient.post()
                .uri("/repos/{owner}/{repo}/pulls", owner, repo)
                .body(request)
                .retrieve()
                .body(PullRequestSummary.class);
    }

    /** POST /repos/{owner}/{repo}/issues/{issue_number}/comments - comenta un PR existente. */
    public CommentSummary addCommentToPullRequest(String owner, String repo, long pullNumber,
                                                    CreateCommentRequest request) {
        return gitHubRestClient.post()
                .uri("/repos/{owner}/{repo}/issues/{issue_number}/comments", owner, repo, pullNumber)
                .body(request)
                .retrieve()
                .body(CommentSummary.class);
    }
}
