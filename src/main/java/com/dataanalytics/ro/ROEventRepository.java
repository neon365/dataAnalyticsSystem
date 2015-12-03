package com.dataanalytics.ro;

import com.dataanalytics.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;



public interface ROEventRepository extends PagingAndSortingRepository<Event, String> {
    Page<Event> findByCustomerEmailHash(String customerEmailHash, Pageable page);
    Page<Event> findByCustomerEmailHashAndType(String customerEmailHash, Event.Type type, Pageable page);
}
