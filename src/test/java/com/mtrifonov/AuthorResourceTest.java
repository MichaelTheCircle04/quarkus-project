package com.mtrifonov;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.text.MatchesPattern.*;
import org.junit.jupiter.api.Test;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;


@QuarkusTest
public class AuthorResourceTest {

    @Test
    void testAuthorEndpointGetRequest() {

        given()
            .when().get("authors/1")
                .then()
                    .statusCode(200)
                    .body("name", is("Гайто Газданов"));
    }

    @Test
    void testAuthorEndpointGetRequestInvalidId() {

        given()
            .when().get("authors/4")
                .then()
                    .statusCode(404)
                    .body(containsString("couldn't find author with id: 4"));
    }

    @Test
    void testAuthorEndpointGetByName() {

        var response = given()
            .param("pageNum", 0)
            .param("pageSize", 1)
            .param("sort", "author_id, name, asc")
            .param("name", "гай")
                .when().get("/authors/name")
                    .then()
                        .statusCode(200)
                        .extract().response();

        assertTrue(response.jsonPath().getString("content[0].name").equals("Гайто Газданов"));
        assertTrue(response.jsonPath().getInt("totalPages") == 2);
        assertTrue(response.jsonPath().getInt("totalElements") == 2);
        assertTrue(response.jsonPath().getBoolean("nextPage") == true);
        assertTrue(response.jsonPath().getBoolean("prevPage") == false);
    }


    @Test
    void testAuthorEndpointGetByTitle() {

        given()
            .param("pageNum", 0)
            .param("pageSize", 1)
            .param("title", "вечер")
                .when().get("/authors/title")
                    .then()
                        .statusCode(200)
                        .body("content[0].name", is("Гайто Газданов"));
    }

    @Test
    @TestTransaction
    void testAuthorEndpointPostRequest() {

        given().contentType(ContentType.JSON)
            .body("{\"name\": \"Федор Достоевский\"}")
                .when().post("/authors/create")
                    .then()
                        .statusCode(201)
                        .header("location", matchesPattern("^(https?:\\/\\/[a-zA-Z0-9.-]+(:\\d+)?\\/authors\\/\\d+)$"));
    }

    @Test
    @TestTransaction
    void testAuthorEndpointPostRequestInvalidBody() {

        given().contentType(ContentType.JSON)
            .body("{\"nname\": \"Федор Достоевский\"}")
                .when().post("/authors/create")
                    .then()
                        .statusCode(400).extract().response();
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
