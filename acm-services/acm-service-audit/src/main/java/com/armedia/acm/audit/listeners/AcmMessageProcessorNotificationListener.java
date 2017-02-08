package com.armedia.acm.audit.listeners;

import com.armedia.acm.audit.model.AuditConstants;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.audit.service.AuditService;
import com.armedia.acm.web.api.MDCConstants;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.context.notification.MessageProcessorNotificationListener;
import org.mule.api.processor.LoggerMessageProcessor;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.transport.PropertyScope;
import org.mule.component.DefaultJavaComponent;
import org.mule.component.simple.EchoComponent;
import org.mule.context.notification.MessageProcessorNotification;
import org.mule.endpoint.DefaultOutboundEndpoint;
import org.mule.interceptor.TimerInterceptor;
import org.mule.module.cmis.processors.AbstractConnectedProcessor;
import org.mule.module.scripting.transformer.ScriptTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Mule message notification listener sets the {@link MDC} variables to messages in session properties and sets the {@link MDC} variables
 * from them.
 * <p>
 * Created by Bojan Milenkoski on 20.1.2016.
 */
public class AcmMessageProcessorNotificationListener implements MessageProcessorNotificationListener<MessageProcessorNotification>
{
    public static final String EVENT_TYPE = "com.armedia.acm.audit.mule.flow";

    private Logger log = LoggerFactory.getLogger(getClass());

    private AuditService auditService;
    private boolean muleFlowsLoggingEnabled;
    private boolean muleFlowsLoggingMessageEnabled;
    private boolean muleFlowsLoggingMessagePropertiesEnabled;
    private List<String> contentTypesToLog;

    @Override
    public void onNotification(MessageProcessorNotification notification)
    {
        log.trace("Mule message processor notification listener called");

        MuleEvent event = notification.getSource();

        if (notification.getAction() == MessageProcessorNotification.MESSAGE_PROCESSOR_PRE_INVOKE)
        {
            if ((event.getSessionVariable(MDCConstants.EVENT_MDC_REQUEST_ID_KEY) == null)
                    && (event.getMessage().getInboundProperty(MDCConstants.EVENT_MDC_REQUEST_ID_KEY) != null))
            {
                // set session variables if missing
                event.setSessionVariable(MDCConstants.EVENT_MDC_REQUEST_ID_KEY,
                        event.getMessage().getInboundProperty(MDCConstants.EVENT_MDC_REQUEST_ID_KEY));
                event.setSessionVariable(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY,
                        event.getMessage().getInboundProperty(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY));
                event.setSessionVariable(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY,
                        event.getMessage().getInboundProperty(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY));
                event.setSessionVariable(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY,
                        event.getMessage().getInboundProperty(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY));
            }

            // set MDC variables from message session properties
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, event.getSessionVariable(MDCConstants.EVENT_MDC_REQUEST_ID_KEY));
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY,
                    event.getSessionVariable(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY));
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, event.getSessionVariable(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY));
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY,
                    event.getSessionVariable(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY));
        }

        if (notification.getAction() == MessageProcessorNotification.MESSAGE_PROCESSOR_POST_INVOKE)
        {
            if (isAuditable(notification))
            {
                audit(notification);
            }
        }
    }

    private boolean isAuditable(MessageProcessorNotification notification)
    {
        if (notification.getProcessor() instanceof DefaultJavaComponent)
        {
            Class<?> type = ((DefaultJavaComponent) notification.getProcessor()).getObjectType();
            return !type.equals(EchoComponent.class);
        }

        if (notification.getProcessor() instanceof TimerInterceptor || notification.getProcessor() instanceof LoggerMessageProcessor)
        {
            return false;
        }

        return true;
    }

    private void audit(MessageProcessorNotification notification)
    {
        log.trace("Mule event auditing handling");

        MuleEvent event = notification.getSource();

        if (isMuleFlowsLoggingEnabled())
        {
            AuditEvent auditEvent = new AuditEvent();

            auditEvent.setIpAddress(MDC.get(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY));
            // when entity is changed without web request the MDC.get(AuditConstants.EVENT_MDC_REQUEST_ID_KEY) is null
            auditEvent.setRequestId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_ID_KEY) == null ? null
                    : UUID.fromString(MDC.get(MDCConstants.EVENT_MDC_REQUEST_ID_KEY)));
            auditEvent.setUserId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY) != null
                    ? MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY) : AuditConstants.USER_ID_ANONYMOUS);
            auditEvent.setFullEventType(EVENT_TYPE + " | " + event.getMessageSourceURI());
            auditEvent.setEventResult(AuditConstants.EVENT_RESULT_SUCCESS);
            auditEvent.setObjectType(AuditConstants.EVENT_OBJECT_TYPE_MULE_FLOW);
            auditEvent.setStatus(AuditConstants.EVENT_STATUS_COMPLETE);
            auditEvent.setEventDate(new Date(notification.getTimestamp()));

            // event properties
            Map<String, String> eventProperties = new HashMap<>();

            eventProperties.put("Flow name", event.getFlowConstruct().getName());
            eventProperties.put("Server ID", notification.getServerId());
            eventProperties.put("Message source URI", event.getMessageSourceURI().toString());
            eventProperties.put("Exchange pattern", event.getExchangePattern().name());
            eventProperties.put("Processor path", notification.getProcessorPath());

            MessageProcessor processor = notification.getProcessor();

            eventProperties.put("Processor type", processor.getClass().getName());

            if (processor instanceof DefaultOutboundEndpoint)
            {
                DefaultOutboundEndpoint outboundEndpoint = (DefaultOutboundEndpoint) processor;
                eventProperties.put("Outbound endpoint name", outboundEndpoint.getName());
                eventProperties.put("Outbound connector name", outboundEndpoint.getConnector().getName());
                eventProperties.put("Outbound connector protocol", outboundEndpoint.getConnector().getProtocol());
                eventProperties.put("Outbound endpoint Address", outboundEndpoint.getAddress());
            }

            if (processor instanceof DefaultJavaComponent)
            {
                DefaultJavaComponent defaultJavaComponent = (DefaultJavaComponent) processor;
                eventProperties.put("Component type", defaultJavaComponent.getObjectType().getName());
            }

            if (processor instanceof ScriptTransformer)
            {
                ScriptTransformer scriptTransformer = (ScriptTransformer) processor;
                eventProperties.put("Processor name", scriptTransformer.getName());
            }

            // Alfresco processor
            if (processor instanceof AbstractConnectedProcessor)
            {
                AbstractConnectedProcessor abstractConnectedProcessor = (AbstractConnectedProcessor) processor;

                String repositoryId = abstractConnectedProcessor.getRepositoryId() != null
                        ? abstractConnectedProcessor.getRepositoryId().toString() : "null";
                eventProperties.put("CMIS Repository Id", repositoryId);
                String baseUrl = abstractConnectedProcessor.getBaseUrl() != null ? abstractConnectedProcessor.getBaseUrl().toString()
                        : "null";
                eventProperties.put("Base URL", baseUrl);
            }

            if (isMuleFlowsLoggingMessageEnabled() && ((event.getMessage().getProperty("contentType", PropertyScope.INVOCATION) == null)
                    || getContentTypesToLog().contains(event.getMessage().getProperty("contentType", PropertyScope.INVOCATION))))
            {
                try
                {
                    eventProperties.put("Message", event.getMessageAsString("UTF-8"));
                }
                catch (MuleException e)
                {
                    // continue execution on exception
                    log.error("Error getting Mule message as String", e);
                }
            }

            if (isMuleFlowsLoggingMessagePropertiesEnabled())
            {
                final MuleMessage message = event.getMessage();

                for (String outboundPropertyName : message.getOutboundPropertyNames())
                {
                    eventProperties.put(outboundPropertyName + "(outbound property)",
                            message.getOutboundProperty(outboundPropertyName).toString());
                }

                for (String inboundPropertyName : message.getInboundPropertyNames())
                {
                    eventProperties.put(inboundPropertyName + "(inbound property)",
                            message.getInboundProperty(inboundPropertyName).toString());
                }

                for (String invocationPropertyName : message.getInvocationPropertyNames())
                {
                    eventProperties.put(invocationPropertyName + "(invocation property)",
                            message.getInvocationProperty(invocationPropertyName).toString());
                }
            }

            auditEvent.setEventProperties(eventProperties);

            if (log.isTraceEnabled())
            {
                log.trace("Activiti AuditEvent: " + auditEvent.toString());
            }

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

    public boolean isMuleFlowsLoggingEnabled()
    {
        return muleFlowsLoggingEnabled;
    }

    public void setMuleFlowsLoggingEnabled(boolean muleFlowsLoggingEnabled)
    {
        this.muleFlowsLoggingEnabled = muleFlowsLoggingEnabled;
    }

    public boolean isMuleFlowsLoggingMessageEnabled()
    {
        return muleFlowsLoggingMessageEnabled;
    }

    public void setMuleFlowsLoggingMessageEnabled(boolean muleFlowsLoggingMessageEnabled)
    {
        this.muleFlowsLoggingMessageEnabled = muleFlowsLoggingMessageEnabled;
    }

    public boolean isMuleFlowsLoggingMessagePropertiesEnabled()
    {
        return muleFlowsLoggingMessagePropertiesEnabled;
    }

    public void setMuleFlowsLoggingMessagePropertiesEnabled(boolean muleFlowsLoggingMessagePropertiesEnabled)
    {
        this.muleFlowsLoggingMessagePropertiesEnabled = muleFlowsLoggingMessagePropertiesEnabled;
    }

    public List<String> getContentTypesToLog()
    {
        return contentTypesToLog;
    }

    public void setContentTypesToLog(List<String> contentTypesToLog)
    {
        this.contentTypesToLog = contentTypesToLog;
    }
}
