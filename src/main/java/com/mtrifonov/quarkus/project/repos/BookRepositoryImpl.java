package com.mtrifonov.quarkus.project.repos;

import org.jooq.DSLContext;
import org.jooq.Result;

import static com.mtrifonov.jooq.generated.Tables.*;

import com.mtrifonov.jooq.generated.tables.Books;
import com.mtrifonov.jooq.generated.tables.records.BooksRecord;
import com.mtrifonov.quarkus.project.entities.BookDTO;

import org.jooq.Record;
import jakarta.inject.Singleton;
import lombok.Data;
import lombok.NoArgsConstructor;

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
		
		System.out.println(res);
		System.out.println(res.getClass());
		
		return res;
	}

	@Override
	public BooksRecord save(BookDTO book) {
		var res = create
				.insertInto(BOOKS, BOOKS.TITLE, BOOKS.PRICE, BOOKS.AMOUNT, BOOKS.AUTHOR_ID)
				.values(book.getTitle(), book.getPrice(), book.getAmount(), book.getAuthorId())
				.returning()
				.fetchOne();
		
		System.out.println(res);
		System.out.println(res.getClass());
		
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
