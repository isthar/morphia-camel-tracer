/**
 * 
 */
package org.apache.camel.processor.interceptor.morphia.caseidgenerator;

/**
 * Class which implements the interface {@link CaseIdGenerator}
 * <p/>
 * Generator creates the next sequenced id starting by the default value and using the information from the outer class
 * 
 * @version 
 * @author T. Neuboeck (2011)
 */
public class SequenceCaseIdGenerator implements CaseIdGenerator {
	//default value for the generator
	private static int DEFAULTID = 1;
	
	/* (non-Javadoc)
	 * @see org.apache.camel.processor.interceptor.morphia.caseidgenerator.CaseIdGenerator#generate(java.lang.String)
	 */
	/**
     * Gets the last known case id from the outside object(caller) to avoid generating the identical id.
     *
     * @return new sequence ID
     */
	@Override
	public Integer generate(String last) {
		//initialize id with the default value
		Integer newID = new Integer(SequenceCaseIdGenerator.DEFAULTID);
		try {
			//check if we have got an information from outside
			if(last != null) {
				//increment outside given information
				Integer lastID = new Integer(last);
				newID = lastID + 1;
			} 
		} catch (Exception e) {
			throw new RuntimeException(e); 
		}
		
		return newID;
	}

}
