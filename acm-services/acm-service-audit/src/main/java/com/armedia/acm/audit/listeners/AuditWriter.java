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

import com.armedia.acm.audit.model.AuditConstants;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.audit.service.AuditService;
import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.web.api.AsyncApplicationListener;
import com.armedia.acm.web.api.MDCConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

@AsyncApplicationListener
public class AuditWriter implements ApplicationListener<ApplicationEvent>
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AuditService auditService;
    /**
     * Spring expression parser instance.
     */
    private ExpressionParser expressionParser;

    /**
     * Event description configuration file
     */
    private File eventDescriptionPropertiesFile;

    /**
     * (event name, parsed SpEL expression) pairs
     */
    private Map<String, Expression> expressionMap = new HashMap<>();

    /**
     * Load configuration from file and build SpEL expression map (bean init method)
     */
    public void loadConfiguration()
    {
        if (eventDescriptionPropertiesFile != null && eventDescriptionPropertiesFile.exists())
        {
            log.debug("Loading Audit event descriptions from configuration file [{}]", eventDescriptionPropertiesFile.getPath());
            Properties properties = new Properties();
            try (InputStream propertyStream = new FileInputStream(eventDescriptionPropertiesFile))
            {
                // use temporary map to avoid race conditions where new configuration is loaded
                // while audit event descriptions are evaluated at the same time
                Map<String, Expression> tempExpressionMap = new HashMap<>();
                // load the properties
                properties.load(propertyStream);
                for (String eventType : properties.stringPropertyNames())
                {
                    // try to parse SpEL expressions
                    String spelExpression = properties.getProperty(eventType);
                    if (spelExpression != null && !spelExpression.isEmpty())
                        try
                        {
                            Expression expression = expressionParser.parseExpression(spelExpression);
                            tempExpressionMap.put(eventType, expression);
                            log.debug("SpEL expression [{}] for event type [{}] added ", spelExpression, eventType);
                        }
                        catch (ParseException e)
                        {
                            log.error("Unable to parse SpEL expression [{}]", spelExpression);
                        }
                }
                // replace existing map
                expressionMap = tempExpressionMap;
            }
            catch (IOException e)
            {
                log.error("Unable to read configuration file [{}]", eventDescriptionPropertiesFile.getPath());
            }
        }
        else
        {
            // configuration file deleted, we're assuming that SpEL expression evaluation is no longer needed
            expressionMap.clear();
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent)
    {
        if (applicationEvent != null)
        {
            if (applicationEvent instanceof AcmEvent)
            {
                handleAuditEvent((AcmEvent) applicationEvent);
            }
            else if (applicationEvent instanceof AbstractConfigurationFileEvent)
            {
                handleConfigurationFileEvent((AbstractConfigurationFileEvent) applicationEvent);
            }
        }
    }

    /**
     * Handle audit events.
     * 
     * @param acmEvent
     *            application event that triggers an audit event
     */
    private void handleAuditEvent(AcmEvent acmEvent)
    {
        if (acmEvent != null)
        {
            log.trace("[{}] at [{}] executed [{}] [{}]", acmEvent.getUserId(), acmEvent.getEventDate(), acmEvent.getEventType(),
                    (acmEvent.isSucceeded() ? "" : "un") + "successfully.");
        }
        else
        {
            return;
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

            if (acmEvent.getEventDescription() != null)
            {
                auditEvent.setEventDescription(acmEvent.getEventDescription());
            }
            else
            {
                auditEvent.setEventDescription(evaluateEventDescription(acmEvent));
            }
            auditEvent.setTrackId(acmEvent.getUserId() + "|" + acmEvent.getEventType());
            auditEvent.setEventResult(acmEvent.isSucceeded() ? AuditConstants.EVENT_RESULT_SUCCESS : AuditConstants.EVENT_RESULT_FAILURE);
            auditEvent.setObjectId(acmEvent.getObjectId());
            auditEvent.setObjectType(acmEvent.getObjectType());
            auditEvent.setParentObjectId(acmEvent.getParentObjectId());
            auditEvent.setParentObjectType(acmEvent.getParentObjectType());
            auditEvent.setIpAddress(acmEvent.getIpAddress());
            auditEvent.setStatus("DRAFT");
            auditEvent.setDiffDetailsAsJson(acmEvent.getDiffDetailsAsJson());

            auditService.audit(auditEvent);
        }
        else
        {
            log.warn("Event [{}] is not auditable", acmEvent.getEventType());
        }
    }

    /**
     * Handle configuration file events
     * 
     * @param abstractConfigurationFileEvent
     *            configuration file related application event
     */
    private void handleConfigurationFileEvent(AbstractConfigurationFileEvent abstractConfigurationFileEvent)
    {
        if (abstractConfigurationFileEvent != null && abstractConfigurationFileEvent.getConfigFile() != null
                && abstractConfigurationFileEvent.getConfigFile().getPath()
                        .equals(eventDescriptionPropertiesFile.getPath()))
        {
            loadConfiguration();
        }
    }

    /**
     * Evaluate event description based on SpEL expression in properties
     * 
     * @param acmEvent
     *            the event that triggered the audit entry
     * @return evaluated event description, or null on failure
     */
    private String evaluateEventDescription(AcmEvent acmEvent)
    {
        String description = null;
        Expression expression = expressionMap.get(acmEvent.getEventType().toLowerCase());
        if (expression != null)
        {
            StandardEvaluationContext context = new StandardEvaluationContext(acmEvent);
            try
            {
                description = expression.getValue(context, String.class);
                log.trace("Audit event description [{}] generated for [{}]", description, acmEvent);
            }
            catch (EvaluationException e)
            {
                log.error("Unable to generate Audit event description for [{}]", acmEvent, e);
            }
        }
        return description;
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

    public ExpressionParser getExpressionParser()
    {
        return expressionParser;
    }

    public void setExpressionParser(ExpressionParser expressionParser)
    {
        this.expressionParser = expressionParser;
    }

    public File getEventDescriptionPropertiesFile()
    {
        return eventDescriptionPropertiesFile;
    }

    public void setEventDescriptionPropertiesFile(File eventDescriptionPropertiesFile)
    {
        this.eventDescriptionPropertiesFile = eventDescriptionPropertiesFile;
    }
}
