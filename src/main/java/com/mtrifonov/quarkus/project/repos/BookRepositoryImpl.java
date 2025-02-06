package com.mtrifonov.quarkus.project.repos;

import org.jooq.DSLContext;

import static com.mtrifonov.jooq.generated.Tables.*;

import java.util.List;
import java.util.stream.Collectors;

import com.mtrifonov.jooq.generated.tables.records.BooksRecord;
import com.mtrifonov.quarkus.project.dto.BookDTO;
import com.mtrifonov.quarkus.project.pagination.Page;
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
		var res = create
				.select(BOOKS.BOOK_ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.AMOUNT, BOOKS.AUTHOR_ID, AUTHORS.NAME)
				.from(BOOKS).join(AUTHORS).on(BOOKS.AUTHOR_ID.eq(AUTHORS.AUTHOR_ID))
				.where(BOOKS.BOOK_ID.eq(id))
				.fetchOneInto(BookDTO.class);
		
		return res;
	}

	@Override
	public List<BooksRecord> findAll(Pageable pageable) {
		return create
			.selectFrom(BOOKS)
			.orderBy(pageable.getSort())
			.limit(pageable.getPageSize())
			.offset(pageable.getPageNum() + pageable.getPageSize())
			.fetch().collect(Collectors.toList());
	}

	@Override
	public BooksRecord save(BookDTO book) {
		var res = create
				.insertInto(BOOKS, BOOKS.TITLE, BOOKS.PRICE, BOOKS.AMOUNT, BOOKS.AUTHOR_ID)
				.values(book.getTitle(), book.getPrice(), book.getAmount(), book.getAuthorId())
				.returning()
				.fetchOne();	

		return res;
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
	public void deleteById(long id) {
		create.delete(BOOKS).where(BOOKS.BOOK_ID.eq(id)).execute();	
	}
}
