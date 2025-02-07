package com.mtrifonov.quarkus.project.repos;

import java.util.List;
import java.util.Optional;

import org.jooq.Condition;

import com.mtrifonov.quarkus.project.dto.AuthorDTO;
import com.mtrifonov.quarkus.project.pagination.Pageable;

public interface AuthorRepository {
    List<AuthorDTO> findAll(Optional<Condition> condition, Optional<Pageable> pageable); //condition and pageable can be empty
    AuthorDTO findById(int id);
    AuthorDTO save(AuthorDTO author);
    void deleteById(int id);
    int count(Optional<Condition> condition);
}
