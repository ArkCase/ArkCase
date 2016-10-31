package com.armedia.acm.audit.listeners;

import com.armedia.acm.audit.model.AuditConstants;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.audit.service.AuditService;
import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.web.api.AsyncApplicationListener;
import com.armedia.acm.web.api.MDCConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationListener;

import java.util.Date;
import java.util.UUID;

@AsyncApplicationListener
public class AuditWriter implements ApplicationListener<AcmEvent>
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AuditService auditService;

    @Override
    public void onApplicationEvent(AcmEvent acmEvent)
    {
        if (log.isTraceEnabled() && acmEvent != null)
        {
            log.trace(acmEvent.getUserId() + " at " + acmEvent.getEventDate() + " executed " + acmEvent.getEventType() + " "
                    + (acmEvent.isSucceeded() ? "" : "un") + "successfully.");
        }

        if (isAuditable(acmEvent))
        {
            AuditEvent auditEvent = new AuditEvent();
            auditEvent.setEventDate(new Date(System.currentTimeMillis()));
            auditEvent.setUserId(acmEvent.getUserId());
            auditEvent.setIpAddress(MDC.get(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY));
            // when database is changed without web request the MDC.get(AuditConstants.EVENT_MDC_REQUEST_ID_KEY) is null
            auditEvent.setRequestId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_ID_KEY) == null ? null
                    : UUID.fromString(MDC.get(MDCConstants.EVENT_MDC_REQUEST_ID_KEY)));
            auditEvent.setFullEventType(acmEvent.getEventType());
            auditEvent.setEventDescription(acmEvent.getEventDescription());
            auditEvent.setTrackId(acmEvent.getUserId() + "|" + acmEvent.getEventType());
            auditEvent.setEventResult(acmEvent.isSucceeded() ? AuditConstants.EVENT_RESULT_SUCCESS : AuditConstants.EVENT_RESULT_FAILURE);
            auditEvent.setObjectId(acmEvent.getObjectId());
            auditEvent.setObjectType(acmEvent.getObjectType());
            auditEvent.setParentObjectId(acmEvent.getParentObjectId());
            auditEvent.setParentObjectType(acmEvent.getParentObjectType());
            auditEvent.setIpAddress(acmEvent.getIpAddress());
            auditEvent.setStatus("DRAFT");

            auditService.audit(auditEvent);

        } else
        {
            if (log.isErrorEnabled())
                log.error("Event " + acmEvent.getEventType() + " is not auditable");
        }

    }

    /**
     * Ensure the non-nullable database fields are set.
     *
     * @param acmEvent
     *            the event to be audited
     * @return whether to audit this event
     */
    private boolean isAuditable(AcmEvent acmEvent)
    {
        return acmEvent != null && acmEvent.getUserId() != null && !acmEvent.getUserId().trim().isEmpty() && acmEvent.getEventDate() != null
                && acmEvent.getEventType() != null;

    }

    public void setAuditService(AuditService auditService)
    {
        this.auditService = auditService;
    }
}
