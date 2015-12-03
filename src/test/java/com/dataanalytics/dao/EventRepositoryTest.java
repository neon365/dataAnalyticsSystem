package com.dataanalytics.dao;

import com.dataanalytics.TestApplication;
import com.dataanalytics.domain.Event;
import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=TestApplication.class)
public class EventRepositoryTest {
    @Autowired
    private EventRepository repository;

    public CouchbaseOperations getCouchbaseOperations() {
        return couchbaseOperations;
    }

    public void setCouchbaseOperations(CouchbaseOperations couchbaseOperations) {
        this.couchbaseOperations = couchbaseOperations;
    }

    @Autowired
    private CouchbaseOperations couchbaseOperations;


    @Test
    public void testSaveAndRead() throws Exception {
        String documentId = "123456";
        Event event = new Event(documentId, "customer1", new Date(), Event.Type.CREATE_RESERVATION_EVENT);
        repository.save(event);
        Event roEvent = repository.findOne(documentId);
        assertThat(roEvent, is(equalTo(event)));
    }

    @Test
    @Ignore
    public void testSaveAndFindAll() throws Exception {
        String documentId = "123456";
        Event event = new Event(documentId, "customer1", new Date(), Event.Type.CREATE_RESERVATION_EVENT);
        repository.save(event);

//        CouchbaseClient cb = getCouchbaseOperations().getCouchbaseClient();
//        com.couchbase.client.protocol.views.View view = cb.getView("com.apple.retail.odyssey.domain.Event", "all");


//        Query query = new Query();
//        query.setReduce(false);
//        query.setKeys(documentId);//ComplexKey.of(Lists.newArrayList(documentId)));
//        List<Event> roEvents = getCouchbaseOperations().findByView("event", "all", query, Event.class);
//        assertThat(roEvents, is(notNullValue()));
//        assertThat(Iterables.size(roEvents), is(equalTo(1)));


//        Paginator paginatedQuery = cb.paginatedQuery(view, new Query().setReduce(false), pageable.getPageSize());
//        int pageCount = 0;
//        List<T> result = null;
//        while (paginatedQuery.hasNext() && pageCount <= pageable.getPageNumber()) {
//            pageCount++;
//            ViewResponse response = paginatedQuery.next();
//            if (pageCount == pageable.getPageNumber()) {
//                result = new ArrayList<T>(response.size());
//                for (final ViewRow row : response) {
//                    result.add(getCouchbaseOperations().findById(row.getId(), getEntityInformation().getJavaType()));
//                }
//            }
//        }
//        return new PageImpl<T>(result, pageable, count());
//        Iterable<Event> roEvents = repository.findAll();
//        assertThat(roEvents, is(notNullValue()));
//        assertThat(Iterables.size(roEvents), is(equalTo(1)));

        Query query = new Query();
        query.setReduce(false);
        query.setKeys("[\""+documentId+"\"]");
       // Iterable<Event> roEvents = repository.customViewQuery(query.setLimit(2).setStale(Stale.FALSE));
        //Iterable<Event> roEvents = repository.findAll(Lists.newArrayList(documentId, documentId));
        //assertThat(roEvents, is(notNullValue()));
        //assertThat(Iterables.size(roEvents), is(equalTo(1)));

    }
}