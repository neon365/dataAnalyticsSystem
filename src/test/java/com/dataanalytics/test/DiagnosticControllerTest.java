package com.dataanalytics.test;

import com.dataanalytics.test.TestApplication;
import com.dataanalytics.controller.DiagnosticController;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=TestApplication.class)
public class DiagnosticControllerTest {
    @Autowired
    DiagnosticController diagnosticController;
    @Test
    public void testUptime() throws Exception {
        assertThat(diagnosticController.uptime(), is(greaterThan(1L)));
    }
}