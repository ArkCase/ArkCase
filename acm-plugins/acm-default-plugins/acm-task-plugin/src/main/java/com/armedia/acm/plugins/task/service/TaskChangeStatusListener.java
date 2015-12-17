package com.armedia.acm.plugins.task.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nikolche
 */
public class TaskChangeStatusListener implements ApplicationListener<AcmApplicationTaskEvent>
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private MuleContextManager muleContextManager;

    @Override
    public void onApplicationEvent(AcmApplicationTaskEvent event)
    {

        if (event != null)
        {

            boolean execute = checkExecution(event.getEventType());

            if (execute)
            {
                try
                {
                    // call Mule flow to create the Alfresco folder
                    MuleMessage msg = getMuleContextManager().send("jms://copyTaskFilesAndFoldersToParent.in", event);

                    // TODO: this is fix for bug EDTRM-178 (workaround). We should see why on this point msg is null (maybe it's normal behaviour?)
                    if (msg != null)
                    {
                        MuleException e = msg.getInboundProperty("executionException");

                        if (e != null)
                        {
                            throw e;
                        }
                    }

                } catch (MuleException e)
                {
                    throw new RuntimeException("Error while copying Task documents.", e);
                }


            }
        }
    }

    private boolean checkExecution(String eventType)
    {

        return "com.armedia.acm.app.task.complete".equals(eventType) || "com.armedia.acm.activiti.task.complete".equals(eventType);
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

}
