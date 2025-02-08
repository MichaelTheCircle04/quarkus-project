package com.mtrifonov.quarkus.project.repos;

import java.util.List;
import java.util.Optional;

import org.jooq.Condition;
import org.jooq.DSLContext;

import static com.mtrifonov.jooq.generated.Tables.*;
import com.mtrifonov.quarkus.project.dto.AuthorDTO;
import com.mtrifonov.quarkus.project.pagination.Pageable;

import jakarta.inject.Singleton;

@Singleton
public class AuthorRepositoryImpl implements AuthorRepository {

    private final DSLContext create;

    public AuthorRepositoryImpl(DSLContext create) {
        this.create = create;
    }

    @Override
    public List<AuthorDTO> findAll(Optional<Condition> conditionOpt, Optional<Pageable> pageableOpt) {
       
        if (conditionOpt.isEmpty() && pageableOpt.isEmpty()) {
            return findAll();
        } else if (conditionOpt.isEmpty() && pageableOpt.isPresent()) {
            return findAll(pageableOpt.get());
        } else if (conditionOpt.isPresent() && pageableOpt.isEmpty()) {
            return findAll(conditionOpt.get());
        } 

        var condition = conditionOpt.get();
        var pageable = pageableOpt.get();

        if (pageable.getSort().isEmpty()) {
            
            return create
                .selectFrom(AUTHORS)
                .where(condition)
                .limit(pageable.getPageSize())
                .offset(pageable.getPageSize() * pageable.getPageNum())
                .fetchInto(AuthorDTO.class);
        }
            
        return create
            .selectFrom(AUTHORS)
            .where(condition)
            .orderBy(pageable.getSort())
            .limit(pageable.getPageSize())
            .offset(pageable.getPageSize() * pageable.getPageNum())
            .fetchInto(AuthorDTO.class);
        }
    
    @Override
    public AuthorDTO findById(int id) {
        return create
            .selectFrom(AUTHORS)
            .where(AUTHORS.AUTHOR_ID.eq(id))
            .fetchOneInto(AuthorDTO.class);
    }
  
    @Override
    public AuthorDTO save(AuthorDTO author) {

        return create
            .insertInto(AUTHORS, AUTHORS.NAME)
            .values(author.getName())
            .returning().fetchOneInto(AuthorDTO.class);
    }

    @Override
    public void deleteById(int id) {
        
        create
            .delete(AUTHORS)
            .where(AUTHORS.AUTHOR_ID.eq(id))
            .execute();
    }

    @Override
    public int count(Optional<Condition> condition) {
        
        if (condition.isEmpty()) {
            return countAll();
        }

        return create
            .selectCount()
            .from(AUTHORS)
            .where(condition.get())
            .fetchOne().value1();
    }

    private List<AuthorDTO> findAll() {

        return create
            .selectFrom(AUTHORS)
            .fetchInto(AuthorDTO.class);
    }
    
    private List<AuthorDTO> findAll(Condition condition) {
    
        return create
            .selectFrom(AUTHORS)
            .where(condition)
            .fetchInto(AuthorDTO.class);
    }

    private List<AuthorDTO> findAll(Pageable pageable) {

        if (pageable.getSort().isEmpty()) {
            return create
                .selectFrom(AUTHORS)
                .limit(pageable.getPageSize())
                .offset(pageable.getPageSize() * pageable.getPageNum())
                .fetchInto(AuthorDTO.class);
        }

        return create
            .selectFrom(AUTHORS)
            .orderBy(pageable.getSort())
            .limit(pageable.getPageSize())
            .offset(pageable.getPageSize() * pageable.getPageNum())
            .fetchInto(AuthorDTO.class);
    }

    private int countAll() {

        return create.
            selectOne()
            .from(AUTHORS)
            .fetchOne().value1();
    }
}
