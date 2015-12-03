package com.dataanalytics.controller;

import com.dataanalytics.domain.CustomerJourney;
import com.dataanalytics.service.CustomerJourneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;


@RestController
@RequestMapping
public class CustomerJourneyController {
    @Autowired
    private CustomerJourneyService customerJourneyService;

    @RequestMapping(value = {"/journey"}, method = GET)
    Iterable<CustomerJourney> getCustomerJourney(@RequestParam("page") int page, @RequestParam("size") int size) {
        Page<CustomerJourney> customerJourneys = customerJourneyService.fetchPage(customerJourneyService.buildPageable(page, size));
        for (CustomerJourney cj: customerJourneys) {
            decorateReference(cj);
        }
        return customerJourneys;
    }

    @RequestMapping(value = {"/journey/{documentId}"}, method = GET)
    CustomerJourney getCustomerJourney(@PathVariable("documentId") String documentId) {
        return decorateReference(customerJourneyService.fetch(documentId));
    }

    private CustomerJourney decorateReference(CustomerJourney ref) {
        ref.add(linkTo(methodOn(CustomerJourneyController.class).getCustomerJourney(ref.getDocumentId())).withSelfRel());
        return ref;
    }

    @RequestMapping(value = {"/journey"}, method = DELETE)
    void rebuildCustomerJourney() {
        customerJourneyService.rebuildCustomerJourney();
    }
}
