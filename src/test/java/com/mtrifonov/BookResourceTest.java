package com.mtrifonov;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.text.MatchesPattern.*;

import org.hamcrest.CoreMatchers;


@QuarkusTest
class BookResourceTest {
    
    @Test
    void testBookEndpointGetRequest() {
   	
    	given()
            .when().get("/books/1")
                .then()
                    .statusCode(200)
                    .body(containsString("Вечер у Клэр"));
    }

    @Test
    void testBookEndpointPutRequest() {

        given()
            .param("newPrice", 800)
                .when().put("/books/1/price")
                    .then()
                        .statusCode(200);
        
        given()
            .param("newAmount", 10)
                .when().put("/books/1/amount")
                    .then()
                        .statusCode(200);
        
        given()
            .when().get("/books/1")
                .then()
                    .body("price", CoreMatchers.equalTo(800))
                    .and()
                    .body("amount", CoreMatchers.equalTo(10));
    }

    @Test
    void testBookEndpointPostRequest() {

        given().contentType(ContentType.JSON)
            .body("{\"title\" : \"Ночные дороги\", \"price\" : 440, \"amount\" : 7, \"authorId\" : 1}")
        	    .when().post("/books/create")
        		    .then()
        			    .statusCode(201)
                        .header("location", matchesPattern("^(https?:\\/\\/[a-zA-Z0-9.-]+(:\\d+)?\\/books\\/\\d+)$"));
    }
}