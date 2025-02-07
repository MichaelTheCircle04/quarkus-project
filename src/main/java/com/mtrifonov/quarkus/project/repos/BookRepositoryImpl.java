package com.mtrifonov.quarkus.project.repos;

import org.jooq.Condition;
import org.jooq.DSLContext;

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
	public BookDTO findById(long id) { //Ok

		return create
			.select(BOOKS.BOOK_ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.AMOUNT, BOOKS.AUTHOR_ID, AUTHORS.NAME)
			.from(BOOKS).join(AUTHORS).on(BOOKS.AUTHOR_ID.eq(AUTHORS.AUTHOR_ID))
			.where(BOOKS.BOOK_ID.eq(id))
			.fetchOneInto(BookDTO.class);		
	}

	@Override
	public List<BookDTO> findAll(Optional<Condition> conditionOpt, Optional<Pageable> pageableOpt) {

		if (conditionOpt.isEmpty() && pageableOpt.isEmpty()) {
			return findAll();
		} else if (conditionOpt.isEmpty()) {
			return findAll(pageableOpt.get());
		}

		var condition = conditionOpt.get();
		var pageable = pageableOpt.get();

		if (pageable.getSort().isEmpty()) {

			return create
				.select(BOOKS.BOOK_ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.AMOUNT, BOOKS.AUTHOR_ID, AUTHORS.NAME)
				.from(BOOKS).join(AUTHORS).on(BOOKS.AUTHOR_ID.eq(AUTHORS.AUTHOR_ID))
				.where(condition)
				.limit(pageable.getPageSize())
				.offset(pageable.getPageNum() * pageable.getPageSize())
				.fetchInto(BookDTO.class);
		}

		return create
			.select(BOOKS.BOOK_ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.AMOUNT, BOOKS.AUTHOR_ID, AUTHORS.NAME)
			.from(BOOKS).join(AUTHORS).on(BOOKS.AUTHOR_ID.eq(AUTHORS.AUTHOR_ID))
			.where(condition)
			.orderBy(pageable.getSort())
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
	public void setPriceById(long id, int price) { //Ok
		create.update(BOOKS).set(BOOKS.PRICE, price).execute();
	}

	@Override
	public void setAmountById(long id, int amount) { //Ok
		create.update(BOOKS).set(BOOKS.AMOUNT, amount).execute();
	}

	@Override
	public void deleteById(long id) { //Ok
		create.delete(BOOKS).where(BOOKS.BOOK_ID.eq(id)).execute();	
	}

	@Override 
	public long count(Optional<Condition> condition) { //Ok

		if (condition.isEmpty()) {
			return countAll();
		}

		return create
			.selectCount()
			.from(BOOKS)
			.where(condition.get())
			.fetchOne().value1();
	}

	private List<BookDTO> findAll() {

		return create
			.select(BOOKS.BOOK_ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.AMOUNT, BOOKS.AUTHOR_ID, AUTHORS.NAME)
			.from(BOOKS).join(AUTHORS).on(BOOKS.AUTHOR_ID.eq(AUTHORS.AUTHOR_ID))
			.fetchInto(BookDTO.class);
	}

	private List<BookDTO> findAll(Pageable pageable) {

		if (pageable.getSort().isEmpty()) {

			return create
				.select(BOOKS.BOOK_ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.AMOUNT, BOOKS.AUTHOR_ID, AUTHORS.NAME)
				.from(BOOKS).join(AUTHORS).on(BOOKS.AUTHOR_ID.eq(AUTHORS.AUTHOR_ID))
				.limit(pageable.getPageSize())
				.offset(pageable.getPageNum() * pageable.getPageSize())
				.fetchInto(BookDTO.class);
		}

		return create
			.select(BOOKS.BOOK_ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.AMOUNT, BOOKS.AUTHOR_ID, AUTHORS.NAME)
			.from(BOOKS).join(AUTHORS).on(BOOKS.AUTHOR_ID.eq(AUTHORS.AUTHOR_ID))
			.orderBy(pageable.getSort())
			.limit(pageable.getPageSize())
			.offset(pageable.getPageNum() * pageable.getPageSize())
			.fetchInto(BookDTO.class);
	}

	private long countAll() {
		return create
			.selectCount()
			.from(BOOKS)
			.fetchOne().value1();
	}
}
