package com.tp.githubapi.controller;

import com.tp.githubapi.dto.CommentSummary;
import com.tp.githubapi.dto.CreateCommentRequest;
import com.tp.githubapi.dto.CreatePullRequestRequest;
import com.tp.githubapi.dto.CreateRepositoryRequest;
import com.tp.githubapi.dto.PullRequestSummary;
import com.tp.githubapi.dto.RepositorySummary;
import com.tp.githubapi.service.GitHubService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints propios que envuelven las llamadas a la API de GitHub para poder
 * disparar cada inciso del TP con curl/Postman. Los endpoints DOCUMENTADOS en
 * openapi/openapi.yaml son los de GitHub (api.github.com), no estos.
 */
@RestController
@RequestMapping("/api")
public class GitHubController {

    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    /** Inciso 2: listado de repositorios propios. */
    @GetMapping("/repos")
    public List<RepositorySummary> listMyRepositories() {
        return gitHubService.listMyRepositories();
    }

    /** Inciso 3: creacion de un repositorio. */
    @PostMapping("/repos")
    @ResponseStatus(HttpStatus.CREATED)
    public RepositorySummary createRepository(@RequestBody CreateRepositoryRequest request) {
        return gitHubService.createRepository(request);
    }

    /** Inciso 4: creacion de un Pull Request entre una rama nueva y main. */
    @PostMapping("/repos/{owner}/{repo}/pulls")
    @ResponseStatus(HttpStatus.CREATED)
    public PullRequestSummary createPullRequest(@PathVariable String owner,
                                                 @PathVariable String repo,
                                                 @RequestBody CreatePullRequestRequest request) {
        return gitHubService.createPullRequest(owner, repo, request);
    }

    /** Inciso 5: comentario en un Pull Request existente. */
    @PostMapping("/repos/{owner}/{repo}/pulls/{pullNumber}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentSummary addCommentToPullRequest(@PathVariable String owner,
                                                   @PathVariable String repo,
                                                   @PathVariable long pullNumber,
                                                   @RequestBody CreateCommentRequest request) {
        return gitHubService.addCommentToPullRequest(owner, repo, pullNumber, request);
    }
}
