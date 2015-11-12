package com.dataanalytics.service;

import com.dataanalytics.dao.EventRepository;
import com.dataanalytics.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Async
    public Event add(Event event) {
        return eventRepository.save(event);
    }

    public Event buildEvent(String customerEmailHash, String employeeId,
                            String transactionId,
                            String serialNumber, String storeNumber,
                            Date timestamp,
                            Event.Type type) {
        return new Event(UUID.randomUUID().toString(), customerEmailHash, employeeId,
                transactionId, serialNumber, storeNumber, timestamp, type);
    }

    public Pageable buildPageable(int pageNumber, int size) {
        return new PageRequest(pageNumber, size);
    }

    public List<Event> fetchPage(Pageable page) {
        return eventRepository.findAll(page).getContent();
    }
}
