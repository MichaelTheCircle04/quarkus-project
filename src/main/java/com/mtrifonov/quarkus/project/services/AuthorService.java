package com.mtrifonov.quarkus.project.services;

import static com.mtrifonov.jooq.generated.Tables.AUTHORS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.SortField;
import org.jooq.impl.TableImpl;

import com.mtrifonov.jooq.generated.tables.records.AuthorsRecord;
import com.mtrifonov.quarkus.project.dto.AuthorDTO;
import com.mtrifonov.quarkus.project.pagination.Page;
import com.mtrifonov.quarkus.project.pagination.PageInformation;
import com.mtrifonov.quarkus.project.pagination.Pageable;
import com.mtrifonov.quarkus.project.repos.AuthorRepository;

import jakarta.inject.Singleton;

@Singleton
public class AuthorService {

    private final AuthorRepository repository;
    private final Map<String, Integer> elementsCount = new HashMap<>();

    public AuthorService(AuthorRepository repository) {
        this.repository = repository;
    }

    public Page<AuthorDTO> findAuthorsByName(String name, PageInformation information) {

        var fields = prepareFields(information.getSort(), AUTHORS);
        var pageable = Pageable.builder()
            .sort(fields)
            .pageSize(information.getPageSize())
            .pageNum(information.getPageNum())
            .build();

        var dto = repository.findAllWhereNameLike(name, pageable).stream().map(this::toDto).toList();
        return toPage(dto, name, pageable);
    };

    
    public AuthorDTO findAuthorById(int id) {
        return toDto(repository.findById(id));
    }

    
    public AuthorsRecord createAuthor(AuthorDTO author) {
        return repository.save(author);
    } 

    public void deleteAuthorById(int id) {
        repository.deleteById(id);
    }

    private AuthorDTO toDto(AuthorsRecord record) {

        return AuthorDTO.builder()
            .authorId(record.getAuthorId())
            .name(record.getName())
            .build();
    }

    private List<SortField<?>> prepareFields(String[] sort, TableImpl<?> table) {
        
        List<SortField<?>> fields = new ArrayList<>();
        if (sort.length == 1) {
            return fields;
        } 

        if (sort[sort.length - 1] == "asc") {
            for (int i = 0; i < sort.length - 1; i++) {
                fields.add(table.field(sort[i]).asc());
            }
        } else {
            for (int i = 0; i < sort.length - 1; i++) {
                fields.add(table.field(sort[i]).desc());
            } 
        }

        return fields;
    }

    private Page<AuthorDTO> toPage(List<AuthorDTO> authors, String name, Pageable pageable) {

        int totalElements;
        
        if (elementsCount.containsKey(name)) {
            totalElements = elementsCount.get(name);
        } else {
            totalElements = repository.count((Condition) AUTHORS.NAME.likeIgnoreCase("%" + name + "%"));
            elementsCount.put(name, totalElements);
        }

        int totalPages = totalElements / pageable.getPageSize();
        boolean nextPage = pageable.getPageNum() < totalPages;
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
