package com.tp.githubapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Proyeccion minima de un repositorio devuelto por GET /user/repos.
 * GitHub no expone "visibility" como boolean sino como string ("public"/"private"),
 * pero tambien devuelve el campo legado "private" (boolean); igualmente lo mapeamos.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RepositorySummary(
        String name,
        String visibility,
        @JsonProperty("html_url") String htmlUrl
) {
}
