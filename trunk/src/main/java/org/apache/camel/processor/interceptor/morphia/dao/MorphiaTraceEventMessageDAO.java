/**
 * 
 */
package org.apache.camel.processor.interceptor.morphia.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.code.morphia.dao.BasicDAO;

import org.bson.types.ObjectId;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.Key;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;

import org.apache.camel.processor.interceptor.morphia.MorphiaTraceEventMessage;
import org.apache.camel.processor.interceptor.morphia.MorphiaTraceEventHandler;

/**
 * Data access object to access and query a trace event log, stored in a mongo db, via morphia 
 * <p />
 * 
 * MorphiaTraceEventMessageDAO extends the generic BasisDAO {@link org.apache.camel.processor.interceptor.morphia.dao}
 * implementing specific access logic for the morphia connection
 * @version 
 * @author T. Neuboeck (2011)
 */
public class MorphiaTraceEventMessageDAO extends BasicDAO<MorphiaTraceEventMessage, ObjectId> {
	
	/**
     * Constructor gets a valid data store and initialize the class via  
     */
	public MorphiaTraceEventMessageDAO	(Datastore ds) {
		super(ds);
	}  
	
	/**
     * Persists a EventMessage in a database
     * 
     * @return resulting list of keys
     */
	public Key<MorphiaTraceEventMessage> saveMessage(Object entity) {
		return this.save((MorphiaTraceEventMessage)entity);
	}

	/**
     * @return list of all available EventMessages {@link MorphiaTraceEventMessage}
     */
	public List<MorphiaTraceEventMessage> findAll( ) {     
		 return this.find().asList(); 
	} 

	/**
	 * Gets a field information for sorting
	 * 
     * @return first EventMessages {@link MorphiaTraceEventMessage} ordered by the given sort field
     */
	private MorphiaTraceEventMessage getFirstOrderedMessage(String sortedField) {
		return this.findOne(this.createQuery().order(sortedField));
	}
	
	/**
	 * Gets a min and max date information for filtering
	 * 
     * @return filtered EventMessages {@link MorphiaTraceEventMessage} by the given date range
     */
	public List<MorphiaTraceEventMessage> findAll(Date min, Date max ) {    
		return this.find(this.createQuery().filter("timestamp >=", min).filter("timestamp <=", max)).asList();
	} 
	
	/**
	 * Gets case id for filtering
	 * 
     * @return first EventMessages {@link MorphiaTraceEventMessage} which matches the given case id
     */
	private MorphiaTraceEventMessage getFirstMatchingMessage(String caseID) {
		return this.findOne(this.createQuery().filter("internalCaseID ==", caseID));
	}
	
	/**
     * @return first internal case id (ordered ascending)
     */
	public String getFirstInternalCaseID() {
		String firstInternalCaseID = null;
		MorphiaTraceEventMessage m = this.getFirstOrderedMessage("internalCaseID");
		  
		 if (m != null){
			 firstInternalCaseID = m.getInternalCaseID();
		 }
		 
		 return firstInternalCaseID;
	}
	
	/**
     * @return first internal case id (ordered descending)
     */
	public String getLastInternalCaseID() {
		String lastInternalCaseID = null;
		MorphiaTraceEventMessage m = this.getFirstOrderedMessage("-internalCaseID");
		  
		 if (m != null){
			 lastInternalCaseID = m.getInternalCaseID();
		 }
		 
		 return lastInternalCaseID;
	}
	
	/**
	 * Gets a internalCaseId to prove
	 * 
     * @return true if the given case id exists in the database
     */
	public boolean internalCaseIDExists(String internalCaseID) {
		boolean caseIDExists = false;
		MorphiaTraceEventMessage m = this.getFirstMatchingMessage(internalCaseID);
		  
		 if (m != null){
			 caseIDExists = true;
		 }
		 
		 return caseIDExists;
	}
	
	/**
     * @return EventMessages {@link MorphiaTraceEventMessage} with smallest timestamp
     */
	public MorphiaTraceEventMessage findMessageWithMinTimestamp() {
		return this.getFirstOrderedMessage("timestamp");
	}
	
	/**
     * @return EventMessages {@link MorphiaTraceEventMessage} with greatest timestamp
     */
	public MorphiaTraceEventMessage findMessageWithMaxTimestamp() {
		return this.getFirstOrderedMessage("-timestamp");
	}
}
