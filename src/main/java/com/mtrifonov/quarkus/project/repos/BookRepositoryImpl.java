package com.mtrifonov.quarkus.project.repos;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Select;

import static com.mtrifonov.jooq.generated.Tables.*;

import java.util.List;
import java.util.Optional;

import com.mtrifonov.quarkus.project.dto.BookDTO;
import com.mtrifonov.quarkus.project.pagination.Pageable;

import jakarta.inject.Singleton;
import lombok.Data;

@Singleton
@Data
public class BookRepositoryImpl implements BookRepository {
	
	private DSLContext create;
	
	public BookRepositoryImpl(DSLContext create) {
		this.create = create;
	}

	@Override
	public BookDTO findById(long id) { 

		return create
			.select(BOOKS.BOOK_ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.AMOUNT, BOOKS.AUTHOR_ID, AUTHORS.NAME)
			.from(BOOKS).join(AUTHORS).on(BOOKS.AUTHOR_ID.eq(AUTHORS.AUTHOR_ID))
			.where(BOOKS.BOOK_ID.eq(id))
			.fetchOneInto(BookDTO.class);		
	}

	@Override
	public List<BookDTO> findAll(Optional<? extends Condition> conditionOpt, Optional<Pageable> pageableOpt) {

		var select = create
				.select(BOOKS.BOOK_ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.AMOUNT, BOOKS.AUTHOR_ID, AUTHORS.NAME)
				.from(BOOKS)
				.join(AUTHORS).on(BOOKS.AUTHOR_ID.eq(AUTHORS.AUTHOR_ID));
		
		if (!conditionOpt.isEmpty()) {
			select.where(conditionOpt.get());
		}
		
		if (pageableOpt.isEmpty()) {
			return select.fetchInto(BookDTO.class);
		}
		
		var pageable = pageableOpt.get();

		if (!pageable.getSort().isEmpty()) {
			select.orderBy(pageable.getSort());
		}
		
		return select
			.limit(pageable.getPageSize())
			.offset(pageable.getPageNum() * pageable.getPageSize())
			.fetchInto(BookDTO.class);
	}

	@Override
	public BookDTO save(BookDTO book) {

		return create
			.insertInto(BOOKS, BOOKS.TITLE, BOOKS.PRICE, BOOKS.AMOUNT, BOOKS.AUTHOR_ID)
			.values(book.getTitle(), book.getPrice(), book.getAmount(), book.getAuthorId())
			.returning().fetchOneInto(BookDTO.class);	
	}

	@Override
	public void setPriceById(long id, int price) { 
		create.update(BOOKS).set(BOOKS.PRICE, price).execute();
	}

	@Override
	public void setAmountById(long id, int amount) {
		create.update(BOOKS).set(BOOKS.AMOUNT, amount).execute();
	}

	@Override
	public void deleteById(long id) { //Ok
		create.delete(BOOKS).where(BOOKS.BOOK_ID.eq(id)).execute();	
	}

	@Override 
	public long count(Optional<? extends Condition> condition) {
		
		var select = create.selectCount().from(BOOKS);

		if (!condition.isEmpty()) {
			select.where(condition.get());
		}

		return select.fetchOne().value1();
	}
}
