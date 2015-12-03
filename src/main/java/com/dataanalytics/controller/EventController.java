package com.dataanalytics.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import com.dataanalytics.domain.Event;
import com.dataanalytics.domain.Reference;
import com.dataanalytics.service.EventService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping

public class EventController {

    @Autowired
    private EventService eventService;

    @RequestMapping(value = {"/event"}, method = GET)
    Iterable<Event> getEvents(@RequestParam("page") int page, @RequestParam("size") int size) {
        Page<Event> events = eventService.fetchPage(eventService.buildPageable(page, size));
        for (Event e: events) {
            decorateEvent(e);
        }
        return events;
    }

    @RequestMapping(value = {"/event/{documentId}"}, method = GET)
    Event getEvent(@PathVariable("documentId") String documentId) {
        return decorateEvent(eventService.fetch(documentId));
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
        eventService.addDeferred(event);
        return decorateReference(event.buildReference());
    }

    @RequestMapping(value = {"/api/v2/event", "/event"}, method = POST)
    @ResponseBody
    HttpEntity<Reference> postEvent(@RequestBody Event event) {
        Event newEvent = new Event(eventService.buildDocumentId(), event);
         eventService.addDeferred(newEvent);
        
        return decorateReference(newEvent.buildReference());
    }
    
    @RequestMapping(value = {"/api/v1/event/bulk", "/api/v2/event/bulk", "/event/bulk"}, method = POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    HttpEntity<List<Reference>>  bulkUploadEvent(@RequestBody final List<Event> eventList) {
    	List<Event> newEventList = eventService.buildEventList(eventList);
    	//prepare reference list
    	eventService.addAll(newEventList);
    	List<Reference> refList = prepareReferenceList(newEventList);
    	System.out.println("controller processing complete");
    	
    	return decorateReference(refList);
    }

    private List<Reference> prepareReferenceList(List<Event> eventList) {
    	List<Reference>  refList = new ArrayList<>();
    	for(Event event : eventList){
    		refList.add(event.buildReference());
    	}
		
		return refList;
	}

	private HttpEntity<Reference> decorateReference(Reference ref) {
        ref.add(linkTo(methodOn(EventController.class).getEvent(ref.getDocumentId())).withSelfRel());
        return new ResponseEntity<>(ref, HttpStatus.ACCEPTED);
    }

    private Event decorateEvent(Event ref) {
        ref.add(linkTo(methodOn(EventController.class).getEvent(ref.getDocumentId())).withSelfRel());
        return ref;
    }
    
    private HttpEntity<List<Reference>> decorateReference(List<Reference> refList) {
    	for(Reference reference : refList){
    		reference.add(linkTo(methodOn(EventController.class).getEvent(reference.getDocumentId())).withSelfRel());
    	}
        return new ResponseEntity<>(refList, HttpStatus.ACCEPTED);
    }
    
}
