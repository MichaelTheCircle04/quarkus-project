package com.mtrifonov.quarkus.project.services;

import java.util.Optional;

import org.jooq.Condition;
import org.jooq.Table;
import org.jooq.TableLike;

public interface TotalElementsCacheService {
    Number getTotalElements(Table<?> table, Optional<? extends TableLike<?>> join, Optional<? extends Condition> condition);
    void cleanCachedTotalElements();
}
