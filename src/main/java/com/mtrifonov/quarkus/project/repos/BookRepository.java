package com.mtrifonov.quarkus.project.repos;

import com.mtrifonov.jooq.generated.tables.records.BooksRecord;
import com.mtrifonov.quarkus.project.entities.BookDTO;

import org.jooq.Record;


//import com.mtrifonov.quarkus.study.entities.Book;

public interface BookRepository {
	BookDTO findById(long id);
	BooksRecord save(BookDTO book);
	void setPriceById(long id, int price);
	void setAmountById(long id, int amount);
	void deleteById(long id);
}
