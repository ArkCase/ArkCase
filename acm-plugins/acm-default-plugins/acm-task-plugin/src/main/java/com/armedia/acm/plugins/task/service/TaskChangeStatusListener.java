package com.armedia.acm.plugins.task.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import org.mule.DefaultMuleMessage;
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

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private MuleContextManager muleContextManager;
    private AcmTaskService acmTaskService;


    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(
            AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

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
                    Map<String, Object> messageProps = new HashMap<>();
                    messageProps.put("auditPropertyEntityAdapter", getAuditPropertyEntityAdapter());
                    messageProps.put("acmTaskService", getAcmTaskService());

                    MuleMessage request = new DefaultMuleMessage(event, messageProps, getMuleContextManager().getMuleContext());

                    MuleMessage msg = getMuleContextManager().getMuleClient().send("jms://copyTaskFilesAndFoldersToParent.in", request);

                    MuleException e = msg.getInboundProperty("executionException");

                    if (e != null)
                    {
                        throw e;
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

    public AcmTaskService getAcmTaskService()
    {
        return acmTaskService;
    }

    public void setAcmTaskService(AcmTaskService acmTaskService)
    {
        this.acmTaskService = acmTaskService;
    }
}
