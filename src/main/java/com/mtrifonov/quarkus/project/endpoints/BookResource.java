package com.mtrifonov.quarkus.project.endpoints;

import java.net.URI;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestQuery;

import com.mtrifonov.quarkus.project.dto.BookDTO;
import com.mtrifonov.quarkus.project.pagination.PageInformation;
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
	@Path("/{id}") //Covered
	public Response getBookById(long id) {
		return Response.ok(bookService.getBookById(id)).build();
	}

	@GET
	@Path("/all") //Covered
	public Response getAllBooks(PageInformation information) {
		return Response.ok(bookService.findAllBooks(Optional.of(information))).build();
	}

	@GET
	@Path("/title") //Covered
	public Response getAllBooksByTitle(@RestQuery String title, PageInformation information) {
		var result = bookService.findAllBooksWhereTitleLike(title, Optional.of(information));
		System.out.println(result);
		return Response.ok(result).build();
	}

	@GET
	@Path("/author/{id}") //Covered
	public Response getAllBooksByAuthorId(int id, PageInformation information) {
		return Response.ok(bookService.findAllBooksByAuthorId(id, Optional.of(information))).build();
	}

	@GET
	@Path("/author") //Covered
	public Response getAllBooksByAuthorName (@RestQuery String name, PageInformation information) {
		return Response.ok(bookService.findAllBooksByAuthorName(name, Optional.of(information))).build();
	}
	
	@POST
	@Path("/create") //Covered
	public Response createBook(BookDTO book) {
		var result = bookService.createBook(book);
		return Response.created(URI.create("http://" + address + "/books/" + result.getBookId())).build();
	}
	
	@PUT
	@Path("/{id}/price") //Covered 
	public Response changePrice(long id, @RestQuery int newPrice) {
		bookService.setPriceById(id, newPrice);
		return Response.ok().build();
	}
	
	@PUT
	@Path("/{id}/amount") //Covered
	public Response changeAmount(long id, @RestQuery int newAmount) {
		bookService.setAmountById(id, newAmount);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/{id}/delete") //Covered
	public Response deleteBook(long id) {
		bookService.deleteById(id);
		return Response.ok().build();
	}
}
