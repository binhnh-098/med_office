package com.example.med_office.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PagedResponse<T> {

    private final List<T> items;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public PagedResponse(
            List<T> items,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean hasNext,
            boolean hasPrevious
    ) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }


}
