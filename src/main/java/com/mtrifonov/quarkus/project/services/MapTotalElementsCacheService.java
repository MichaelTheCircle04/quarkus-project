package com.mtrifonov.quarkus.project.services;

import static com.mtrifonov.jooq.generated.Tables.AUTHORS;
import static com.mtrifonov.jooq.generated.Tables.BOOKS;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jooq.Condition;
import org.jooq.Table;
import org.jooq.TableLike;

import com.mtrifonov.quarkus.project.repos.AuthorRepository;
import com.mtrifonov.quarkus.project.repos.BookRepository;

import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.inject.Singleton;

@Singleton
@IfBuildProfile("map")
public class MapTotalElementsCacheService implements TotalElementsCacheService {

    private final Map<Optional<? extends Condition>, Integer> authorsMap = new HashMap<>();
    private final Map<Optional<? extends Condition>, Long> booksMap = new HashMap<>();
    private final AuthorRepository authorsRepo;
    private final BookRepository booksRepo;

    public MapTotalElementsCacheService(AuthorRepository authorsRepo, BookRepository booksRepo) {
        this.authorsRepo = authorsRepo;
        this.booksRepo = booksRepo;
    }
    
    @Override
    public Number getTotalElements(Table<?> table, Optional<? extends TableLike<?>> join, Optional<? extends Condition> condition) {
        
        if (table == AUTHORS) {
            return getTotalElementsFromAuthors(join, condition);
        } else if (table == BOOKS) {
            return getTotalElementsFromBooks(condition);
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

    private Integer getTotalElementsFromAuthors(Optional<? extends TableLike<?>> join, Optional<? extends Condition> condition) {

        Integer total = authorsMap.get(condition);

        if (total == null) {
            total = authorsRepo.count(join, condition);
            authorsMap.put(condition, total);
        }

        return total;
    }

    private Long getTotalElementsFromBooks(Optional<? extends Condition> condition) {

        Long total = booksMap.get(condition);

        if (total == null) {
            total = booksRepo.count(condition);
            booksMap.put(condition, total);
        }

        return total;
    }

    private void cleanAuthorsCachedTotalElements() {
        authorsMap.clear();
    }

    private void cleanBooksCachedTotalElements() {
        booksMap.clear();
    }
}
