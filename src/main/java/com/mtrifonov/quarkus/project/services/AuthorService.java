package com.mtrifonov.quarkus.project.services;

import static com.mtrifonov.jooq.generated.Tables.AUTHORS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jooq.Condition;

import com.mtrifonov.quarkus.project.dto.AuthorDTO;
import com.mtrifonov.quarkus.project.pagination.Page;
import com.mtrifonov.quarkus.project.pagination.PageInformation;
import com.mtrifonov.quarkus.project.pagination.Pageable;
import com.mtrifonov.quarkus.project.pagination.Paginator;
import com.mtrifonov.quarkus.project.repos.AuthorRepository;

import jakarta.inject.Singleton;

@Singleton
public class AuthorService {

    private final AuthorRepository repository;
    private final Map<Optional<? extends Condition>, Integer> elementsCount = new HashMap<>();

    public AuthorService(AuthorRepository repository) {
        this.repository = repository;
    }

    public Page<AuthorDTO> findAuthorsByName(String name, Optional<PageInformation> information) {
      
        var condition = Optional.of(AUTHORS.NAME.likeIgnoreCase("%" + name + "%"));
        var pageable = Paginator.getPageable(information, AUTHORS);
        var authors = repository.findAll(condition, pageable);

        return toPage(authors, condition, pageable);
    };

    
    public AuthorDTO findAuthorById(int id) {
        return repository.findById(id);
    }

    
    public AuthorDTO createAuthor(AuthorDTO author) {
        return repository.save(author);
    } 

    public void deleteAuthorById(int id) {
        repository.deleteById(id);
    }

    private Page<AuthorDTO> toPage(List<AuthorDTO> authors, Optional<? extends Condition> condition, Optional<Pageable> optionalPageable) {

        if (optionalPageable.isEmpty()) {
            return Page.<AuthorDTO>builder().content(authors).build();
        }

        var pageable = optionalPageable.get();

        int totalElements;
        
        if (elementsCount.containsKey(condition)) {
            totalElements = elementsCount.get(condition);
        } else {
            totalElements = repository.count(condition);
            elementsCount.put(condition, totalElements);
        }

        int totalPages;
		int remainder = totalElements % pageable.getPageSize();

		if (totalElements < pageable.getPageSize()) {
			totalPages = 1;
	  	} else if (remainder == 0) {
			totalPages = totalElements / pageable.getPageSize();
		} else {
			totalPages = (totalElements - remainder) / pageable.getPageSize();
			totalPages += 1;
		}

        boolean nextPage = pageable.getPageNum() < totalPages - 1;
        boolean prevPage = pageable.getPageNum() > 0;

        return Page.<AuthorDTO>builder()
            .totalPages(totalPages)
            .totalElements(totalElements)
            .pageSize(pageable.getPageSize())
            .pageNum(pageable.getPageNum())
            .nextPage(nextPage)
            .prevPage(prevPage)
            .content(authors)
            .build();
    }
}
