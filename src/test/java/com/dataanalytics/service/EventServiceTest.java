package com.dataanalytics.service;

import com.dataanalytics.TestApplication;
import com.dataanalytics.dao.EventRepository;
import com.dataanalytics.domain.CouchbaseEvent;
import com.dataanalytics.domain.CustomerJourney;
import com.dataanalytics.domain.Event;
import com.dataanalytics.domain.Reference;
import com.dataanalytics.ro.CouchbaseEventRepository;
import com.dataanalytics.ro.ROCustomerJourneyRepository;
import com.dataanalytics.ro.ROEventRepository;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
public class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ROEventRepository roEventRepository;

    @Mock
    private ROCustomerJourneyRepository roCustomerJourneyRepository;

    @Mock
    private Event event;

    @Mock
    private Page<Event> page;

    @Mock
    private Page<CustomerJourney> customerJourneyPage;

    @Mock
    private Page<CouchbaseEvent> couchbaseEventPage;

    @Mock
    private CustomerJourney customerJourney;

    @InjectMocks
    @Spy
    private CustomerJourneyService customerJourneyService;

    @Mock
    private CouchbaseEventRepository couchbaseEventRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(eventRepository.save(any(Event.class))).then(returnsFirstArg());
        when(roEventRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(couchbaseEventPage.iterator()).thenReturn(Lists.newArrayList(new CouchbaseEvent("documentId")).iterator());
        when(couchbaseEventRepository.findAll(any(Pageable.class))).thenReturn(couchbaseEventPage);
        when(roEventRepository.findOne(anyString())).thenReturn(event);
        when(eventRepository.findOne(anyString())).thenReturn(event);
        when(roCustomerJourneyRepository.findByCustomerEmailHash(anyString(),any(Pageable.class))).thenReturn(customerJourneyPage);
        when(customerJourneyPage.iterator()).thenReturn(Arrays.asList(customerJourney).iterator());
        EnumMap<Event.Type, Reference> eventMap = new EnumMap<>(Event.Type.class);
        when(customerJourney.getEventMap()).thenReturn(eventMap);
    }

    @Test
    public void testAdd() throws Exception {
        assertThat(eventService.add(event), is(equalTo(event)));
        verify(eventRepository).save(event);
    }

    @Test
    public void testAddDeferred() throws Exception {
        assertThat(eventService.addDeferred(event).get(), is(equalTo(event)));
        verify(eventRepository).save(event);
    }

    @Test
    public void testBuildEvent() throws Exception {
        Date timestamp = new Date();
        Event eventBuilt = eventService.buildEvent("a", "b", "c", "d", "e", timestamp, Event.Type.CREATE_RESERVATION_EVENT);
        Event operand = new Event(eventBuilt.getDocumentId(), "a", "b", "c", "d", "e", timestamp, Event.Type.CREATE_RESERVATION_EVENT);
        assertThat(eventBuilt, is(equalTo(operand)));
    }

    @Test
    public void testBuildEventList() throws Exception {
        List<Event> eventList = new ArrayList<>();
        List<Event> operandList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Date timestamp = new Date();
            eventList.add(eventService.buildEvent("a", "b", "c", "d", "e", timestamp, Event.Type.CREATE_RESERVATION_EVENT));
            operandList.add(new Event(eventList.get(i).getDocumentId(), "a", "b", "c", "d", "e", timestamp, Event.Type.CREATE_RESERVATION_EVENT));
        }
        assertThat(eventList, is(equalTo(operandList)));
    }

    @Test
    public void testBuildDocumentId() throws Exception {
        assertThat(eventService.buildDocumentId(), is(instanceOf(String.class)));
        assertThat(eventService.buildDocumentId().length(), is(equalTo(36)));
    }

    @Test
    public void testBuildPageable() throws Exception {

    }

    @Test
    public void testFetchPage() throws Exception {
        Pageable p = mock(Pageable.class);
        assertThat(eventService.fetchPage(p), is(instanceOf(Page.class)));
        verify(couchbaseEventRepository).findAll(p);
        verify(eventRepository).findOne(anyString());
    }

    @Test
    public void testFetch() throws Exception {
        assertThat(eventService.fetch("documentId"), is(equalTo(event)));
        verify(eventRepository).findOne("documentId");
    }

    @Test
    public void testAddAll() throws Exception {

    }
}