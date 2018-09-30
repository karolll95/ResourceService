package com.computeams.controllers;

import com.computeams.models.ResourceDto;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MimeTypeUtils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ResourceControllerTest {

    private static final int PORT = 8080;
    private static final String BASE_PATH = "/api/resources";
    private static final String BASE_HOST = "http://localhost";


    @BeforeClass
    public static void setup() {
        RestAssured.port = PORT;
        RestAssured.basePath = BASE_PATH;
        RestAssured.baseURI = BASE_HOST;
    }

    @Test
    public void getResourceById_verifyConnection_Correct() {
        given().when()
               .get("/1")
               .then()
               .statusCode(200);
    }

    @Test
    public void getResourceById_verifyConnection_Wrong() {
        given().when()
               .get("1000")
               .then()
               .statusCode(500);
    }

    @Test
    public void getResourcesByIds_verifyJsonKeyValue_Correct() {
        assertEquals(1, given().when().get("/many/{id}", 1)
                               .then().statusCode(200)
                               .extract()
                               .body().as(ResourceDto[].class)[0].getId());
    }

    @Test
    public void addResource_verifyJsonKeyValue_Correct() {
        ResourceDto resourceDto = new ResourceDto();
        resourceDto.setId(5);
        resourceDto.setResourceUrl("http://www.google.pl");
        resourceDto.setName("Google");

        given().contentType(MimeTypeUtils.APPLICATION_JSON_VALUE)
               .body(resourceDto)
               .when()
               .post()
               .then()
               .body("name", equalTo("Google"));
    }

    @Test
    public void findResourceByName_verifyJsonKeyValue_Correct() {
        String query = "hus";
        assertThat(given().when()
                          .get("?search=" + query)
                          .then()
                          .extract()
                          .body()
                          .as(ResourceDto[].class)[0].getName(), containsString(query));
    }
}
