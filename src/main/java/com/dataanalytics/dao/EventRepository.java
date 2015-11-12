package com.dataanalytics.dao;

import com.dataanalytics.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface EventRepository extends PagingAndSortingRepository<Event, String> {
    Page<Event> findByCustomerEmailHash(String cutomerEmailHash, Pageable page);
    Page<Event> findByCustomerEmailHashAndType(String cutomerEmailHash, Event.Type type, Pageable page);
}
