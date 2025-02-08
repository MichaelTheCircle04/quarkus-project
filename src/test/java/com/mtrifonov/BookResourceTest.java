package com.mtrifonov;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.text.MatchesPattern.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void testBookEndpointGetAllRequest() {

        var response = given()
            .param("pageNum", 0)
            .param("pageSize", 2)
            .param("sort", "author_id, title, asc")
            .when().get("/books/all")
                .then()
                    .statusCode(200)
                    .extract().response();

        assertTrue(response.jsonPath().getString("content[0].title").equals("Вечер у Клэр"));
        assertTrue(response.jsonPath().getString("content[1].title").equals("Возвращение Будды"));
    }

    @Test
    void testBookEndpointGetByTitle() {

        var response = given()
            .param("pageNum", 0)
            .param("pageSize", 2)
            .param("sort", "author_id, asc")
            .param("title", "вече")
            .when().get("/books/title")
                .then()
                    .statusCode(200)
                    .extract().response();

        assertTrue(response.jsonPath().getString("content[0].title").equals("Вечер у Клэр"));
        assertTrue(response.jsonPath().getInt("totalPages") == 1);
        assertTrue(response.jsonPath().getInt("totalElements") == 1);
        assertTrue(response.jsonPath().getBoolean("nextPage") == false);
        assertTrue(response.jsonPath().getBoolean("prevPage") == false);
    }

    @Test
    void testBookEndpointGetByAuthorId() {

        var response = given()
            .param("pageNum", 0)
            .param("pageSize", 2)
            .param("sort", "book_id, asc")
            .when().get("/books/author/2")
                .then()
                    .statusCode(200)
                    .extract().response();

        assertTrue(response.jsonPath().getString("content[0].title").equals("Тимур и его команда"));
        assertTrue(response.jsonPath().getString("content[1].title").equals("Чук и Гек"));
    }

    @Test
    void testBookEndpointGetByName() {

        var response = given()
            .param("pageNum", 0)
            .param("pageSize", 2)
            .param("sort", "title, asc")
            .param("name", "гай")
            .when().get("/books/author")
                .then()
                    .statusCode(200)
                    .extract().response();

        assertTrue(response.jsonPath().getString("content[0].title").equals("Вечер у Клэр"));
        assertTrue(response.jsonPath().getString("content[1].title").equals("Возвращение Будды"));
    }

    @Test
    @TestTransaction
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
    @TestTransaction
    void testBookEndpointPostRequest() {

        given().contentType(ContentType.JSON)
            .body("{\"title\" : \"Ночные дороги\", \"price\" : 440, \"amount\" : 7, \"authorId\" : 1}")
        	    .when().post("/books/create")
        		    .then()
        			    .statusCode(201)
                        .header("location", matchesPattern("^(https?:\\/\\/[a-zA-Z0-9.-]+(:\\d+)?\\/books\\/\\d+)$"));
    }
}