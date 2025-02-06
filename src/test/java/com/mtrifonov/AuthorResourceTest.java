package com.mtrifonov;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.CoreMatchers.*;
//import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.text.MatchesPattern.*;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class AuthorResourceTest {

    @Test
    void testAuthorEndpointPostRequest() {

        given().contentType(ContentType.JSON)
            .body("{\"name\": \"Федор Достоевский\"}")
                .when().post("/authors/create")
                    .then()
                        .statusCode(201)
                        .header("location", matchesPattern("^(https?:\\/\\/[a-zA-Z0-9.-]+(:\\d+)?\\/authors\\/\\d+)$"));;
    }

    @Test
    void testAuthorEndpointGetRequest() {

        given()
            .when().get("authors/1")
                .then()
                    .statusCode(200)
                    .body("name", is("Гайто Газданов"));
    }

    @Test
    void testAuthorEndpointGetPageRequest() {

        var response = given()
            .param("pageNum", 0)
            .param("pageSize", 1)
            .param("sort", "author_id, asc")
            .param("name", "гай")
                .when().get("/authors/name")
                    .then()
                        .statusCode(200)
                        .extract().response();

        //assertTrue(response.jsonPath().getInt("authorId[0]") == 1);
        //assertTrue(response.jsonPath().getInt("authorId[1]") == 2);
    }

    @Test
    void testAuthorEndpointDeleteRequest() {

        var response = given().contentType(ContentType.JSON)
            .body("{\"name\": \"Жан Жене\"}")
                .when().post("/authors/create")
                    .then()
                        .extract()
                        .response();

        String location = response.getHeader("location");

        given()
            .when().delete("/authors/delete/" + location.charAt(location.length() - 1))
                .then()
                    .statusCode(200);
    }
}
