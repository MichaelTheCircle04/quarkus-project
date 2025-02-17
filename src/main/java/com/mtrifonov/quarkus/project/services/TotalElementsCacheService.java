package com.mtrifonov.quarkus.project.services;

import java.util.Optional;

import org.jooq.Condition;
import org.jooq.Table;

public interface TotalElementsCacheService {
    Number getTotalElements(Table<?> table, Optional<? extends Condition> condition);
    void recalculateCachedTotalElements(Table<?> table);
}
