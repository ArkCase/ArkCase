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

import com.armedia.acm.activiti.model.SpringActivitiEntityEvent;
import com.armedia.acm.audit.model.AuditConfig;
import com.armedia.acm.audit.model.AuditConstants;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.audit.service.AuditService;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.web.api.AsyncApplicationListener;
import com.armedia.acm.web.api.MDCConstants;

import org.activiti.engine.delegate.event.ActivitiEntityEvent;
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
 * Activiti entity changes listener. Logs entity changes.
 * <p>
 * Created by Bojan Milenkoski on 21.1.2016.
 */
@AsyncApplicationListener
public class AcmActivitiEntityEventListener implements ApplicationListener<SpringActivitiEntityEvent>
{
    private static final String EVENT_TYPE = "com.armedia.acm.audit.activiti.entity.event";

    private Logger log = LogManager.getLogger(getClass());

    private AuditService auditService;

    private AuditConfig auditConfig;

    private ObjectConverter objectConverter;

    @Override
    public void onApplicationEvent(SpringActivitiEntityEvent springActivitiEntityEvent)
    {
        ActivitiEvent event = (ActivitiEvent) springActivitiEntityEvent.getSource();

        switch (springActivitiEntityEvent.getEventType())
        {
        case "create":
            onCreate(event);
            break;
        case "update":
            onUpdate(event);
            break;
        case "delete":
            onDelete(event);
            break;
        default:
            break;
        }

    }

    protected void onCreate(ActivitiEvent event)
    {
        log.debug("Activiti entity created event handling");

        audit(event, AuditConstants.EVENT_STATUS_ACTIVITI_ENTITY_CREATED);
    }

    protected void onDelete(ActivitiEvent event)
    {
        log.debug("Activiti entity deleted event handling");

        audit(event, AuditConstants.EVENT_STATUS_ACTIVITI_ENTITY_DELETED);
    }

    protected void onUpdate(ActivitiEvent event)
    {
        log.debug("Activiti entity update event handling");

        audit(event, AuditConstants.EVENT_STATUS_ACTIVITI_ENTITY_UPDATED);
    }

    private void audit(ActivitiEvent event, String eventStatus)
    {
        if (!(event instanceof ActivitiEntityEvent))
        {
            return;
        }

        ActivitiEntityEvent activitiEntityEvent = (ActivitiEntityEvent) event;

        if (auditConfig.getActivitiLoggingEntityEventsEnabled())
        {
            Object entity = activitiEntityEvent.getEntity();

            AuditEvent auditEvent = new AuditEvent();

            Map<String, Object> processVariables = null;
            if (event.getExecutionId() != null)
            {
                processVariables = event.getEngineServices().getRuntimeService().getVariables(event.getExecutionId());
            }
            setAuditEventProperties(auditEvent, entity, processVariables);
            auditEvent.setStatus(eventStatus);

            log.trace("Activiti entity AuditEvent: {}", auditEvent.toString());

            getAuditService().audit(auditEvent);
        }
    }

    private void setAuditEventProperties(AuditEvent auditEvent, Object object, Map<String, Object> processVariables)
    {
        auditEvent.setIpAddress(MDC.get(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY));
        // when entity is changed without web request the MDC.get(AuditConstants.EVENT_MDC_REQUEST_ID_KEY) is null
        auditEvent.setRequestId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_ID_KEY) == null ? null
                : UUID.fromString(MDC.get(MDCConstants.EVENT_MDC_REQUEST_ID_KEY)));
        auditEvent
                .setUserId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY) != null ? MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY)
                        : AuditConstants.USER_ID_ANONYMOUS);
        auditEvent.setFullEventType(EVENT_TYPE + " | " + object.getClass().getName());
        auditEvent.setEventResult(AuditConstants.EVENT_RESULT_SUCCESS);
        auditEvent.setObjectType(AuditConstants.EVENT_OBJECT_TYPE_ACTIVITI_ENTITY);
        String id;
        try
        {
            id = (String) object.getClass().getMethod("getId").invoke(object);
            auditEvent.setObjectId(Long.decode(id));
        }
        catch (Exception e)
        {
            // object doesn't have String getId() method or the id cannot be cast to Long, we'll use the default -1L
            log.debug("Object of class: " + object.getClass().getName() + " doesn't have getId() method!");
            auditEvent.setObjectId(-1L);
        }

        // Activiti events don't contain event date, so we set current date
        auditEvent.setEventDate(new Date());

        // event properties
        Map<String, String> eventProperties = new HashMap<>();

        if (processVariables != null)
        {
            eventProperties.putAll(processVariables.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> (e.getValue() == null) ? "null" : e.getValue().toString())));
        }

        if (getAuditConfig().isActivitiLoggingEntityEventsObjectEnabled())
        {
            // Convert Object to JSON string
            eventProperties.put("Object", getObjectConverter().getJsonMarshaller().marshal(object));
        }

        auditEvent.setEventProperties(eventProperties);
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

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

}
