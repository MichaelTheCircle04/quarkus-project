package com.mtrifonov.quarkus.project.repos;

import java.util.List;

import com.mtrifonov.jooq.generated.tables.records.BooksRecord;
import com.mtrifonov.quarkus.project.dto.BookDTO;
import com.mtrifonov.quarkus.project.pagination.Pageable;

public interface BookRepository {
	BookDTO findById(long id);
	List<BookDTO> findAll(Pageable pageable);
	BooksRecord save(BookDTO book);
	void setPriceById(long id, int price);
	void setAmountById(long id, int amount);
	void deleteById(long id);
}
