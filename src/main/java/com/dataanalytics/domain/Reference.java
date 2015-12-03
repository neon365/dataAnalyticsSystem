package com.dataanalytics.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import java.util.Date;


public class Reference extends ResourceSupport {
    private String documentId;

    private Date timestamp;

    @JsonCreator
    public Reference(@JsonProperty("documentId") String documentId, @JsonProperty("timestamp") Date timestamp) {
        this.documentId = documentId;
        this.timestamp = timestamp;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reference)) return false;
        if (!super.equals(o)) return false;

        Reference reference = (Reference) o;

        if (documentId != null ? !documentId.equals(reference.documentId) : reference.documentId != null) return false;
        if (timestamp != null ? !timestamp.equals(reference.timestamp) : reference.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (documentId != null ? documentId.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
}
