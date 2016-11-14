
package org.mule.module.cmis.connectivity;

import javax.annotation.Generated;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.routing.filter.Filter;
import org.mule.module.cmis.adapters.CMISCloudConnectorConnectionIdentifierAdapter;
import org.mule.module.cmis.connection.ConnectionManager;
import org.mule.module.cmis.process.ManagedConnectionProcessInterceptor;
import org.mule.module.cmis.process.ProcessCallback;
import org.mule.module.cmis.process.ProcessCallbackProcessInterceptor;
import org.mule.module.cmis.process.ProcessInterceptor;
import org.mule.module.cmis.process.ProcessTemplate;
import org.mule.module.cmis.process.RetryProcessInterceptor;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class ManagedConnectionProcessTemplate<P >implements ProcessTemplate<P, CMISCloudConnectorConnectionIdentifierAdapter>
{

    private final ProcessInterceptor<P, CMISCloudConnectorConnectionIdentifierAdapter> processInterceptor;

    public ManagedConnectionProcessTemplate(ConnectionManager<CMISCloudConnectorConnectionKey, CMISCloudConnectorConnectionIdentifierAdapter> connectionManager, MuleContext muleContext) {
        ProcessInterceptor<P, CMISCloudConnectorConnectionIdentifierAdapter> processCallbackProcessInterceptor = new ProcessCallbackProcessInterceptor<P, CMISCloudConnectorConnectionIdentifierAdapter>();
        ProcessInterceptor<P, CMISCloudConnectorConnectionIdentifierAdapter> managedConnectionProcessInterceptor = new ManagedConnectionProcessInterceptor<P>(processCallbackProcessInterceptor, connectionManager, muleContext);
        ProcessInterceptor<P, CMISCloudConnectorConnectionIdentifierAdapter> retryProcessInterceptor = new RetryProcessInterceptor<P, CMISCloudConnectorConnectionIdentifierAdapter>(managedConnectionProcessInterceptor, muleContext, connectionManager.getRetryPolicyTemplate());
        processInterceptor = retryProcessInterceptor;
    }

    public P execute(ProcessCallback<P, CMISCloudConnectorConnectionIdentifierAdapter> processCallback, MessageProcessor messageProcessor, MuleEvent event)
        throws Exception
    {
        return processInterceptor.execute(processCallback, null, messageProcessor, event);
    }

    public P execute(ProcessCallback<P, CMISCloudConnectorConnectionIdentifierAdapter> processCallback, Filter filter, MuleMessage message)
        throws Exception
    {
        return processInterceptor.execute(processCallback, null, filter, message);
    }

}
