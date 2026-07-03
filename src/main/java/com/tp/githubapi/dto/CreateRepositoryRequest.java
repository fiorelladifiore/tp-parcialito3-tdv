package com.tp.githubapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Body para POST /user/repos.
 * "isPrivate" se mapea a "private" en GitHub, que no es palabra reservada en JSON
 * pero si lo es en Java, por eso el nombre distinto en el record.
 */
public record CreateRepositoryRequest(
        String name,
        String description,
        @JsonProperty("private") boolean isPrivate
) {
}
