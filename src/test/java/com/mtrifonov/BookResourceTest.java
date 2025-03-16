package com.mtrifonov;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.text.MatchesPattern.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

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
    void testBookEndpointGetRequestInvalidId() {
        
        given()
            .when().get("/books/10")
                .then()
                    .statusCode(404)
                    .body(containsString("couldn't find book with id: 10"));
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
            .param("word", "вече")
            .when().get("/books/search")
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

        var titles = response.jsonPath().getList("content").stream().map(o -> (String) ((Map<String, Object>) o).get("title")).toList();
        assertThat(titles, contains("Тимур и его команда", "Чук и Гек"));
    }

    @Test
    void testBookEndpointGetByName() {

        var response = given()
            .param("pageNum", 0)
            .param("pageSize", 2)
            .param("sort", "title, asc")
            .param("word", "гай")
            .when().get("/books/search")
                .then()
                    .statusCode(200)
                    .extract().response();

        var titles = response.jsonPath().getList("content").stream().map(o -> (String) ((Map<String, Object>) o).get("title")).toList();
        assertThat(titles, contains("Вечер у Клэр", "Возвращение Будды"));
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

    @Test
    void testBookEndpointPostRequestInvalidBody() {

        given().contentType(ContentType.JSON)
            .body("{\"title\": \"Ночные дороги\"}")
                .when().post("/books/create")
                    .then()
                        .statusCode(400);
    }
}