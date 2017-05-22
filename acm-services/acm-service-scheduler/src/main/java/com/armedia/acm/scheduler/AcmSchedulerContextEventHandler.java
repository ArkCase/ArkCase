package com.armedia.acm.scheduler;

import com.armedia.acm.spring.events.AbstractContextHolderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;


/**
 * Reconfigure the scheduler when a Spring context is added or removed.  This lets the scheduler call a scheduled
 * bean which is defined in a child context (that is - a bean defined in a file in .arkcase/acm/spring folder).
 */
public class AcmSchedulerContextEventHandler implements ApplicationListener<AbstractContextHolderEvent>
{
    private AcmScheduler scheduler;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(AbstractContextHolderEvent abstractContextHolderEvent)
    {
        log.info("Context named [{}] was added or removed - updating scheduler configuration", abstractContextHolderEvent.getContextName());
        getScheduler().updateConfiguration();
    }

    public AcmScheduler getScheduler()
    {
        return scheduler;
    }

    public void setScheduler(AcmScheduler scheduler)
    {
        this.scheduler = scheduler;
    }
}
