package com.mtrifonov.quarkus.project.endpoints;

import java.net.URI;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestQuery;

import com.mtrifonov.quarkus.project.dto.AuthorDTO;
import com.mtrifonov.quarkus.project.pagination.PageInformation;
import com.mtrifonov.quarkus.project.services.AuthorService;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;
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
    @Path("/{id}") //Covered
    public Response getAuthorById(int id) {
        return Response.ok(service.findAuthorById(id)).build();
    }
    
    @GET
    @Path("/name") //Covered
    public Response getAuthorsByName(@RestQuery String name, PageInformation information) {
        return Response.ok(service.findAuthorsByName(name, Optional.of(information))).build();
    }

    @GET
    @Path("/title")
    public Response getAuthorsByBookTitle(@RestQuery String title, PageInformation information) {
        return Response.ok(service.findAllAuthorsByBookTitle(title, Optional.of(information))).build();
    }
    
    @POST
    @Path("/create") //Covered
    public Response createAuthor(@Valid AuthorDTO author) {
        var result = service.createAuthor(author);
        return Response.created(URI.create("http://" + address + "/authors/" + result.getAuthorId())).build();
    }

    @DELETE
    @Path("/delete/{id}") //Covered
    public Response deleteAuthor(int id) {
        service.deleteAuthorById(id);
        return Response.ok().build();
    }
}
