package com.armedia.acm.activiti;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Created by armdev on 6/24/14.
 */
public class AcmActivitiTaskEventListener implements ApplicationListener<AcmTaskActivitiEvent>
{
    private int timesCalled;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(AcmTaskActivitiEvent event)
    {
        log.info("Got an event: " + event.getEventType());
        ++timesCalled;
    }

    /**
     * To be called from the Activiti business process, to demonstrate we can call Spring beans from Activiti
     * @param message
     */
    public void logMessage(String message, String... messages)
    {
        log.info("Got a message: " + message);
        for (String m : messages)
        {
            log.info("\t" + m);
        }
    }

    public int getTimesCalled()
    {
        return timesCalled;
    }

    public void reset()
    {
        timesCalled = 0;
    }
}
