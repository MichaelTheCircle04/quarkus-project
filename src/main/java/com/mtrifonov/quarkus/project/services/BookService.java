package com.mtrifonov.quarkus.project.services;

import com.mtrifonov.jooq.generated.tables.records.BooksRecord;
import com.mtrifonov.quarkus.project.entities.BookDTO;
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

}
