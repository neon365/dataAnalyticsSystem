package com.dataanalytics.dao;

import com.dataanalytics.TestApplication;
import com.dataanalytics.domain.Event;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=TestApplication.class)
public class EventRepositoryTest {
    @Autowired
    private EventRepository repository;

    @Before
    public void setup() {
        repository.deleteAll();
    }

    @Test
    public void testSaveAndRead() throws Exception {
        String documentId = "123456";
        Event event = new Event(documentId, "customer1", new Date(), Event.Type.CREATE_RESERVATION_EVENT);
        repository.save(event);
        Event roEvent = repository.findOne(documentId);
        assertThat(roEvent, is(equalTo(event)));
    }

    @Test
    public void testFindByEmailHash() throws Exception {
        List<Event> entities = buildRandomEvents("person@organization.org", Event.Type.values().length);
        repository.save(entities);
        repository.save(buildRandomEvents("person@test.org",Event.Type.values().length));
        Page<Event> roEvents = repository.findByCustomerEmailHash("asdf", new PageRequest(0, 100));
        assertThat(roEvents.getNumberOfElements(), is(equalTo(0)));
        roEvents = repository.findByCustomerEmailHash("\"person@organization.org\"", new PageRequest(0, 100));
        assertThat(roEvents.getNumberOfElements(), is(equalTo(entities.size())));
        for (Event e: roEvents.getContent()) {
            if (e.getDocumentId().equals(entities.get(2).getDocumentId())) {
                assertThat(e, is(equalTo(entities.get(2))));
            }
            if (e.getDocumentId().equals(entities.get(5).getDocumentId())) {
                assertThat(e, is(equalTo(entities.get(5))));
            }
        }


    }

    public List<Event> buildRandomEvents(String cutomerEmailHash, int count) {
        List<Event> eventList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Event.Type[] values = Event.Type.values();
            Event.Type type = values[i % values.length];
            eventList.add(new Event(UUID.randomUUID().toString(), cutomerEmailHash, new Date(), type));
        }
        return eventList;
    }
}