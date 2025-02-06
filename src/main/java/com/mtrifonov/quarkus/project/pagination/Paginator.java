package com.mtrifonov.quarkus.project.pagination;

import java.util.ArrayList;
import java.util.List;

import org.jooq.SortField;
import org.jooq.impl.TableImpl;

public class Paginator {

    public static Pageable getPageable(PageInformation inf, TableImpl<?> table) {

        var fields = prepareFields(inf.getSort(), table);
        return Pageable.builder()
            .sort(fields)
            .pageSize(inf.getPageSize())
            .pageNum(inf.getPageNum())
            .build();
    }

    private static List<SortField<?>> prepareFields(String[] sort, TableImpl<?> table) {
        
        List<SortField<?>> fields = new ArrayList<>();
        if (sort.length == 1) {
            return fields;
        } 

        if (sort[sort.length - 1] == "asc") {
            for (int i = 0; i < sort.length - 1; i++) {
                fields.add(table.field(sort[i]).asc());
            }
        } else {
            for (int i = 0; i < sort.length - 1; i++) {
                fields.add(table.field(sort[i]).desc());
            } 
        }
        return fields;
    }
}
