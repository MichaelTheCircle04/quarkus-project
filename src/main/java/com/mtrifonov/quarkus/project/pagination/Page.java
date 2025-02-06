package com.mtrifonov.quarkus.project.pagination;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> {

    private int totalPages;
    private long totalElements;
    private int pageSize;
    private int pageNum;
    private boolean nextPage;
    private boolean prevPage;
    private List<T> content;
}
