package com.mtrifonov.quarkus.project.endpoints;

import java.net.URI;

import com.mtrifonov.quarkus.project.entities.BookDTO;
import com.mtrifonov.quarkus.project.repos.BookRepository;
import com.mtrifonov.quarkus.project.services.BookService;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;

@Path("/books")
@AllArgsConstructor
public class BookResource {
	
	private final String address = "localhost:8080";
	private final BookRepository repo;
	private final BookService bookService;
	
	@GET
	@Path("/{id}")
	public Response bookById(long id) {
		var book = repo.findById(id);
		return Response.ok(book).build();
	}
	
	@POST
	@Path("/create")
	public Response createBook(BookDTO book) {
		var result = repo.save(book);
		return Response.created(URI.create("http://" + address + "/books/" + result.getBookId())).build();
	}
	
	@PUT
	@Path("/{id}/price")
	public Response changePrice(long id, int newPrice) {
		bookService.setPriceById(id, newPrice);
		return Response.ok().build();
	}
	
	@PUT
	@Path("/{id}/amount")
	public Response changeAmount(long id, int value) {
		bookService.setAmountById(id, value);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/{id}/delete")
	public Response deleteBook(long id) {
		bookService.deleteById(id);
		return Response.ok().build();
	}
}
