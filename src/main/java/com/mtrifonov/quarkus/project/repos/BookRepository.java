package com.mtrifonov.quarkus.project.repos;

import java.util.List;
import java.util.Optional;
import org.jooq.Condition;
import com.mtrifonov.quarkus.project.dto.BookDTO;
import com.mtrifonov.quarkus.project.pagination.Pageable;

public interface BookRepository {
	List<BookDTO> findAll(Optional<? extends Condition> cond, Optional<Pageable> pageableOpt); 
	BookDTO findById(long id);
	BookDTO save(BookDTO book);
	void setPriceById(long id, int price);
	void setAmountById(long id, int amount);
	void deleteById(long id);
	long count(Optional<? extends Condition> condition);
}
