package com.dataanalytics;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.test.context.web.WebAppConfiguration;



@Configuration
@EnableAutoConfiguration
@WebAppConfiguration
@ComponentScan(basePackages = {"com.dataanalytics.controller", "com.dataanalytics.service"/*FIXME:wont work without local couchbase:,  "com.dataanalytics.config"*/})
//FIXME:This cannot be enabled without local couchbase: @EnableCouchbaseRepositories("com.dataanalytics.dao")
@EnableElasticsearchRepositories(basePackages = {"com.dataanalytics.ro", "com.dataanalytics.dao"})
public class TestApplication {
}
