/**
 * 
 */
package org.apache.camel.processor.interceptor.morphia.caseidgenerator;

import java.util.UUID;

/**
 * Class which implements the interface {@link CaseIdGenerator}
 * <p/>
 * Generator creates a random UUID using {@link java.util.UUID}
 * 
 * @version 
 * @author T. Neuboeck (2011)
 */
public class UUIDCaseIDGenerator implements CaseIdGenerator {

	/* (non-Javadoc)
	 * @see org.apache.camel.processor.interceptor.morphia.caseidgenerator.CaseIdGenerator#generate(java.lang.String)
	 */
	/**
     * Gets the last known case id from the outside object(caller) and ignore it (because generator is working randomly)
     *
     * @return new UUID
     */
	@Override
	public UUID generate(String last) {
		return UUID.randomUUID();
	}

}
