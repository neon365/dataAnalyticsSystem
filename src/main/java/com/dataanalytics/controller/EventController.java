package com.dataanalytics.controller;

import com.dataanalytics.domain.Event;
import com.dataanalytics.service.EventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.springframework.http.ResponseEntity;

import com.dataanalytics.domain.Reference;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;


@RestController
@RequestMapping

public class EventController {
	
    @Autowired
    private EventService eventService;

    @RequestMapping(value = {"/event"}, method = GET)
    List<Event> getEvents(@RequestParam("page") int page, @RequestParam("size") int size) {
        return eventService.fetchPage(eventService.buildPageable(page, size));
    }

    @RequestMapping(value = {"/event/{documentId}"}, method = GET)
    Event getEvent(@PathVariable("documentId") String documentId) {
        return eventService.fetch(documentId);
    }

    @RequestMapping(value = "/api/v1/event", method = POST)
    HttpEntity<Reference> postEvent(@RequestParam("customerEmailHash") String customerEmailHash,
                    @RequestParam(value = "employeeId", required = false) String employeeId,
                    @RequestParam(value = "transactionId", required = false) String transactionId,
                    @RequestParam(value = "serialNumber", required = false) String serialNumber,
                    @RequestParam(value = "storeNumber", required = false) String storeNumber,
                    @RequestParam(value = "timestamp", required = false) Date timestamp,
                    @RequestParam("type") Event.Type type) {
        Event event = eventService.buildEvent(customerEmailHash, employeeId,
                transactionId, serialNumber, storeNumber, timestamp, type);
        eventService.add(event);
        return decorateRefernce(event.buildReference());
    }

    @RequestMapping(value = {"/api/v2/event", "/event"}, method = POST)
    @ResponseBody
    HttpEntity<Reference> postEvent(@RequestBody Event event) {
        Event newEvent = new Event(eventService.buildDocumentId(), event);
        eventService.addDefered(newEvent);
        return decorateRefernce(newEvent.buildReference());
    }
    
    @RequestMapping(value = {"/api/v1/event/bulk", "/api/v2/event/bulk", "/event/bulk"}, method = POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    void bulkUploadEvent(@RequestBody final ArrayList<Event> eventList) {
         eventService.addAll(eventList);
    }

    private HttpEntity<Reference> decorateRefernce(Reference ref) {
        ref.add(linkTo(methodOn(EventController.class).getEvent(ref.getDocumentId())).withSelfRel());
        return new ResponseEntity<>(ref, HttpStatus.ACCEPTED);
    }
}
