package com.mtrifonov.quarkus.project.services;

import static com.mtrifonov.jooq.generated.Tables.AUTHORS;
import static com.mtrifonov.jooq.generated.Tables.BOOKS;
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

    private final HashCommands<String, String, Integer> authorsCommands;
    private final HashCommands<String, String, Long> booksCommands;
    private final BookRepository bookRepo;
    private final AuthorRepository authorRepo;
    
    public RedisTotalElementsCacheService(RedisDataSource redis, BookRepository bookRepo, AuthorRepository authorRepo) {
        this.authorsCommands = redis.hash(String.class, String.class, Integer.class);
        this.booksCommands = redis.hash(String.class, String.class, Long.class);
        this.bookRepo = bookRepo;
        this.authorRepo = authorRepo;
    }

    //Public API methods
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
    public void cleanCachedTotalElements(Table<?> table) {

        if (table == AUTHORS) {
            cleanAuthorsCachedTotalElements();
        } else if (table == BOOKS) {
            cleanBooksCachedTotalElements();
        } else {
            throw new IllegalArgumentException();
        }
    }

    //Cleaners
    private void cleanAuthorsCachedTotalElements() {
        authorsCommands.hdel("AUTHORS");
    }

    private void cleanBooksCachedTotalElements() {
        authorsCommands.hdel("BOOKS");
    }

    //Aggregators
    private Integer getAuthorsTotalElements(Optional<? extends TableLike<?>> join, Optional<? extends Condition> condition) {

        Integer totalElements = getTotalElementsFromAuthors(condition);

        if (totalElements == null) {
            totalElements = authorRepo.count(join, condition);
            putTotalElementsToAuthors(condition, totalElements);
        }

        return totalElements;
    }

    private Long getBooksTotalElements(Optional<? extends Condition> condition) {

        Long totalElements = this.getTotalElementsFromBooks(condition);

        if (totalElements == null) {
			totalElements = bookRepo.count(condition);
			putTotalElementsToBooks(condition, totalElements);
        }

        return totalElements;
    }

    //Main low-level methods
    private Integer getTotalElementsFromAuthors(Optional<? extends Condition> condition) {
        return authorsCommands.hget("AUTHORS", condition.isEmpty() ? "" : condition.get().toString());
    }

    private void putTotalElementsToAuthors(Optional<? extends Condition> condition, int newCount) {
        String value = condition.isEmpty() ? "" : condition.get().toString();
        authorsCommands.hset("AUTHORS", Map.of(value, newCount));
    }

    private Long getTotalElementsFromBooks(Optional<? extends Condition> condition) {
        return booksCommands.hget("BOOKS", condition.isEmpty() ? "" : condition.get().toString());
    }

    private void putTotalElementsToBooks(Optional<? extends Condition> condition, long newCount) {
        String value = condition.isEmpty() ? "" : condition.get().toString();
        booksCommands.hset("AUTHORS", Map.of(value, newCount));
    }
}
