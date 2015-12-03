package com.dataanalytics.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.annotation.Nullable;


@Document(indexName = "event", type="couchbaseDocument")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CouchbaseEvent {
    @Id
    String id;

    public CouchbaseEvent() {
        super();
    }

    public CouchbaseEvent(String id) {
        this();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static Iterable<String> convert(Iterable<CouchbaseEvent> couchbaseEvents) {
        return Iterables.transform(couchbaseEvents, new Function<CouchbaseEvent, String>() {
            @Nullable
            @Override
            public String apply(CouchbaseEvent input) {
                if (input == null || input.id == null) {
                    return "";
                }
                return input.id;
            }
        });
    }
}
