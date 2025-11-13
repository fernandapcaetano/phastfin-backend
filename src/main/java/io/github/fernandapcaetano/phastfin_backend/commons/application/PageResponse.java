package io.github.fernandapcaetano.phastfin_backend.commons.application;

public record PageResponse<T>(
        T elements,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLast,
        boolean isFirst
) { }
