package com.dataanalytics.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
import java.util.UUID;


@Document(indexName = "event")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
    private String documentId;
    private String customerEmailHash;
    private String employeeId;
    private String transactionId;
    private String serialNumber;
    private String storeNumber;
    private Date timestamp;
    private Type type;

    public enum Type {
        CREATE_RESERVATION_EVENT,
        CHECK_IN_RESERVATION_EVENT,
        START_APPOINTMENT_EVENT,
        END_APPOINTMENT_EVENT,
        RESERVE_INVENTORY_EVENT,
        RUN_REQUESTED_EVENT,
        RUN_DELIVERED_EVENT,
        SALE_COMPLETE_EVENT
    }

    @Deprecated
    public Event() {
    }

    public Event(String documentId, String customerEmailHash, Date timestamp, Type type) {
        checkForLegalState(documentId, customerEmailHash, timestamp, type);
        this.customerEmailHash = customerEmailHash;
        this.type = type;
    }

    @JsonCreator
    public Event(@JsonProperty(value = "documentId") String documentId,
                 @JsonProperty(value = "customerEmailHash") String customerEmailHash,
                 @JsonProperty("employeeId") String employeeId,
                 @JsonProperty("transactionId") String transactionId,
                 @JsonProperty("serialNumber") String serialNumber,
                 @JsonProperty("storeNumber") String storeNumber,
                 @JsonProperty("timestamp") Date timestamp,
                 @JsonProperty(value = "type") Type type) {
        this(documentId, customerEmailHash, timestamp, type);
        this.employeeId = employeeId;
        this.serialNumber = serialNumber;
        this.storeNumber = storeNumber;
        this.transactionId = transactionId;
    }

    private void checkForTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        if (this.timestamp == null) {
            this.timestamp = new Date();
        }
    }

    private void checkForUniqueId(String documentId) {
        if (documentId == null) {
            this.documentId = UUID.randomUUID().toString();
        } else {
            this.documentId = documentId;
        }
    }

    private void checkForLegalState(String documentId, String customerToken, Date timestamp, Type type) {
        if (customerToken == null || type == null) {
            throw new IllegalStateException("Event must include customer email hash and type");
        }
        checkForUniqueId(documentId);
        checkForTimestamp(timestamp);
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getCustomerEmailHash() {
        return customerEmailHash;
    }

    public void setCustomerEmailHash(String customerEmailHash) {
        this.customerEmailHash = customerEmailHash;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getStoreNumber() {
        return storeNumber;
    }

    public void setStoreNumber(String storeNumber) {
        this.storeNumber = storeNumber;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;

        Event event = (Event) o;

        if (customerEmailHash != null ? !customerEmailHash.equals(event.customerEmailHash) : event.customerEmailHash != null)
            return false;
        if (documentId != null ? !documentId.equals(event.documentId) : event.documentId != null) return false;
        if (employeeId != null ? !employeeId.equals(event.employeeId) : event.employeeId != null) return false;
        if (serialNumber != null ? !serialNumber.equals(event.serialNumber) : event.serialNumber != null) return false;
        if (storeNumber != null ? !storeNumber.equals(event.storeNumber) : event.storeNumber != null) return false;
        if (timestamp != null ? !timestamp.equals(event.timestamp) : event.timestamp != null) return false;
        if (transactionId != null ? !transactionId.equals(event.transactionId) : event.transactionId != null)
            return false;
        if (type != event.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = documentId != null ? documentId.hashCode() : 0;
        result = 31 * result + (customerEmailHash != null ? customerEmailHash.hashCode() : 0);
        result = 31 * result + (employeeId != null ? employeeId.hashCode() : 0);
        result = 31 * result + (transactionId != null ? transactionId.hashCode() : 0);
        result = 31 * result + (serialNumber != null ? serialNumber.hashCode() : 0);
        result = 31 * result + (storeNumber != null ? storeNumber.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
