package com.mtrifonov;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import com.mtrifonov.quarkus.project.entities.BookDTO;
import com.mtrifonov.quarkus.project.repos.BookRepository;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.text.MatchesPattern.*;

@QuarkusTest
class GreetingResourceTest {
	
	@Inject
	BookRepository repo;
	
    @Test
    void testHelloEndpoint() {
        given()
          .when().get("/hello")
          .then()
             .statusCode(200)
             .body(is("Hello from Quarkus REST"));
        
        given().body("{\"title\": \"Ночные дороги\", \"price\": 440, \"amount\": 7, \"authorId\": 1}")
        	.when().post("/books/create")
        		.then()
        			.statusCode(201)
        			.body(matchesPattern(("^(https?:\\/\\/[a-zA-Z0-9.-]+(:\\d+)?\\/task\\/management\\/system\\/\\d+)$")));
    }
    
    @Test
    void testBookEndpoint() {
    	
    	var res = repo.findById(1);
    	
    	//var book = BookDTO.builder().title("Возвращение Будды").price(500).amount(5).authorId(1).build();
    	/*var dto = repo.save(book);
    	System.out.println(dto);*/
    	
    	
    	//given().when().get("/book")
    }

}