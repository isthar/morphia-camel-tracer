/**
 * 
 */
package org.apache.camel.processor.interceptor.morphia.mockups;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.camel.Exchange;
import org.apache.camel.processor.interceptor.morphia.caseidgenerator.CaseIdGenerator;

/**
 * Class which implements the interface {@link Mockup}
 * <p/>
 * Purpose of the ResourceObject is to randomly add or remove resource information from a given exchange  {@link org.apache.camel.Exchange} 
 * This could be used to generate resource values for systems - which would need this information - but doesn't include this information in the exchange header yet  or just for testing purposes. 
 * 
 * @version 
 * @author T. Neuboeck (2011)
 */
public class ResourceMockup implements Mockup {

	//header property which should be used
	private static final String HEADER_FIELD = "RESOURCE_UID";
	//propability if a event should get an resource
	private static final double RESOURCE_PROB = 0.7;
	//list of resources
	private static final List RESOURCES = 
		   Arrays.asList(new String[]{"Mr. X", "Ms. Sample", "Mr. Anybody", "Ms. Somebody"});
	
	//use just the basic java.util.random
	private static Random r = new Random();
	
	/* (non-Javadoc)
	 * @see org.apache.camel.processor.interceptor.morphia.mockups.Mockup#execute(org.apache.camel.Exchange)
	 */
	/**
     * Gets an exchange object {@link org.apache.camel.Exchange} on which the mockup should apply his logic
     * <p />
     * The method generates based on the given probability a resource information (randomly) and add the resource value to the header or remove it from the exchange header
     */
	@Override
	public void execute(Exchange exchange) {
		//calculate if we add a resource or not (then it would be necessary to remove an existing)

	    if(r.nextInt(100) > (100- Math.round((100*RESOURCE_PROB)))){
	    	String resource = ResourceMockup.RESOURCES.get(r.nextInt(ResourceMockup.RESOURCES.size())).toString();
	    	exchange.getIn().setHeader(ResourceMockup.HEADER_FIELD, resource);
	    } else {
	    	exchange.getIn().removeHeader(ResourceMockup.HEADER_FIELD);
	    }
	}

}
