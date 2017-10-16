
package org.mule.module.cmis.adapters;

import javax.annotation.Generated;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.routing.filter.Filter;
import org.mule.module.cmis.CMISCloudConnector;
import org.mule.module.cmis.process.ProcessAdapter;
import org.mule.module.cmis.process.ProcessCallback;
import org.mule.module.cmis.process.ProcessTemplate;
import org.mule.module.cmis.process.ProcessTemplate;


/**
 * A <code>CMISCloudConnectorProcessAdapter</code> is a wrapper around {@link CMISCloudConnector } that enables custom processing strategies.
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class CMISCloudConnectorProcessAdapter
    extends CMISCloudConnectorLifecycleAdapter
    implements ProcessAdapter<CMISCloudConnectorCapabilitiesAdapter>
{


    public<P >ProcessTemplate<P, CMISCloudConnectorCapabilitiesAdapter> getProcessTemplate() {
        final CMISCloudConnectorCapabilitiesAdapter object = this;
        return new ProcessTemplate<P,CMISCloudConnectorCapabilitiesAdapter>() {


            @Override
            public P execute(ProcessCallback<P, CMISCloudConnectorCapabilitiesAdapter> processCallback, MessageProcessor messageProcessor, MuleEvent event)
                throws Exception
            {
                return processCallback.process(object);
            }

            @Override
            public P execute(ProcessCallback<P, CMISCloudConnectorCapabilitiesAdapter> processCallback, Filter filter, MuleMessage message)
                throws Exception
            {
                return processCallback.process(object);
            }

        }
        ;
    }

}
