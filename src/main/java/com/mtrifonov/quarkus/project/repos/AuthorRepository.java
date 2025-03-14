package com.mtrifonov.quarkus.project.repos;

import java.util.List;
import java.util.Optional;
import org.jooq.Condition;
import org.jooq.TableLike;

import com.mtrifonov.quarkus.project.dto.AuthorDTO;
import com.mtrifonov.quarkus.project.pagination.Pageable;

public interface AuthorRepository {
    List<AuthorDTO> findAll(Optional<? extends TableLike<?>> join, Optional<? extends Condition> cond, Optional<Pageable> pageable);
    AuthorDTO findById(int id);
    AuthorDTO save(AuthorDTO author);
    void deleteById(int id);
    int count(Optional<? extends TableLike<?>> join, Optional<? extends Condition> condition);
}
