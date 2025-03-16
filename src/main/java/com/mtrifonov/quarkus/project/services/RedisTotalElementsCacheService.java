package com.mtrifonov.quarkus.project.services;

import static com.mtrifonov.jooq.generated.Tables.*;
import java.util.Map;
import java.util.Optional;
import org.jooq.Condition;
import org.jooq.Table;
import org.jooq.TableLike;
import com.mtrifonov.quarkus.project.repos.AuthorRepository;
import com.mtrifonov.quarkus.project.repos.BookRepository;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.hash.HashCommands;
import jakarta.inject.Singleton;

@Singleton
@IfBuildProfile("redis")
public class RedisTotalElementsCacheService implements TotalElementsCacheService {

    private final HashCommands<String, String, Number> conditionCache;
    private final BookRepository bookRepo;
    private final AuthorRepository authorRepo;
    
    public RedisTotalElementsCacheService(RedisDataSource redis, BookRepository bookRepo, AuthorRepository authorRepo) {
        this.conditionCache = redis.hash(String.class, String.class, Number.class);
        this.bookRepo = bookRepo;
        this.authorRepo = authorRepo;
    }

    @Override
    public Number getTotalElements(Table<?> table, Optional<? extends TableLike<?>> join, Optional<? extends Condition> condition) {

        if (table == AUTHORS) {
            return getAuthorsTotalElements(join, condition);
        } else if (table == BOOKS) {
            return getBooksTotalElements(condition); 
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void cleanCachedTotalElements() {
        conditionCache.hdel("TOTAL_ELEMENTS");
    }

    private Integer getAuthorsTotalElements(Optional<? extends TableLike<?>> join, Optional<? extends Condition> condition) {

        Integer totalElements = (Integer) getTotalElements(condition);

        if (totalElements == null) {
            totalElements = authorRepo.count(join, condition);
            putTotalElements(condition, totalElements);
        }

        return totalElements;
    }

    private Long getBooksTotalElements(Optional<? extends Condition> condition) {

        Long totalElements = (Long) this.getTotalElements(condition);

        if (totalElements == null) {
			totalElements = bookRepo.count(condition);
			putTotalElements(condition, totalElements);
        }

        return totalElements;
    }

    private Number getTotalElements(Optional<? extends Condition> condition) {
        return conditionCache.hget("TOTAL_ELEMENTS", condition.isEmpty() ? "" : condition.get().toString());
    }

    private void putTotalElements(Optional<? extends Condition> condition, Number newCount) {
        String value = condition.isEmpty() ? "" : condition.get().toString();
        conditionCache.hset("TOTAL_ELEMENTS", Map.of(value, newCount));
    }
}
