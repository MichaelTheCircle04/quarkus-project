package com.mtrifonov.quarkus.project.endpoints;

import java.net.URI;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestQuery;

import com.mtrifonov.quarkus.project.dto.AuthorDTO;
import com.mtrifonov.quarkus.project.pagination.PageInformation;
import com.mtrifonov.quarkus.project.services.AuthorService;

import jakarta.inject.Singleton;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Singleton
@Path("/authors")
public class AuthorResource {

    private final String address;
    private final AuthorService service;
    
    public AuthorResource(AuthorService service, @ConfigProperty(name = "server.address") String address) {
        this.service = service;
        this.address = address;
    }

    @GET
    @Path("/{id}")
    public Response getAuthorById(int id) {
        return Response.ok(service.findAuthorById(id)).build();
    }
    
    @GET
    @Path("/name")
    public Response getAuthorsWhereNameLike(@RestQuery String name, PageInformation information) {
        return Response.ok(service.findAuthorsByName(name, information)).build();
    }
    
    @POST
    @Path("/create")
    public Response createAuthor(AuthorDTO author) {
        var result = service.createAuthor(author);
        return Response.created(URI.create("http://" + address + "/authors/" + result.getAuthorId())).build();
    }

    @DELETE
    @Path("/delete/{id}")
    public Response deleteAuthor(int id) {
        service.deleteAuthorById(id);
        return Response.ok().build();
    }
}
