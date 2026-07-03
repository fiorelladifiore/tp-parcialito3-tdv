package com.tp.githubapi.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

/** Propaga el status y el body de error tal cual los devuelve GitHub, en vez de un 500 generico. */
@RestControllerAdvice
public class GitHubErrorHandler {

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<String> handleGitHubError(RestClientResponseException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .headers(HttpHeaders.EMPTY)
                .body(ex.getResponseBodyAsString());
    }
}
