package com.dataanalytics.dao;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

@Configuration
@EnableCouchbaseRepositories
public class CouchbaseConfiguration extends AbstractCouchbaseConfiguration {
    @Override
    protected List<String> bootstrapHosts() {
        return Arrays.asList("localhost");
    }

    @Override
    protected String getBucketName() {
        return "default";
    }

    @Override
    protected String getBucketPassword() {
        return "";
    }
}
