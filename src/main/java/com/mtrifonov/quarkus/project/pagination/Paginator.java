package com.mtrifonov.quarkus.project.pagination;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jooq.SortField;
import org.jooq.impl.TableImpl;

public class Paginator {

    public static Optional<Pageable> getPageable(Optional<PageInformation> optInf, TableImpl<?> table) {

        if (optInf.isEmpty()) {
            return Optional.empty();
        }

        var inf = optInf.get();

        var fields = prepareFields(inf.getSort(), table);
        return Optional.of(Pageable.builder()
            .sort(fields)
            .pageSize(inf.getPageSize())
            .pageNum(inf.getPageNum())
            .build());
    }

    public static int getTotalPages(Number totalElements, Pageable pageable) {

        if (totalElements instanceof Integer) {
            return getTotalPagesIfInt((int) totalElements, pageable);
        } else {
            return getTotalPagesIfLong((long) totalElements, pageable);
        }
    }

    private static int getTotalPagesIfInt(int totalElements, Pageable pageable) {

        int totalPages;
		int remainder = totalElements % pageable.getPageSize();

		if (totalElements < pageable.getPageSize()) {
			totalPages = 1;
	  	} else if (remainder == 0) {
			totalPages = totalElements / pageable.getPageSize();
		} else {
			totalPages = (totalElements - remainder) / pageable.getPageSize();
			totalPages += 1;
		}

        return totalPages;
    }

    private static int getTotalPagesIfLong(long totalElements, Pageable pageable) {

        int totalPages;
		int remainder = (int) totalElements % pageable.getPageSize();

		if (totalElements < pageable.getPageSize()) {
			totalPages = 1;
	  	} else if (remainder == 0) {
			totalPages = (int) totalElements / pageable.getPageSize();
		} else {
			totalPages = (int) (totalElements - remainder) / pageable.getPageSize();
			totalPages += 1;
		}

        return totalPages;
    }

    private static List<SortField<?>> prepareFields(String[] sort, TableImpl<?> table) {
        
        List<SortField<?>> fields = new ArrayList<>();
        
        if (sort.length == 1) {
            return fields;
        } 
        
        if (sort[sort.length - 1].equals("asc")) {
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
