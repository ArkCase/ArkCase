package com.armedia.acm.audit;


import com.armedia.acm.event.AcmEvent;
import com.armedia.commons.audit.AuditActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;

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
            Map<String, String> params = new HashMap<>();
            params.put("ipAddress", acmEvent.getIpAddress());
            if ( acmEvent.getObjectId() != null )
            {
                params.put("objectId", String.valueOf(acmEvent.getObjectId()));
            }
            if ( acmEvent.getObjectType() != null )
            {
                params.put("objectType", acmEvent.getObjectType());
            }
            AuditActivity.audit(
                    acmEvent.getEventType(),
                    acmEvent.getUserId() + "|" + acmEvent.getEventType(), // trackId
                    acmEvent.getUserId(),
                    acmEvent.isSucceeded() ? "success" : "failure",
                    params);
        } else {
            if(log.isErrorEnabled())
                log.error("Event "+ acmEvent.getEventType()+" is not auditable");
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
