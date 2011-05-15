package org.apache.camel.processor.interceptor.morphia.caseidgenerator;

/**
 * Interface used to represent an abstract CaseID Generator
 * @version 
 * @author T. Neuboeck (2011)
 */
public interface CaseIdGenerator {
	/**
     * Gets the last known case id from the outside object(caller) to avoid generating the identical id.
     *
     * @return new ObjectID
     */
	public Object generate(String last); 
}
