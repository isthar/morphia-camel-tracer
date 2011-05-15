/**
 * 
 */
package org.apache.camel.processor.interceptor.morphia;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



import org.apache.camel.processor.Logger;
import org.apache.camel.processor.interceptor.TraceEventMessage;
import org.apache.camel.processor.interceptor.DefaultTraceEventMessage;
import org.apache.camel.processor.interceptor.TraceEventHandler;
import org.apache.camel.processor.interceptor.TraceInterceptor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;


import com.google.code.morphia.*;
import com.google.code.morphia.query.Query;
import com.mongodb.Mongo;

import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.util.CaseInsensitiveMap;
import org.apache.camel.util.IntrospectionSupport;
import org.apache.camel.util.ObjectHelper;

import org.apache.camel.processor.interceptor.morphia.dao.MorphiaTraceEventMessageDAO;
import org.apache.camel.processor.interceptor.morphia.mockups.Mockup;
import org.apache.camel.processor.interceptor.morphia.caseidgenerator.CaseIdGenerator;
import org.apache.camel.processor.interceptor.morphia.controller.MorphiaConnectionController;

/**
 * @author tneuboeck
 *
 */
public class MorphiaTraceEventHandler implements TraceEventHandler {
	  //private static final transient Logger LOG = LoggerFactory.getLogger(MyTraceEventHandler.class);
	  private static final String morphiaTraceEventMessageClassName = "org.apache.camel.processor.interceptor.morphia.MorphiaTraceEventMessage";
	  private Class<?> morphiaTraceEventMessageClass;
	  private MorphiaConnectionController morphiaController;
	  private CaseIdGenerator caseIdGenerator;
	  private List<Mockup> mockups;
	  
	  
	  public CaseIdGenerator getCaseIdGenerator() {
		  return caseIdGenerator;
	  }
		
	  public void setCaseIdGenerator(CaseIdGenerator caseIdGenerator) {
		  this.caseIdGenerator = caseIdGenerator;
	  }

	  public MorphiaConnectionController getMorphiaController() {
	  		return this.morphiaController;
	  }
	  
	  public void setMorphiaController(MorphiaConnectionController morphiaController) {
	  		this.morphiaController = morphiaController;
	  }
	  
	  public List<Mockup> getMockups() {
		  return mockups;
	  }
	
	  public void setMockups(List<Mockup> mockups) {
		  this.mockups = mockups;
	  }

	  private void executeMockups(Exchange exchange){
		  Iterator<Mockup> mockupIterator = mockups.iterator();
			
		  while(mockupIterator.hasNext()) {
			  mockupIterator.next().execute(exchange);
		  }
	  }
	  
	  public void traceExchange(ProcessorDefinition node, Processor target, TraceInterceptor traceInterceptor, Exchange exchange) throws Exception {
			  
			 MorphiaTraceEventMessageDAO traceMessageDAO = this.morphiaController.createMorphiaTraceEventMessageDAO(true);
			 Object currentInternalCaseID = null;
			 
			 this.executeMockups(exchange);
		
			 //no header set -> generate new id
			  if (exchange.getIn().getHeader("MORPHIATRACE_CASE_ID") == null ) {
				 
				 //get last internal Case ID (if null -> generate new; else -> generate new and check if unique -> use; else -> generate new ....)
				 Object lastExistingInternalCaseID = traceMessageDAO.getLastInternalCaseID();
				 String temp = null;
				 if(lastExistingInternalCaseID != null){
					 temp = lastExistingInternalCaseID.toString();
				 }
				 
				 Object generatedInternalCaseID = caseIdGenerator.generate(temp);
				 
				 //no existing interalCaseID -> first Case traced
				 if (lastExistingInternalCaseID != null) {
					 
					 while (traceMessageDAO.internalCaseIDExists(generatedInternalCaseID.toString())) {
						 generatedInternalCaseID = caseIdGenerator.generate(lastExistingInternalCaseID.toString());			  
					 }
				 }
				 
			  	 currentInternalCaseID = generatedInternalCaseID;
			  	 
			  	 //only set for the first message in the case		  
			   	 exchange.getIn().setHeader("MORPHIATRACE_CASE_ID",currentInternalCaseID);
			   	 
			  //header set -> receive existing
			  } else {
				  currentInternalCaseID =  exchange.getIn().getHeader("MORPHIATRACE_CASE_ID");
			  }
					  
		      Date timestamp = new Date();
			  Exchange event = new DefaultExchange(exchange);
			  event.setProperty(Exchange.TRACE_EVENT_NODE_ID, node.getId());
			  event.setProperty(Exchange.TRACE_EVENT_TIMESTAMP, timestamp);
			  event.setProperty(Exchange.TRACE_EVENT_EXCHANGE, exchange);
			          		          
			  TraceEventMessage msg = new DefaultTraceEventMessage(timestamp, node, exchange);
			 
			  // load the morphia event message class
			  loadMorphiaTraceEventMessageClass(exchange);
			   
			  // create a new instance of the event message class
			  Object morphiaMsg = ObjectHelper.newInstance(morphiaTraceEventMessageClass);
								
			  // copy options from event to morphiaMsg
			  Map<String, Object> options = new HashMap<String, Object>();
			  IntrospectionSupport.getProperties(msg, options, null);
			  IntrospectionSupport.setProperties(morphiaMsg, options);
			  // and set the timestamp as its not a String type
			  IntrospectionSupport.setProperty(morphiaMsg, "timestamp", msg.getTimestamp());
			   
			  event.getIn().setBody(morphiaMsg);
			
			  //set internal id & correlation id
			  ((MorphiaTraceEventMessage)morphiaMsg).setInternalCaseID(currentInternalCaseID.toString());
			  if ( exchange.getProperty(Exchange.CORRELATION_ID) != null ) {
				  ((MorphiaTraceEventMessage)morphiaMsg).setCorrelationID(exchange.getProperty(Exchange.CORRELATION_ID).toString());
			  }
			  
			  traceMessageDAO.saveMessage(morphiaMsg);
			  
			  event.setProperty(Exchange.TRACE_EVENT, Boolean.TRUE);			
		}

	  public Object traceExchangeIn(ProcessorDefinition node, Processor target, TraceInterceptor traceInterceptor, Exchange exchange) throws Exception {
			traceExchange(node,target,traceInterceptor,exchange);
			return null;
	  }


	  public void traceExchangeOut(ProcessorDefinition node, Processor target, TraceInterceptor traceInterceptor, Exchange exchange, Object traceState) throws Exception {
		 traceExchange(node,target,traceInterceptor,exchange);
	  }

	  private synchronized void loadMorphiaTraceEventMessageClass(Exchange exchange) {
	    if (morphiaTraceEventMessageClass == null) {
	        morphiaTraceEventMessageClass = exchange.getContext().getClassResolver().resolveClass(morphiaTraceEventMessageClassName);
	        if (morphiaTraceEventMessageClass == null) {
	            throw new IllegalArgumentException("Cannot find class: " + morphiaTraceEventMessageClassName
	                    + ". Make sure camel-jpa.jar is in the classpath.");
	        }
	    }
	  }
    
}
