package com.mtrifonov.quarkus.project.services;

import static com.mtrifonov.jooq.generated.Tables.AUTHORS;
import static com.mtrifonov.jooq.generated.Tables.BOOKS;

import java.util.List;
import java.util.Optional;

import org.jooq.Condition;

import com.mtrifonov.quarkus.project.dto.BookDTO;
import com.mtrifonov.quarkus.project.pagination.Page;
import com.mtrifonov.quarkus.project.pagination.PageInformation;
import com.mtrifonov.quarkus.project.pagination.Pageable;
import com.mtrifonov.quarkus.project.pagination.Paginator;
import com.mtrifonov.quarkus.project.repos.BookRepository;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@Singleton
@Transactional
public class BookService {
	
	private final BookRepository repository;
	private final TotalElementsCacheService cacheService;
	
	public BookService(BookRepository repository, TotalElementsCacheService cacheService) {
		this.repository = repository;
		this.cacheService = cacheService;
	}
	
	public BookDTO getBookById(long id) {

		var book = repository.findById(id);

		if (book == null) {
			throw new NotFoundException("couldn't find book with id: " + id);
		}

		return book;
	}

    public Page<BookDTO> findAllBooks(Optional<PageInformation> information) {

		var pageable = Paginator.getPageable(information, BOOKS);
		var content = repository.findAll(Optional.empty(), pageable);

		return toPage(content, Optional.empty(), pageable);
    }

	public Page<BookDTO> findAllByString(String word, Optional<PageInformation> information) {

		var condition = Optional.of(
			BOOKS.TITLE.likeIgnoreCase("%" + word + "%")
			.or(AUTHORS.NAME.likeIgnoreCase("%" + word + "%"))
			);

		var pageable = Paginator.getPageable(information, BOOKS);
		var content = repository.findAll(condition, pageable);
		
		return toPage(content, condition, pageable);
	}
	
	public Page<BookDTO> findAllBooksByAuthorId(int id, Optional<PageInformation> information) {
		
		var condition = Optional.of(BOOKS.AUTHOR_ID.eq(id));
		var pageable = Paginator.getPageable(information, BOOKS);
		var content = repository.findAll(condition, pageable);
		
		return toPage(content, condition, pageable);
	}
	
	//Колличество не важно для филтров которые будет использовать пользователь, но постоянно будет меняться, посему можно пренебречь в данном случае очисткой кэша
	public void setAmountById(long id, int amount) {
		repository.setAmountById(id, amount);
	}

	//Методы изменяющие дынные, после их применения кэш сбрасывается
	public BookDTO createBook(BookDTO book) {
		var result = repository.save(book);
		cacheService.cleanCachedTotalElements();
		return result;
	}

	public void setPriceById(long id, int price) {
		repository.setPriceById(id, price);
		cacheService.cleanCachedTotalElements();
	}

	public void deleteById(long id) {
		repository.deleteById(id);
		cacheService.cleanCachedTotalElements();
	}

	private Page<BookDTO> toPage (List<BookDTO> books, Optional<? extends Condition> condition, Optional<Pageable> optionalPageable) {

		if (optionalPageable.isEmpty()) {
			return Page.<BookDTO>builder().content(books).build();
		}

		var pageable = optionalPageable.get();

		long totalElements = (long) cacheService.getTotalElements(BOOKS, Optional.empty(), condition);
		int totalPages = Paginator.getTotalPages(totalElements, pageable);
		boolean nextPage = pageable.getPageNum() < totalPages - 1;
		boolean prevPage = pageable.getPageNum() > 0;

		return Page.<BookDTO>builder()
			.totalPages(totalPages)
			.totalElements(totalElements)
			.pageSize(pageable.getPageSize())
			.pageNum(pageable.getPageNum())
			.nextPage(nextPage)
			.prevPage(prevPage)
			.content(books)
			.build();
	}
}
