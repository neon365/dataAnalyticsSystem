package com.dataanalytics.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import com.dataanalytics.domain.CouchbaseEvent;
import com.dataanalytics.domain.CustomerJourney;
import com.dataanalytics.ro.CouchbaseEventRepository;
import com.dataanalytics.ro.ROEventRepository;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.metrics.percentiles.InternalPercentiles;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.dataanalytics.dao.EventRepository;
import com.dataanalytics.domain.Event;

import javax.annotation.Nullable;



@Service
public class EventService {
    Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CouchbaseEventRepository couchbaseEventRepository;

    @Autowired
    private CustomerJourneyService customerJourneyService;


    public Event add(Event event) {
    	Event retEvent = eventRepository.save(event);
        customerJourneyService.updateCustomerJourney(retEvent);
        return retEvent;
    }

    @Async("eventControllerQueue")
    public Future<Event> addDeferred(Event event) {
        return new AsyncResult<>(add(event));
    }
   
    public Event buildEvent(String customerEmailHash, String employeeId,
                            String transactionId,
                            String serialNumber, String storeNumber,
                            Date timestamp,
                            Event.Type type) {
        return new Event(buildDocumentId(), customerEmailHash, employeeId,
                transactionId, serialNumber, storeNumber, timestamp, type);
    }

   public List<Event> buildEventList(List<Event> eventList){
	   List<Event> newEventList = new ArrayList<>();
	   for(Event event: eventList){
		   newEventList.add(new Event(buildDocumentId(),event));
	   }
	   return newEventList;
	   
   }
    
    public String buildDocumentId() {
        return UUID.randomUUID().toString();
    }

    public Pageable buildPageable(int pageNumber, int size) {
        return new PageRequest(pageNumber, size);
    }

    public Page<Event> fetchPage(Pageable page) {
        Page<CouchbaseEvent> couchbaseEvents = couchbaseEventRepository.findAll(page);
        Iterable<String> converted = CouchbaseEvent.convert(couchbaseEvents);
        ArrayList<Event> events = Lists.newArrayList();
        for (String key: converted) {
            events.add( eventRepository.findOne(key));
        }
        //FIXME:findAll with list of ids does not work in couchbase
//        Iterable<Event> all = eventRepository.findAll(converted);

        return new PageImpl<>(events, page, couchbaseEvents.getTotalElements());
    }

    public Event fetch(String documentId) {
        //CouchbaseEvent couchbaseEvents = couchbaseEventRepository.findOne(documentId);
        return eventRepository.findOne(documentId);
    }

    @Async("eventControllerQueue")
    public Future<ArrayList<Event>> addAll(List<Event> eventList){
        //FIXME:Don't leave unnecessary logging in the code--use the debugger for this
    	logger.trace("started saving the documents");
    	ArrayList<Event> result = (ArrayList<Event>) eventRepository.save(eventList);
        logger.trace("End of saving the documents");
		return new AsyncResult<>(result);
    }

}
