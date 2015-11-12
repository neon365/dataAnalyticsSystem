package com.dataanalytics.security;

import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public Filter getSecuritFilter () {
        SecurityFilter securityFilter = new SecurityFilter();
        
        return securityFilter;
    }

}