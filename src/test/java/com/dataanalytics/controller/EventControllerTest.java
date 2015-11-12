package com.dataanalytics.controller;

import com.dataanalytics.TestApplication;
import com.jayway.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class EventControllerTest {
    @Autowired
    EventController eventController;

    @Value("${local.server.port}")
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void testGetEvents() throws Exception {
        given().param("page", 0)
                .param("size", 10)
                .when().get("/event").then().statusCode(HttpStatus.SC_OK)
                .body(any(String.class));
    }

    @Test
    public void testPostEventWithNoParameters() throws Exception {
        when().post("/api/v1/event").then().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(any(String.class));
    }

    @Test
    public void testPostEventAndRead() throws Exception {
        given().param("customerEmailHash", "abc")
                .param("employeeId", "def")
                .param("transactionId", "hij")
                .param("type", "CHECK_IN_RESERVATION_EVENT")
                .when().post("/api/v1/event").then().statusCode(HttpStatus.SC_ACCEPTED)
                .body(allOf(containsString("documentId")));
    }
}