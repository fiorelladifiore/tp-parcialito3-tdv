package com.tp.githubapi.dto;

/**
 * Body para POST /repos/{owner}/{repo}/pulls.
 * head: rama origen con los cambios (ej: "feature/nueva-funcionalidad").
 * base: rama destino (ej: "main").
 */
public record CreatePullRequestRequest(
        String title,
        String body,
        String head,
        String base
) {
}
