package com.adrielle.corefinancas.dtos;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PagedResponseDTO<T>(
    List<T> content,
    long totalElements,
    int totalPages,
    int page,
    int size
) {
    public static <E, D> PagedResponseDTO<D> from(Page<E> page, Function<E, D> mapper) {
        return new PagedResponseDTO<>(
            page.getContent().stream().map(mapper).toList(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.getNumber(),
            page.getSize()
        );
    }
}
