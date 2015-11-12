package com.dataanalytics.controller;

import com.dataanalytics.domain.Event;
import com.dataanalytics.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;


@RestController
@RequestMapping

public class EventController {

    @Autowired
    private EventService eventService;

    @RequestMapping(value = {"/api/v1/event", "/event"}, method = GET)
    List<Event> getEvents(@RequestParam("page") int page, @RequestParam("size") int size) {
        return eventService.fetchPage(eventService.buildPageable(page, size));
    }

    @RequestMapping(value = "/api/v1/event", method = POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    Map<String, String> postEvent(@RequestParam("customerEmailHash") String customerEmailHash,
                    @RequestParam(value = "employeeId", required = false) String employeeId,
                    @RequestParam(value = "transactionId", required = false) String transactionId,
                    @RequestParam(value = "serialNumber", required = false) String serialNumber,
                    @RequestParam(value = "storeNumber", required = false) String storeNumber,
                    @RequestParam(value = "timestamp", required = false) Date timestamp,
                    @RequestParam("type") Event.Type type) {
        Event event = eventService.buildEvent(customerEmailHash, employeeId,
                transactionId, serialNumber, storeNumber, timestamp, type);
        eventService.add(event);
        Map<String, String> ret = new HashMap<>();
        ret.put("documentId", event.getDocumentId());
        return ret;
    }

    @RequestMapping(value = {"/api/v2/event", "/event"}, method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    Event postEvent(@RequestBody Event event) {
         return eventService.add(event);
    }
}
