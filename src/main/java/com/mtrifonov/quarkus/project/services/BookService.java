package com.mtrifonov.quarkus.project.services;

import static com.mtrifonov.jooq.generated.Tables.BOOKS;

import com.mtrifonov.jooq.generated.tables.records.BooksRecord;
import com.mtrifonov.quarkus.project.dto.BookDTO;
import com.mtrifonov.quarkus.project.pagination.Page;
import com.mtrifonov.quarkus.project.pagination.PageInformation;
import com.mtrifonov.quarkus.project.pagination.Paginator;
import com.mtrifonov.quarkus.project.repos.BookRepository;

import jakarta.inject.Singleton;

@Singleton
public class BookService {
	
	private final BookRepository bookRepo;
	
	public BookService(BookRepository bookRepo) {
		this.bookRepo = bookRepo;
	}
	
	public BookDTO getBookById(long id) {
		return bookRepo.findById(id);
	}

    public Page<BookDTO> findAllBooks(PageInformation information) {

		var pageable = Paginator.getPageable(information, BOOKS);
		return toPage(bookRepo.findAll(pageable).stream().map(this::toDto));
    }
	
	public BooksRecord createBook(BookDTO book) {
		return bookRepo.save(book);
	}
	
	public void setPriceById(long id, int price) {
		bookRepo.setPriceById(id, price);
	}
	
	public void setAmountById(long id, int amount) {
		bookRepo.setAmountById(id, amount);
	}

	public void deleteById(long id) {
		bookRepo.deleteById(id);
	}

	private BookDTO toDto() {
		return nyll; 
	}
}
