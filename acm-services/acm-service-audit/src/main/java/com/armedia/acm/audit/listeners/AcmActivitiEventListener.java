package com.armedia.acm.audit.listeners;

import com.armedia.acm.audit.model.AuditConstants;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.audit.service.AuditService;
import com.armedia.acm.web.api.MDCConstants;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Activiti event listener logs Activiti events.
 * <p>
 * Created by Bojan Milenkoski on 21.1.2016.
 */
public class AcmActivitiEventListener implements ActivitiEventListener
{
    private static final String EVENT_TYPE = "com.armedia.acm.audit.activiti.event";

    private Logger log = LoggerFactory.getLogger(getClass());

    private AuditService auditService;
    private boolean activityEventsLoggingEnabled;

    @Override
    public void onEvent(ActivitiEvent event)
    {
        log.debug("Activity event handling");

        switch (event.getType())
        {
        case JOB_EXECUTION_FAILURE:
        case ACTIVITY_ERROR_RECEIVED:
            audit(event, AuditConstants.EVENT_RESULT_FAILURE);
            break;

        case MEMBERSHIP_CREATED:
        case MEMBERSHIP_DELETED:
        case MEMBERSHIPS_DELETED:
        case TASK_ASSIGNED:
        case TASK_COMPLETED:
        case ACTIVITY_COMPLETED:
        case JOB_EXECUTION_SUCCESS:
            audit(event, AuditConstants.EVENT_RESULT_SUCCESS);
            break;

        default:
            break;
        }
    }

    private void audit(ActivitiEvent event, String eventResult)
    {
        if (isActivityEventsLoggingEnabled())
        {
            AuditEvent auditEvent = new AuditEvent();

            auditEvent.setIpAddress(MDC.get(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY));
            // when entity is changed without web request the MDC.get(AuditConstants.EVENT_MDC_REQUEST_ID_KEY) is null
            auditEvent.setRequestId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_ID_KEY) == null ? null
                    : UUID.fromString(MDC.get(MDCConstants.EVENT_MDC_REQUEST_ID_KEY)));
            auditEvent.setUserId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY) != null
                    ? MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY) : "anonymous");
            auditEvent.setFullEventType(EVENT_TYPE + " | " + event.getType().name());
            auditEvent.setEventResult(eventResult);
            auditEvent.setObjectType(AuditConstants.EVENT_OBJECT_TYPE_ACTIVITI_EVENT);
            auditEvent.setStatus(AuditConstants.EVENT_STATUS_COMPLETE);

            // Activiti events don't contain event date, so we set current date
            auditEvent.setEventDate(new Date());

            // event properties
            Map<String, String> eventProperties = new HashMap<>();

            Map<String, Object> processVariables = null;
            if (event.getExecutionId() != null)
            {
                processVariables = event.getEngineServices().getRuntimeService().getVariables(event.getExecutionId());
                eventProperties.putAll(processVariables.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString())));
            }

            auditEvent.setEventProperties(eventProperties);

            if (log.isTraceEnabled())
            {
                log.trace("Activiti AuditEvent: " + auditEvent.toString());
            }

            getAuditService().audit(auditEvent);
        }
    }

    @Override
    public boolean isFailOnException()
    {
        return false;
    }

    public AuditService getAuditService()
    {
        return auditService;
    }

    public void setAuditService(AuditService auditService)
    {
        this.auditService = auditService;
    }

    public boolean isActivityEventsLoggingEnabled()
    {
        return activityEventsLoggingEnabled;
    }

    public void setActivityEventsLoggingEnabled(boolean activityEventsLoggingEnabled)
    {
        this.activityEventsLoggingEnabled = activityEventsLoggingEnabled;
    }
}
