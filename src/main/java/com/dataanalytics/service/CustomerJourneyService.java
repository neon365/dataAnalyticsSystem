package com.dataanalytics.service;

import com.dataanalytics.dao.EventRepository;
import com.dataanalytics.domain.CouchbaseEvent;
import com.dataanalytics.domain.CustomerJourney;
import com.dataanalytics.domain.Event;
import com.dataanalytics.ro.CouchbaseEventRepository;
import com.dataanalytics.ro.ROCustomerJourneyRepository;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;


@Service
public class CustomerJourneyService {
    Logger logger = LoggerFactory.getLogger(CustomerJourneyService.class);

    @Autowired
    private ROCustomerJourneyRepository roCustomerJourneyRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CouchbaseEventRepository couchbaseEventRepository;

    public void updateCustomerJourney(Event event) {
        String customerEmailHash = event.getCustomerEmailHash();

        Page<CustomerJourney> customerJourneyPage = roCustomerJourneyRepository.findByCustomerEmailHash(customerEmailHash,
                new PageRequest(0, 1));//, Sort.Direction.DESC, "timestamp"));
        CustomerJourney customerJourney = null;
        for (CustomerJourney journey: customerJourneyPage) {
            if (!journey.getEventMap().containsKey(event.getType())) {
                customerJourney = journey;
            }
        }
        if (customerJourney == null) {
            customerJourney = new CustomerJourney(customerEmailHash);
        }
        customerJourney.addEvent(event);
        roCustomerJourneyRepository.save(customerJourney);
    }

    public String buildDocumentId() {
        return UUID.randomUUID().toString();
    }

    public Pageable buildPageable(int pageNumber, int size) {
        return new PageRequest(pageNumber, size);
    }

    public Page<CustomerJourney> fetchPage(Pageable page) {
        return roCustomerJourneyRepository.findAll(page);
    }

    public CustomerJourney fetch(String documentId) {
        return roCustomerJourneyRepository.findOne(documentId);
    }

    @Async("customerJourneyRebuildQueue")
    public void rebuildCustomerJourney() {
        //FIXME:Requires several GB of memory to run
        roCustomerJourneyRepository.deleteAll();

        Page<Event> events = fetchPageOfEvents(new PageRequest(0, 1));
        long count = events.getTotalElements();
        int pageSize = 10000;
        for (int i = 0; i*pageSize < count; i++) {
            logger.info("Rebuilding: "+pageSize+ " records starting at "+i*pageSize+" until "+ count);
            events = fetchPageOfEvents(new PageRequest(i*pageSize, pageSize));
            for (Event event : events) {
                updateCustomerJourney(event);
            }
        }


    }

    public Page<Event> fetchPageOfEvents(Pageable page) {
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
}
