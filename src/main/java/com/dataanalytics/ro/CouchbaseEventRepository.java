package com.dataanalytics.ro;

import com.dataanalytics.domain.CouchbaseEvent;
import com.dataanalytics.domain.Event;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface CouchbaseEventRepository extends PagingAndSortingRepository<CouchbaseEvent, String> {
}
