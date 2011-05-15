/**
 * 
 */
package org.apache.camel.processor.interceptor.morphia.mockups;

import org.apache.camel.Exchange;

/**
 * Interface used to represent an abstract mockup object
 * @version 
 * @author T. Neuboeck (2011)
 */
public interface Mockup {
	/**
     * Gets an exchange object {@link org.apache.camel.Exchange} on which the mockup should apply his logic
     */
	 public void execute(Exchange exchange);
}
