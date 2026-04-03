package com.adrielle.corefinancas.dtos;

import java.time.OffsetDateTime;
import java.util.List;

public record ErrorResponseDTO(
    OffsetDateTime timestamp,
    int status,
    String message,
    List<String> errors
) {
    public ErrorResponseDTO(int status, String message) {
        this(OffsetDateTime.now(), status, message, null);
    }

    public ErrorResponseDTO(int status, String message, List<String> errors) {
        this(OffsetDateTime.now(), status, message, errors);
    }
}
