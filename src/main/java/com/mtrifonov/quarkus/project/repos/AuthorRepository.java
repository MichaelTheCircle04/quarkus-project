package com.mtrifonov.quarkus.project.repos;

import java.util.List;

import org.jooq.Condition;

import com.mtrifonov.jooq.generated.tables.records.AuthorsRecord;
import com.mtrifonov.quarkus.project.dto.AuthorDTO;
import com.mtrifonov.quarkus.project.pagination.Pageable;

public interface AuthorRepository {
    List<AuthorsRecord> findAllWhereNameLike(String name, Pageable pageable);
    AuthorsRecord findById(int id);
    AuthorsRecord save(AuthorDTO author);
    void deleteById(int id);
    int count(Condition condition);
}
