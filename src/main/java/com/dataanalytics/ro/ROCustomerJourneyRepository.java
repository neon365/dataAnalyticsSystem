package com.dataanalytics.ro;

import com.dataanalytics.domain.CustomerJourney;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface ROCustomerJourneyRepository extends ElasticsearchRepository<CustomerJourney, String> {
    Page<CustomerJourney> findByCustomerEmailHash(String customerEmailHash, Pageable pageable);
}
