package com.computeams.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BasicConfigurationTest {

    private TestRestTemplate restTemplate;
    private URL base;

    @Before
    public void setUp() throws MalformedURLException {
        restTemplate = new TestRestTemplate("user", "password");
        base = new URL("http://localhost:8080/api/resources/many/1");
    }

    @Test
    public void requestResourcesList_loggedUser_success() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity(base.toString(), String.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(
                Objects.requireNonNull(response.getBody())
                       .contains("id")
        );
    }

    @Test
    public void requestResourcesList_wrongCredentials_fail() {
        // given
        restTemplate = new TestRestTemplate("user", "wrongpassword");

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(base.toString(), String.class);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }


}
