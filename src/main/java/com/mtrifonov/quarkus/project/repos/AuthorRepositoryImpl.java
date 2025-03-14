package com.mtrifonov.quarkus.project.repos;

import java.util.List;
import java.util.Optional;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.TableLike;
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
    public List<AuthorDTO> findAll(Optional<? extends TableLike<?>> join, Optional<? extends Condition> cond, Optional<Pageable> pageableOpt) {
        
        var select = create.select(AUTHORS.AUTHOR_ID, AUTHORS.NAME);
        
        if (!join.isEmpty()) {
            select.from(join.get());
        } else {
            select.from(AUTHORS);
        }

        if (!cond.isEmpty()) {
        	select.where(cond.get());
        }
        
        if (pageableOpt.isEmpty()) {
        	return select.fetchInto(AuthorDTO.class);
        }
        
        var pageable = pageableOpt.get();
        
        if (!pageable.getSort().isEmpty()) {
            select.orderBy(pageable.getSort());
        }
        
        return select
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
    public int count(Optional<? extends TableLike<?>> join, Optional<? extends Condition> condition) {
        
    	var select = create.selectCount();
    	
        if (!join.isEmpty()) {
            select.from(join.get());
        } else {
            select.from(AUTHORS);
        }

        if (!condition.isEmpty()) {
            select.where(condition.get());
        }

        return select.fetchOne().value1();
    }
}
