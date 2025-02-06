package com.mtrifonov.quarkus.project.endpoints;

import java.net.URI;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestQuery;

import com.mtrifonov.quarkus.project.dto.BookDTO;
import com.mtrifonov.quarkus.project.pagination.Pageable;
import com.mtrifonov.quarkus.project.services.BookService;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;


@Path("/books")
public class BookResource {
	
	private final String address;
	private final BookService bookService;

	public BookResource(@ConfigProperty(name = "server.address") String address, BookService bookService) {
		this.address = address;
		this.bookService = bookService;
	}
	
	@GET
	@Path("/{id}")
	public Response getBookById(long id) {
		var book = bookService.getBookById(id);
		return Response.ok(book).build();
	}

	@GET
	@Path("/all")
	public Response getAllBooks(Pageable pageable) {
		return Response.ok(bookService.findAllBooks(pageable)).build();
	}
	
	@POST
	@Path("/create")
	public Response createBook(BookDTO book) {
		var result = bookService.createBook(book);
		return Response.created(URI.create("http://" + address + "/books/" + result.getBookId())).build();
	}
	
	@PUT
	@Path("/{id}/price")
	public Response changePrice(long id, @RestQuery int newPrice) {
		bookService.setPriceById(id, newPrice);
		return Response.ok().build();
	}
	
	@PUT
	@Path("/{id}/amount")
	public Response changeAmount(long id, @RestQuery int newAmount) {
		bookService.setAmountById(id, newAmount);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/{id}/delete")
	public Response deleteBook(long id) {
		bookService.deleteById(id);
		return Response.ok().build();
	}
}
