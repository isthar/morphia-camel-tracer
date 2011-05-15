/**
 * 
 */
package org.apache.camel.processor.interceptor.morphia.controller;

import java.util.List;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.DatastoreImpl;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

import org.apache.camel.processor.interceptor.morphia.dao.MorphiaTraceEventMessageDAO;

/**
 * ConnectionController manage a current connection to a specific tracer mongo db via morphia and other necessary objects to derive information out of the trace log
 * <p />
 * 
 * The controller creates for a successfully established connection a dao to access and query the tracer db
 * 
 * @version 
 * @author T. Neuboeck (2011)
 */
public class MorphiaConnectionController {
		
		private MongoDBConnectionInfo mongoDBConnInfo;
		private Morphia morphia;
		private Mongo mongo;
		private boolean connected;
		private MorphiaResourceDerivation resourceDerivation;
		
		private MorphiaTraceEventMessageDAO currentDAO = null;
		
		//stores last error message
		private String errorMessage = null;
		
		/**
	     * Default constructor for the controller
	     */
		public MorphiaConnectionController() {
			this.resourceDerivation = new MorphiaResourceDerivation();
		}
		
		/**
	     * @return true if the controller has established a connection to the mongo db instance else false
	     */
		public boolean isConnected() {
			return this.connected;
		}
		
		/**
	     * @return new MongoDBConnectionInfo {@link MongoDBConnectionInfo}
	     */
		public MongoDBConnectionInfo getMongoDBConnInfo() {
		 	  return this.mongoDBConnInfo;
		 }
		
		
		/**
	     * Gets the current used MongoDBConnectionInfo {@link MongoDBConnectionInfo}
	     */
		public void setMongoDBConnInfo(MongoDBConnectionInfo mongoDBConnInfo) {
		 	  this.mongoDBConnInfo = mongoDBConnInfo;
		 }
		
		/**
	     * @return resource derivation method {@link MorphiaResourceDerivation}
	     */
		public MorphiaResourceDerivation getResourceDerivation() {
			return resourceDerivation;
		}
		
		/**
	     * Gets the current used resource derivation method {@link MorphiaResourceDerivation}
	     * <p />
	     * The resource Derivation specifies how the resource should be mined out of the trace log
	     */
		public void setResourceDerivation(MorphiaResourceDerivation resourceDerivation) {
			this.resourceDerivation = resourceDerivation;
		}

		/**
	     * @return creates a DAO to access and query the Morphia-TraceLog (without creating a new database if the current is not available) 
	     */
		public MorphiaTraceEventMessageDAO createMorphiaTraceEventMessageDAO(){
			return this.createMorphiaTraceEventMessageDAO(false);
		}
		
		/**
		 * Gets a flag if the database should be created if the current db is not available
		 * 
	     * @return creates a DAO to access and query the Morphia-TraceLog (without creating a new database if the current is not available) 
	     */
		public MorphiaTraceEventMessageDAO createMorphiaTraceEventMessageDAO(boolean createDBAllowed) {
	 		 MorphiaTraceEventMessageDAO traceMessageDAO = null;
	 		 
	 		 //reset last error message
	 		 this.errorMessage = null;
	 		 
	 		  try {
	 			 //if already connected don't try again
	 			 if(!this.connected) {
			 		  Datastore datastore=null;
				
		 			  //create a new morphia instance
					  this.morphia = new Morphia();			  
					  
					  //create a new mongo instance for given host name and port
	 				  this.mongo = new Mongo(this.mongoDBConnInfo.getHostname(),this.mongoDBConnInfo.getPort());
				    	
	 				  //checks if it's allowed to create a new db, if not we have to check manually if the database doesn't exists
	 				  if(!createDBAllowed) {
	 					  //check if database if available, if not we throw an exception
		 				  if(!this.mongo.getDatabaseNames().contains(this.mongoDBConnInfo.getDatabase())){
		 					  throw new Exception("Database '" + this.mongoDBConnInfo.getDatabase() + "' is unknown.");
		 				  }
	 				  } 
			 		  
			 		  //if secure mode is given -> use credentials
	 				  if(mongoDBConnInfo.getCredentials().isSecureMode()) {
	 					  //create new datastore: try to connect to mongo-db via morphia with given credentials
	 					  datastore = new DatastoreImpl(this.morphia, this.mongo, this.mongoDBConnInfo.getDatabase(), this.mongoDBConnInfo.getCredentials().getUsername(), this.mongoDBConnInfo.getCredentials().getPassword());
	 				  } else {
	 					//create new datastore: try to connect to mongo-db via morphia without credentials
	 					  datastore = new DatastoreImpl(this.morphia, this.mongo, this.mongoDBConnInfo.getDatabase(), null, null);
	 				  }	
				    
	 				  //create new dao with freshly created datastore instance
	 				  traceMessageDAO = new MorphiaTraceEventMessageDAO(datastore);
	 				  
	 				  this.currentDAO = traceMessageDAO;
	 				  
	 				  this.connected = true;
	 			  } else {
	 				  traceMessageDAO = this.currentDAO;
	 			  }
	 			 
 				  return  traceMessageDAO;
		 		} 
		 		catch (Exception e) {	
		 			//if something has failed (e.g. unable to connect on given port, ...) set error message and throw exception 
		 			this.errorMessage = e.getMessage();
		 			throw new RuntimeException(e);
				}
		 }
		
		/**
	     * @return true if the attempt to connect via morphia was successful else false
	     */
		public boolean tryToConnectViaMorphia() {
			Datastore datastore=null;
			  
			 //create a new morphia instance
			this.morphia = new Morphia();			  
			
			try {
				//create a new mongo instance for given host name and port
				this.mongo = new Mongo(this.mongoDBConnInfo.getHostname(),this.mongoDBConnInfo.getPort());
			    
				//if secure mode is given -> use credentials
				if(mongoDBConnInfo.getCredentials().isSecureMode()) {
					//create new datastore: try to connect to mongo-db via morphia with given credentials
	 				datastore = new DatastoreImpl(this.morphia, this.mongo, this.mongoDBConnInfo.getDatabase(), this.mongoDBConnInfo.getCredentials().getUsername(), this.mongoDBConnInfo.getCredentials().getPassword());
				} else {
					//create new datastore: try to connect to mongo-db via morphia without credentials
	 				datastore = new DatastoreImpl(this.morphia, this.mongo, this.mongoDBConnInfo.getDatabase(), null, null);	  
				}
				
				//create new dao with freshly created datastore instance
			    this.currentDAO = new MorphiaTraceEventMessageDAO(datastore);
				
				//attempt was successful
				this.connected = true;
			} catch(Exception e) {
				//attempt was unsuccessful
				this.errorMessage = e.getMessage();
				this.connected = false;
			}
			
			return this.connected;
		}
		
		/**
	     * @return list of available databases for given mongo server instance or NULL if not connected
	     */
		public List<String> getAvailableDatabaseList(){
			return (this.connected ? this.mongo.getDatabaseNames() : null);
		}
		
		/**
	     * @return true if some error exceeded, else false
	     */ 
		public boolean isError(){
			return (this.errorMessage != null);
		}
		
		/**
	     * @return last error message as string
	     */ 
		public String getErrorMessage(){
			return this.errorMessage;
		}
}
