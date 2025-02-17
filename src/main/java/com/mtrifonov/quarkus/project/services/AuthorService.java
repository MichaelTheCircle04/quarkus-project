package com.mtrifonov.quarkus.project.services;

import static com.mtrifonov.jooq.generated.Tables.AUTHORS;

import java.util.List;
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
    private final TotalElementsCacheService cacheService;

    public AuthorService(AuthorRepository repository, TotalElementsCacheService cacheService) {
        this.repository = repository;
        this.cacheService = cacheService;
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
        var result = repository.save(author);
        cacheService.recalculateCachedTotalElements(AUTHORS);
        return result;
    } 

    public void deleteAuthorById(int id) {
        repository.deleteById(id);
        cacheService.recalculateCachedTotalElements(AUTHORS);
    }

    private Page<AuthorDTO> toPage(List<AuthorDTO> authors, Optional<? extends Condition> condition, Optional<Pageable> optionalPageable) {

        if (optionalPageable.isEmpty()) {
            return Page.<AuthorDTO>builder().content(authors).build();
        }

        var pageable = optionalPageable.get();

        int totalElements = (int) cacheService.getTotalElements(AUTHORS, condition);
        int totalPages = Paginator.getTotalPages(totalElements, pageable);
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
