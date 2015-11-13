package com.dataanalytics.service;

import com.dataanalytics.dao.EventRepository;
import com.dataanalytics.domain.Event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;


@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public Event add(Event event) {
        return eventRepository.save(event);
    }
    
    @Async
    public Future<Event> addDefered(Event event) {
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
    
    public String buildDocumentId() {
        return UUID.randomUUID().toString();
    }

    public Pageable buildPageable(int pageNumber, int size) {
        return new PageRequest(pageNumber, size);
    }

    public List<Event> fetchPage(Pageable page) {
        return eventRepository.findAll(page).getContent();
    }
    
    public Event fetch(String documentId) {
        return eventRepository.findOne(documentId);
    }
    
    public void addAll(ArrayList<Event> eventList){
        eventRepository.save(eventList);
       }
}
