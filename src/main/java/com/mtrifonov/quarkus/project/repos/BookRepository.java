package com.mtrifonov.quarkus.project.repos;

import java.util.List;
import java.util.Optional;

import org.jooq.Condition;

import com.mtrifonov.quarkus.project.dto.BookDTO;
import com.mtrifonov.quarkus.project.pagination.Pageable;

public interface BookRepository {
	BookDTO findById(long id);
	List<BookDTO> findAll(Optional<Condition> condition, Optional<Pageable> pageable); 
	BookDTO save(BookDTO book);
	void setPriceById(long id, int price);
	void setAmountById(long id, int amount);
	void deleteById(long id);
	long count(Optional<Condition> condition);
}
