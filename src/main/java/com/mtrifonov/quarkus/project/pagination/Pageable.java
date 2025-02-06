package com.mtrifonov.quarkus.project.pagination;

import java.util.List;

import org.jooq.SortField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pageable {

    private int pageSize;
    private int pageNum;
    private List<SortField<?>> sort;

}
