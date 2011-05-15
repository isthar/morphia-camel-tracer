/**
 * 
 */
package org.apache.camel.processor.interceptor.morphia.controller;

/**
 * Simple POJO which stores mongo db credentials
 * 
 * @version 
 * @author T. Neuboeck (2011)
 */
public class MongoDBCredentials {
		private String username;
		private char[] password;
		
		/**
	     * @return current user name
	     */
		public String getUsername() {
			return this.username;
		}
		
		/**
	     * Gets the user name as string
	     */
		public void setUsername(String username) {
			this.username = username;
		}
		
		/**
	     * @return current password
	     */
		public char[] getPassword() {
			return this.password;
		}
		
		/**
	     * Gets the password as char[]
	     */
		public void setPassword(char[] password) {
			this.password = password;
		}
		
		/**
	     * TODO
	     */
		public boolean isSecureMode() {
				//TODO check if secure mode
				return false;
		}
}
