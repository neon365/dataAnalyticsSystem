package com.dataanalytics;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.web.WebAppConfiguration;


@Configuration
@EnableAutoConfiguration
@WebAppConfiguration
@ComponentScan(basePackages = {"com.apple.retail.odyssey.controller", "com.apple.retail.odyssey.service"})
public class TestApplication {
}
