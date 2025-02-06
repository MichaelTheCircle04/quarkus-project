package com.mtrifonov.quarkus.project.repos;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.DSLContext;

import static com.mtrifonov.jooq.generated.Tables.*;
import com.mtrifonov.jooq.generated.tables.records.AuthorsRecord;
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
    public List<AuthorsRecord> findAllWhereNameLike(String name, Pageable pageable) {

        if (pageable.getSort().size() == 0) {
            return findAllWhereNameLikeUnsorted(name, pageable);
        }

        return create
            .selectFrom(AUTHORS)
            .where(AUTHORS.NAME.likeIgnoreCase("%" + name + "%"))
            .orderBy(pageable.getSort())
            .limit(pageable.getPageSize())
            .offset(pageable.getPageSize() * pageable.getPageNum())
            .fetch().collect(Collectors.toList());
    }
    
    @Override
    public AuthorsRecord findById(int id) {
        return create
            .selectFrom(AUTHORS)
            .where(AUTHORS.AUTHOR_ID.eq(id))
            .fetchOne();
    }
  
    @Override
    public AuthorsRecord save(AuthorDTO author) {

        return create
            .insertInto(AUTHORS, AUTHORS.NAME)
            .values(author.getName())
            .returning().fetchOne();
    }

    @Override
    public void deleteById(int id) {
        
        create
            .delete(AUTHORS)
            .where(AUTHORS.AUTHOR_ID.eq(id))
            .execute();
    }

    @Override
    public int count(Condition condition) {
        
        if (condition == null) {
            return countAll();
        }

        return create
            .selectCount()
            .from(AUTHORS)
            .where(condition)
            .fetchOne().value1();
    }

    private List<AuthorsRecord> findAllWhereNameLikeUnsorted(String name, Pageable pageable) {

        return create
            .selectFrom(AUTHORS)
            .where(AUTHORS.NAME.likeIgnoreCase("%" + name + "%"))
            .limit(pageable.getPageSize())
            .offset(pageable.getPageSize() * pageable.getPageNum())
            .fetch().collect(Collectors.toList());
    }

    private int countAll() {

        return create.
            selectOne()
            .from(AUTHORS)
            .fetchOne().value1();
    }
}
