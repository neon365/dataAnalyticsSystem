package com.dataanalytics;

import com.dataanalytics.security.SecurityFilter;
import com.mangofactory.swagger.plugin.EnableSwagger;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.servlet.Filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;
//import org.springframework.integration.config.EnableIntegration;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.support.GenericMessage;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableSwagger
@EnableAsync
@EnableElasticsearchRepositories("com.dataanalytics.ro")
public class Application {
    @Bean
    public Filter getSecuritFilter () {
        SecurityFilter securityFilter = new SecurityFilter();
        return securityFilter;
    }


    @Bean(name="eventControllerQueue")
    protected AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(64);
        return executor;
    }

    @Bean(name="customerJourneyRebuildQueue")
    protected AsyncTaskExecutor cjTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        return executor;
    }

    @Bean
    public ElasticsearchTemplate elasticsearchTemplate() {
        return new ElasticsearchTemplate(elasticSearchClient());
    }

    private Client elasticSearchClient() {
        return new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
               // .addTransportAddress(new InetSocketTransportAddress("host2", 9300));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
