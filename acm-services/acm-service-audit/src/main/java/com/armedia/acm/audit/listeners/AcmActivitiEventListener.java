package com.armedia.acm.audit.listeners;

/*-
 * #%L
 * ACM Service: Audit Library
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.activiti.model.SpringActivitiEvent;
import com.armedia.acm.audit.model.AuditConfig;
import com.armedia.acm.audit.model.AuditConstants;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.audit.service.AuditService;
import com.armedia.acm.web.api.MDCConstants;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.slf4j.MDC;
import org.springframework.context.ApplicationListener;

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
public class AcmActivitiEventListener implements ApplicationListener<SpringActivitiEvent>
{
    private static final String EVENT_TYPE = "com.armedia.acm.audit.activiti.event";

    private Logger log = LogManager.getLogger(getClass());

    private AuditService auditService;

    private AuditConfig auditConfig;

    @Override
    public void onApplicationEvent(SpringActivitiEvent springActivitiEvent)
    {

        ActivitiEvent event = (ActivitiEvent) springActivitiEvent.getSource();

        log.trace("Activiti event handling, event type {}", event.getType());

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
        if (getAuditConfig().getActivitiLoggingEntityEventsEnabled())
        {
            AuditEvent auditEvent = new AuditEvent();

            auditEvent.setIpAddress(MDC.get(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY));
            // when entity is changed without web request the MDC.get(AuditConstants.EVENT_MDC_REQUEST_ID_KEY) is null
            auditEvent.setRequestId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_ID_KEY) == null ? null
                    : UUID.fromString(MDC.get(MDCConstants.EVENT_MDC_REQUEST_ID_KEY)));
            auditEvent.setUserId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY) != null
                    ? MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY)
                    : AuditConstants.USER_ID_ANONYMOUS);
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
                eventProperties.putAll(
                        processVariables.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString())));
            }
            auditEvent.setEventProperties(eventProperties);

            log.trace("Activiti AuditEvent: {}", auditEvent);

            getAuditService().audit(auditEvent);
        }
    }

    public AuditService getAuditService()
    {
        return auditService;
    }

    public void setAuditService(AuditService auditService)
    {
        this.auditService = auditService;
    }

    public AuditConfig getAuditConfig()
    {
        return auditConfig;
    }

    public void setAuditConfig(AuditConfig auditConfig)
    {
        this.auditConfig = auditConfig;
    }
}
