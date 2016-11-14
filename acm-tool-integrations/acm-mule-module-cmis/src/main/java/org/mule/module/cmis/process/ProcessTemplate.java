
package org.mule.module.cmis.process;

import javax.annotation.Generated;


/**
 * ProcessTemplate provides a processing context for message processing.
 * <p/>
 * Examples of processing context can be to provide error handling, transaction state verification,
 * transactional demarcation, connection management, security, etc.
 * @param <T> type of the return value of the processing execution
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public interface ProcessTemplate<T,O >{

       public T execute(ProcessCallback<T, O> callback, org.mule.api.processor.MessageProcessor messageProcessor, org.mule.api.MuleEvent event) throws Exception;
    public T execute(ProcessCallback<T, O> callback, org.mule.api.routing.filter.Filter filter, org.mule.api.MuleMessage message) throws Exception;
}
