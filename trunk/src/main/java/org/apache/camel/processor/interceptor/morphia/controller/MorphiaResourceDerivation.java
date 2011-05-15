package org.apache.camel.processor.interceptor.morphia.controller;

import org.apache.camel.processor.interceptor.morphia.MorphiaTraceEventMessage;

/**
 * Resource Derivation describe the method how the resource information should be derived out of the event log / events
 * <p />
 * 
 * The class support three different ways to derive the information about the resource out of the MorphiaTraceEventMessage
 * <ul>
 *   <li>DEFAULT  - each event of the log gets the same resource-information -> available information would be ignored</li>
 *   <li>ORIGINAL - using the original resource information per event -> if no information is available it will be null</li>
 *   <li>COMPLEMENT - using the original resource information per event if available, else we use the first mined resource information (has to be done from outside, object only will remember the first derivable resource)</li>
 * </ul>
 * @version 
 * @author T. Neuboeck (2011)
 */
public class MorphiaResourceDerivation {
	//default resource-value and default-resource id
	private static final String DEFAULT_RESOURCE = "UNKNOWN";
	private static final String DEFAULT_RESOURCEID = "RESOURCE_UID";
	
	//different derivation variants
	public enum MorphiaResourceDerivationMode {
		DEFAULT, ORIGINAL, COMPLEMENT
	}
	
	protected MorphiaResourceDerivationMode currentMode;
	protected String defaultResource;
	protected String defaultResourceId;
	protected String alreadyDerivedResource;
	
	/**
     * Default constructor for the derivation
     */
	public MorphiaResourceDerivation(){
		this.currentMode = MorphiaResourceDerivationMode.DEFAULT;
		this.defaultResource = MorphiaResourceDerivation.DEFAULT_RESOURCE;
		this.defaultResourceId = MorphiaResourceDerivation.DEFAULT_RESOURCEID;
		this.resetAlreadyDerivedResource();
	}

	/**
     * @return current derivation variant
     */
	public MorphiaResourceDerivationMode getCurrentMode() {
		return currentMode;
	}

	/**
     * Gets derivation variant {@link MorphiaResourceDerivationMode}
     */
	public void setCurrentMode(MorphiaResourceDerivationMode currentMode) {
		this.currentMode = currentMode;
	}

	/**
     * @return current default resource value as String
     */
	public String getDefaultResource() {
		return defaultResource;
	}

	/**
     * Gets default resource value
     */
	public void setDefaultResource(String defaultResource) {
		this.defaultResource = defaultResource;
	}

	/**
     * @return current default resource id as String
     */
	public String getDefaultResourceId() {
		return defaultResourceId;
	}

	/**
     * Gets default resource id
     */
	public void setDefaultResourceId(String defaultResourceId) {
		this.defaultResourceId = defaultResourceId;
	}

	/**
	 * Gets the message (event) for which the resource should be derived
	 * 
     * @return derived resource value
     */
	public String deriveResourceValue(MorphiaTraceEventMessage message) {
		String resource = "";
		
		//derive value differently for each mode
		switch (this.currentMode){
			case DEFAULT:
				//simply return the default resouce value
				resource = this.getDefaultResource();
				break;
			case ORIGINAL:
				//try to extract the original information (via given resource-id) out of the message
				resource = this.extractResourceIdentifierOutOfMessage(message, this.getDefaultResourceId());
				break;
			case COMPLEMENT:
				//try to extract the original information (via given resource-id) out of the message
				resource = this.extractResourceIdentifierOutOfMessage(message, this.getDefaultResourceId());
				
				//if no resource value is derivable check if we already know any value
				if(resource != null) {
					if(this.alreadyDerivedResource == null){
						this.alreadyDerivedResource = resource;
					}
				} else {
					//check if we know any value yet, else we use the current value as default values for following event w/o resource
					if(this.alreadyDerivedResource != null){
						resource = this.alreadyDerivedResource;
					} 
				}
	
				break;
		}
		return resource;
	}

	/**
     * Reset already known resource value to null
     */
	public void resetAlreadyDerivedResource(){
		this.alreadyDerivedResource = null;
	}
	
	/**
     * @return already known resource value as string
     */
	public String getAlreadyDerivedResource(){
		return this.alreadyDerivedResource;
	}
	
	/**
	 * Gets the message (event) for which the resource should be derived and the current resource-id
	 * 
     * @return extracted resource value
     */
	private String extractResourceIdentifierOutOfMessage(MorphiaTraceEventMessage message, String resourceIdentifier ) {
		
		//return null if no id was found
		String resource = null;
		
		//the input string looks like {Header1=text, Header2=text, Header3=text}
		//		1.step		remove curly brace
		//      2.step		split in single header info
		//		3.step		split single header info into id+value pair
		
		String[] temp = message.getHeaders().replace("{", "").replace("}", "").split(",");
		
		for (int i = 0; i<temp.length; i++) {
			String[] pair = temp[i].split("=");
			
			//we need a pair ...
			if(pair.length == 2) {
				if(pair[0].trim().equals(resourceIdentifier.trim())) {
					if(resource == null)
						resource = pair[1];
				}
			}
		}
		
		return resource;
	}
}
