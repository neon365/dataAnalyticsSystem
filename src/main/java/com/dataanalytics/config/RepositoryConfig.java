package com.dataanalytics.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableCouchbaseRepositories("com.dataanalytics.dao")
class RepositoryConfig extends AbstractCouchbaseConfiguration {

	@Override
	protected List<String> bootstrapHosts() {
		// TODO remove hard coding and replace values based on environment
		return Arrays.asList("localhost");
	}

	@Override
	protected String getBucketName() {
		// TODO remove hard coding and replace values based on environment
		return "event";
	}

	@Override
	protected String getBucketPassword() {
		// TODO remove hard coding and replace values based on environment
		return "";
	}
}