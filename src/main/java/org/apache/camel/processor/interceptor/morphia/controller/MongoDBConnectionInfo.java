/**
 * 
 */
package org.apache.camel.processor.interceptor.morphia.controller;

import org.apache.camel.processor.interceptor.morphia.caseidgenerator.CaseIdGenerator;


/**
 * Simple POJO which stores all necessary information to connect to a mongo db instance 
 * 
 * @version 
 * @author T. Neuboeck (2011)
 */
public class MongoDBConnectionInfo {
		//default values for hostname, port and database-id
		private static final String DEFAULT_HOSTNAME = "localhost";
		private static final int DEFAULT_PORT = 27017;
		private static final String DEFAULT_DB = "traceDB";
		
		private String hostname;				
		private int port;							
		private String database;
		//credentials are stored in a seperated pojo
		private MongoDBCredentials credentials;
		
		/**
	     * Default constructor for the pojo
	     */
		public MongoDBConnectionInfo() {
				//initialize with default values
				this.hostname = MongoDBConnectionInfo.DEFAULT_HOSTNAME;
				this.port = MongoDBConnectionInfo.DEFAULT_PORT;
				this.database = MongoDBConnectionInfo.DEFAULT_DB;
		}
		
		/**
	     * @return current host name
	     */
		public String getHostname() {
			return this.hostname;
		}
		
		/**
	     * Gets the host name as string
	     */
		public void setHostname(String hostname) {
			this.hostname = hostname;
		}
		
		/**
	     * @return current port
	     */
		public int getPort() {
			return this.port;
		}
		
		/**
	     * Gets the port as int
	     */
		public void setPort(int port) {
			this.port = port;
		}
		
		/**
	     * @return current database
	     */
		public String getDatabase() {
			return this.database;
		}
		
		/**
	     * Gets the port as string
	     */
		public void setDatabase(String database) {
			this.database = database;
		}
		
		/**
	     * @return current credentials
	     */
		public MongoDBCredentials getCredentials() {
			return this.credentials;
		}
		
		/**
	     * Gets the credentials as {@link MongoDBCredentials}
	     */
		public void setCredentials(MongoDBCredentials credentials) {
			this.credentials = credentials;
		}
}
