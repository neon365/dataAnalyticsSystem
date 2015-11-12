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

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=TestApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class DiagnosticControllerTest {
    @Autowired
    DiagnosticController diagnosticController;

    @Value("${local.server.port}")
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void testUptime() throws Exception {
        assertThat(diagnosticController.uptime(), is(greaterThan(1L)));
    }

    @Test
    public void textUptimeWebService() throws Exception {
        when().get("/uptime").then().statusCode(HttpStatus.SC_OK).
                body(any(String.class));
    }
}