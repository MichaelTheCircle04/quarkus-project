package com.mtrifonov.quarkus.project.pagination;

import org.jboss.resteasy.reactive.RestQuery;

import jakarta.ws.rs.DefaultValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageInformation {
    
    @RestQuery
    @DefaultValue("0")
    private Integer pageNum;
    @RestQuery
    @DefaultValue("10")
    private Integer pageSize;
    @RestQuery
    @DefaultValue("unsorted")
    private String[] sort;
}
