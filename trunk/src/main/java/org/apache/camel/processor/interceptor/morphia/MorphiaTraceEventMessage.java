/**
 * 
 */
package org.apache.camel.processor.interceptor.morphia;

import java.io.Serializable;
import java.util.Date;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Property;
import com.google.code.morphia.annotations.Transient;
import org.bson.types.ObjectId;

import org.apache.camel.Exchange;
import org.apache.camel.processor.interceptor.TraceEventMessage;
/**
 * A trace event message that contains decomposed information about the traced
 * {@link org.apache.camel.Exchange} at the point of interception. The information is stored as snapshot copies
 * using String types.
 * <p/>
 * Notice not all implementations may provide direct access to the traced {@link Exchange} using
 * the {@link #getTracedExchange()} method, and thus this method may return <tt>null</tt>.
 * This morphia implementation will return <tt>null</tt>.
 */
@Entity("traceEventMessage")
public class MorphiaTraceEventMessage implements TraceEventMessage, Serializable {

    @Id private ObjectId id;
    protected Date timestamp;
    protected String fromEndpointUri;
    protected String previousNode;
    protected String toNode;
    protected String exchangeId;
    protected String shortExchangeId;
    protected String exchangePattern;
    protected String properties;
    protected String headers;
    protected String body;
    protected String bodyType;
    protected String outHeaders;
    protected String outBody;
    protected String outBodyType;
    protected String causedByException;
    
    protected String correlationID;
    protected String internalCaseID;
    
    
    public MorphiaTraceEventMessage() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    /**
     * Gets the timestamp when the interception occurred
     */
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(String previousNode) {
        this.previousNode = previousNode;
    }

    public String getFromEndpointUri() {
        return fromEndpointUri;
    }

    public void setFromEndpointUri(String fromEndpointUri) {
        this.fromEndpointUri = fromEndpointUri;
    }

    public String getToNode() {
        return toNode;
    }

    public void setToNode(String toNode) {
        this.toNode = toNode;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getShortExchangeId() {
        return shortExchangeId;
    }

    public void setShortExchangeId(String shortExchangeId) {
        this.shortExchangeId = shortExchangeId;
    }

    public String getExchangePattern() {
        return exchangePattern;
    }

    public void setExchangePattern(String exchangePattern) {
        this.exchangePattern = exchangePattern;
    }

   // @Lob
    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    //@Lob
    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    //@Lob
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    //@Lob
    public String getOutBody() {
        return outBody;
    }

    public void setOutBody(String outBody) {
        this.outBody = outBody;
    }

    public String getOutBodyType() {
        return outBodyType;
    }

    public void setOutBodyType(String outBodyType) {
        this.outBodyType = outBodyType;
    }

    //@Lob
    public String getOutHeaders() {
        return outHeaders;
    }

    public void setOutHeaders(String outHeaders) {
        this.outHeaders = outHeaders;
    }

    //@Lob
    public String getCausedByException() {
        return causedByException;
    }

    public void setCausedByException(String causedByException) {
        this.causedByException = causedByException;
    }

    public Exchange getTracedExchange() {
        return null;
    }

    public String getCorrelationID() {
		return correlationID;
	}

	public void setCorrelationID(String correlationID) {
		this.correlationID = correlationID;
	}

	/**
     * Gets the internal case id
     */
	public String getInternalCaseID() {
		return internalCaseID;
	}

	public void setInternalCaseID(String internalCaseID) {
		this.internalCaseID = internalCaseID;
	}

	@Override
    public String toString() {
        return "TraceEventMessage[" + getExchangeId() + "] on node: " + getToNode();   
    }

}
