package com.dataanalytics.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
import java.util.EnumMap;
import java.util.UUID;

import com.dataanalytics.domain.Event.Type;
import org.springframework.hateoas.ResourceSupport;

import static com.dataanalytics.domain.Event.Type.*;



@Document(indexName="customer")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerJourney extends ResourceSupport {

    @Id
    private String documentId;

    private Date timestamp;

    private String customerEmailHash;

    private EnumMap<Event.Type, Reference> eventMap;

    public CustomerJourney(String customerEmailHash) {
        this(customerEmailHash,new EnumMap<Event.Type, Reference>(Type.class));
    }

    @JsonCreator
    public CustomerJourney(@JsonProperty("customerEmailHash") String customerEmailHash, @JsonProperty("eventMap") EnumMap<Event.Type, Reference> eventMap) {
        documentId = UUID.randomUUID().toString();
        timestamp = new Date();
        this.customerEmailHash = customerEmailHash;
        this.eventMap = eventMap;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public EnumMap<Type, Reference> getEventMap() {
        return eventMap;
    }

    public void setEventMap(EnumMap<Type, Reference> eventMap) {
        this.eventMap = eventMap;
    }

    public String getCustomerEmailHash() {
        return customerEmailHash;
    }

    public void setCustomerEmailHash(String customerEmailHash) {
        this.customerEmailHash = customerEmailHash;
    }

    public Reference addEvent(Event event) {
        return eventMap.put(event.getType(), event.buildReference());
    }

    public Reference getReference(Type type) {
        return eventMap.get(type);
    }

    private Long timeBetweenEvents(Reference event1, Reference event2) {
        if (event1 == null || event2 == null) {
            return null;
        } else {
            return event2.getTimestamp().getTime()-event1.getTimestamp().getTime();
        }
    }

    public Long getTimeFromReservationToCheckIn() {
        return timeBetweenEvents(eventMap.get(CREATE_RESERVATION_EVENT), eventMap.get(CHECK_IN_RESERVATION_EVENT));
    }

    public Long getTimeFromCheckInToStartAppointment() {
        return timeBetweenEvents(eventMap.get(CHECK_IN_RESERVATION_EVENT), eventMap.get(START_APPOINTMENT_EVENT));
    }

    public Long getTimeFromStartAppointmentToEndAppointment() {
        return timeBetweenEvents(eventMap.get(START_APPOINTMENT_EVENT), eventMap.get(END_APPOINTMENT_EVENT));
    }

    public Long getTimeFromEndAppointmentToReserveInventory() {
        return timeBetweenEvents(eventMap.get(END_APPOINTMENT_EVENT), eventMap.get(RESERVE_INVENTORY_EVENT));
    }

    public Long getTimeFromReserveInventoryToRunRequest() {
        return timeBetweenEvents(eventMap.get(RESERVE_INVENTORY_EVENT), eventMap.get(RUN_REQUESTED_EVENT));
    }

    public Long getTimeFromRunRequestedToDelivered() {
        return timeBetweenEvents(eventMap.get(RUN_REQUESTED_EVENT), eventMap.get(RUN_DELIVERED_EVENT));
    }

    public Long getTimeFromRunDeliveredToSale() {
        return timeBetweenEvents(eventMap.get(RUN_DELIVERED_EVENT), eventMap.get(SALE_COMPLETE_EVENT));
    }

    public Long getTimeFromEarliestToLatestEvent() {
        Type firstType = getEntryEventType();
        Type lastType = getExitEventType();
        return timeBetweenEvents(eventMap.get(firstType), eventMap.get(lastType));
    }

    public Type getEntryEventType() {
        Type[] values = Type.values();
        Type firstType = getTypeFromValues(values);
        return firstType;
    }

    public Type getExitEventType() {
        Type[] values = Type.values();
        ArrayUtils.reverse(values);
        Type firstType = getTypeFromValues(values);
        return firstType;
    }

    private Type getTypeFromValues(Type[] values) {
        Type firstType = null;
        for (Type t: values) {
            if (eventMap.containsKey(t)) {
                firstType = t;
                break;
            }
        }
        return firstType;
    }

}
