package com.armedia.acm.audit;


import com.armedia.acm.event.AcmEvent;
import com.armedia.commons.audit.AuditActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class AuditWriter implements ApplicationListener<AcmEvent>
{
    private Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public void onApplicationEvent(AcmEvent acmEvent)
    {
        if ( log.isTraceEnabled() && acmEvent != null )
        {
            log.trace(acmEvent.getUserId() + " at " + acmEvent.getEventDate() + " executed " + acmEvent.getEventType() +
                    " " + (acmEvent.isSucceeded() ? "" : "un") + "successfully.");
        }

        if ( isAuditable(acmEvent) )
        {
            AuditActivity.Parameter ipAddress = new AuditActivity.Parameter("ipAddress", acmEvent.getIpAddress());
            AuditActivity.audit(
                    acmEvent.getEventType(),
                    acmEvent.getUserId() + "|" + acmEvent.getEventType(), // trackId
                    acmEvent.getUserId(),
                    acmEvent.isSucceeded() ? "success" : "failure",
                    ipAddress);
        }

    }

    /**
     * Ensure the non-nullable database fields are set.
     * @param acmEvent the event to be audited
     * @return whether to audit this event
     */
    private boolean isAuditable(AcmEvent acmEvent)
    {
        return acmEvent != null &&
                acmEvent.getUserId() != null &&
                ! acmEvent.getUserId().trim().isEmpty() &&
                acmEvent.getEventDate() != null &&
                acmEvent.getEventType() != null;

    }


}
