package com.armedia.acm.audit;


import com.armedia.acm.event.AcmEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class AuditWriter implements ApplicationListener<AcmEvent>
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(AcmEvent acmEvent)
    {
        log.info(acmEvent.getUserId() + " at " + acmEvent.getEventDate() + " executed " + acmEvent.getEventType() +
            " " + ( acmEvent.isSucceeded() ? "" : "un") + "successfully.");
    }
}
