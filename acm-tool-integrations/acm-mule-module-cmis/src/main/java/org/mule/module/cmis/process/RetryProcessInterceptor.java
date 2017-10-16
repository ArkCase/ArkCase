
package org.mule.module.cmis.process;

import java.io.InterruptedIOException;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.retry.RetryPolicy;
import org.mule.api.retry.RetryPolicyTemplate;
import org.mule.api.routing.filter.Filter;
import org.mule.retry.DefaultRetryContext;
import org.mule.retry.PolicyStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class RetryProcessInterceptor<T, O >implements ProcessInterceptor<T, O>
{

    private static Logger logger = LoggerFactory.getLogger(RetryProcessInterceptor.class);
    private final ProcessInterceptor<T, O> next;
    private final RetryPolicyTemplate retryPolicyTemplate;
    private final Map<Object, Object> metaInfo = null;
    /**
     * Mule Context
     * 
     */
    protected MuleContext muleContext;

    public RetryProcessInterceptor(ProcessInterceptor<T, O> next, MuleContext muleContext, RetryPolicyTemplate retryPolicyTemplate) {
        this.next = next;
        this.muleContext = muleContext;
        this.retryPolicyTemplate = retryPolicyTemplate;
    }

    /**
     * Sets muleContext
     * 
     * @param value Value to set
     */
    public void setMuleContext(MuleContext value) {
        this.muleContext = value;
    }

    /**
     * Retrieves muleContext
     * 
     */
    public MuleContext getMuleContext() {
        return this.muleContext;
    }

    public T execute(ProcessCallback<T, O> processCallback, O object, MessageProcessor messageProcessor, MuleEvent event)
        throws Exception
    {
        RetryPolicy retryPolicy = retryPolicyTemplate.createRetryInstance();
        DefaultRetryContext retryContext = new DefaultRetryContext("Work Descriptor", metaInfo);
        retryContext.setMuleContext(muleContext);
        PolicyStatus status = null;
        T result = null;
        try {
            Exception cause = null;
            do {
                try {
                    result = this.next.execute(processCallback, object, messageProcessor, event);
                    if (retryPolicyTemplate.getNotifier()!= null) {
                        retryPolicyTemplate.getNotifier().onSuccess(retryContext);
                    }
                    return result;
                } catch (Exception e) {
                    cause = e;
                    if (logger.isDebugEnabled()) {
                        logger.debug(cause.getMessage(), cause);
                    }
                    if (retryPolicyTemplate.getNotifier()!= null) {
                        retryPolicyTemplate.getNotifier().onFailure(retryContext, cause);
                    }
                    boolean isManagedException = false;
                    if (processCallback.getManagedExceptions()!= null) {
                        for (Class exceptionClass: ((List<Class> ) processCallback.getManagedExceptions())) {
                            if (exceptionClass.isInstance(e)) {
                                isManagedException = true;
                                break;
                            }
                        }
                    }
                    if ((cause instanceof InterruptedException)||(cause instanceof InterruptedIOException)) {
                        logger.error("Process was interrupted (InterruptedException), ceasing process");
                        break;
                    } else {
                        if (isManagedException) {
                            status = retryPolicy.applyPolicy(cause);
                        } else {
                            status = PolicyStatus.policyExhausted(cause);
                        }
                    }
                }
            } while (status.isOk());
            if ((status!= null)&&(!status.isOk())) {
                retryContext.setFailed(cause);
                throw cause;
            }
        } finally {
            if ((status!= null)&&(status.getThrowable()!= null)) {
                if (logger.isDebugEnabled()) {
                    logger.debug(status.getThrowable().getMessage(), status.getThrowable());
                }
            }
        }
        return null;
    }

    public T execute(ProcessCallback<T, O> processCallback, O object, Filter filter, MuleMessage message)
        throws Exception
    {
        throw new UnsupportedOperationException();
    }

}
