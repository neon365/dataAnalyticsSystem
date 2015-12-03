package com.dataanalytics.dao;

import com.dataanalytics.domain.Event;
import com.couchbase.client.protocol.views.Query;

import org.springframework.data.couchbase.core.view.View;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


@SuppressWarnings("unused")
public interface EventRepository extends CouchbaseRepository<Event, String> {
//    @View(designDocument = "event", viewName = "all")
//    Iterable<Event> customViewQuery(Query query);
}
