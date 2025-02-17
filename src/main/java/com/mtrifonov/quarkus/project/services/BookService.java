package com.mtrifonov.quarkus.project.services;

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

@Singleton
public class BookService {
	
	private final BookRepository bookRepo;
	private final AuthorService authorService;
	private final TotalElementsCacheService cacheService;
	
	public BookService(BookRepository bookRepo, AuthorService authorService, TotalElementsCacheService cacheService) {
		this.bookRepo = bookRepo;
		this.authorService = authorService;
		this.cacheService = cacheService;
	}
	
	public BookDTO getBookById(long id) {
		return bookRepo.findById(id);
	}

    public Page<BookDTO> findAllBooks(Optional<PageInformation> information) {

		var pageable = Paginator.getPageable(information, BOOKS);
		var content = bookRepo.findAll(Optional.empty(), pageable);

		return toPage(content, Optional.empty(), pageable);
    }

	public Page<BookDTO> findAllBooksWhereTitleLike(String title, Optional<PageInformation> information) {

		var condition = Optional.of(BOOKS.TITLE.likeIgnoreCase("%" + title + "%"));
		var pageable = Paginator.getPageable(information, BOOKS);
		var content = bookRepo.findAll(condition, pageable);

		return toPage(content, condition, pageable);
	}

	
	public Page<BookDTO> findAllBooksByAuthorId(int id, Optional<PageInformation> information) {
		
		var condition = Optional.of(BOOKS.AUTHOR_ID.eq(id));
		var pageable = Paginator.getPageable(information, BOOKS);
		var content = bookRepo.findAll(condition, pageable);
		
		return toPage(content, condition, pageable);
	}

	
	public Page<BookDTO> findAllBooksByAuthorName(String name, Optional<PageInformation> information) {

		var authorsIds = authorService
			.findAuthorsByName(name, Optional.empty())
			.getContent().stream()
			.map(a -> a.getAuthorId())
			.toList();

		var condition = Optional.of(BOOKS.AUTHOR_ID.in(authorsIds));
		var pageable = Paginator.getPageable(information, BOOKS);
		var content = bookRepo.findAll(condition, pageable);
		
		return toPage(content, condition, pageable);
	}
	
	public BookDTO createBook(BookDTO book) {
		var result = bookRepo.save(book);
		cacheService.recalculateCachedTotalElements(BOOKS);
		return result;
	}
	
	public void setPriceById(long id, int price) {
		bookRepo.setPriceById(id, price);
		cacheService.recalculateCachedTotalElements(BOOKS);
	}
	
	public void setAmountById(long id, int amount) {
		bookRepo.setAmountById(id, amount);
		cacheService.recalculateCachedTotalElements(BOOKS);
	}

	public void deleteById(long id) {
		bookRepo.deleteById(id);
		cacheService.recalculateCachedTotalElements(BOOKS);
	}

	private Page<BookDTO> toPage (List<BookDTO> books, Optional<? extends Condition> condition, Optional<Pageable> optionalPageable) {

		if (optionalPageable.isEmpty()) {
			return Page.<BookDTO>builder().content(books).build();
		}

		var pageable = optionalPageable.get();

		long totalElements = (long) cacheService.getTotalElements(BOOKS, condition);
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
