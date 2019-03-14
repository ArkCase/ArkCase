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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.armedia.acm.audit.model.AuditConfig;
import com.armedia.acm.audit.model.AuditConstants;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.audit.service.AuditService;
import com.armedia.acm.web.api.MDCConstants;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.object.ObjectFactory;
import org.mule.api.processor.LoggerMessageProcessor;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.transport.PropertyScope;
import org.mule.component.DefaultJavaComponent;
import org.mule.component.simple.EchoComponent;
import org.mule.context.notification.MessageProcessorNotification;
import org.mule.interceptor.TimerInterceptor;
import org.mule.object.SingletonObjectFactory;
import org.slf4j.MDC;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Bojan Milenkoski on 29.1.2016.
 */
public class AcmMessageProcessorNotificationListenerTest extends EasyMockSupport
{
    private AcmMessageProcessorNotificationListener listener;
    private AuditService mockAuditService;
    private MessageProcessorNotification mockNotification;
    private MuleEvent mockMuleEvent;
    private AuditConfig auditConfig;

    @Before
    public void setUp() throws Exception
    {
        listener = new AcmMessageProcessorNotificationListener();
        auditConfig = new AuditConfig();
        listener.setAuditConfig(auditConfig);
        mockNotification = createMock(MessageProcessorNotification.class);
        mockAuditService = createMock(AuditService.class);
        listener.setAuditService(mockAuditService);
        mockMuleEvent = createMock(MuleEvent.class);
    }

    @Test
    public void onNotificationDoesNotAuditEventsWhenMuleFlowsLoggingDisabled()
    {
        // given
        auditConfig.setMuleFlowsLoggingEnabled(false);
        auditConfig.setMuleFlowsLoggingMessageEnabled(true);
        auditConfig.setMuleFlowsLoggingMessagePropertiesEnabled(true);
        expect(mockNotification.getSource()).andReturn(mockMuleEvent).anyTimes();
        expect(mockNotification.getAction()).andReturn(MessageProcessorNotification.MESSAGE_PROCESSOR_POST_INVOKE).anyTimes();
        MessageProcessor mockMessageProcessor = createMock(MessageProcessor.class);
        expect(mockNotification.getProcessor()).andReturn(mockMessageProcessor).anyTimes();

        // when
        replayAll();
        listener.onNotification(mockNotification);

        // then
        verifyAll();
    }

    @Test
    public void onNotificationDoesNotAuditMuleMessageWhenMuleFlowsLoggingMessageDisabled() throws URISyntaxException
    {
        // given
        auditConfig.setMuleFlowsLoggingEnabled(true);
        auditConfig.setMuleFlowsLoggingMessageEnabled(false);
        auditConfig.setMuleFlowsLoggingMessagePropertiesEnabled(true);
        expect(mockNotification.getSource()).andReturn(mockMuleEvent).anyTimes();
        expect(mockNotification.getAction()).andReturn(MessageProcessorNotification.MESSAGE_PROCESSOR_POST_INVOKE).anyTimes();
        expect(mockNotification.getTimestamp()).andReturn(new Date().getTime());
        expect(mockNotification.getServerId()).andReturn("some server");
        expect(mockNotification.getProcessorPath()).andReturn("processor path");
        MessageProcessor mockMessageProcessor = createMock(MessageProcessor.class);
        expect(mockNotification.getProcessor()).andReturn(mockMessageProcessor).anyTimes();
        expect(mockMuleEvent.getMessageSourceURI()).andReturn(new URI("http://someUri")).times(2);
        FlowConstruct mockFlowConstruct = createMock(FlowConstruct.class);
        expect(mockMuleEvent.getFlowConstruct()).andReturn(mockFlowConstruct);
        expect(mockFlowConstruct.getName()).andReturn("Flow name");
        expect(mockMuleEvent.getExchangePattern()).andReturn(MessageExchangePattern.ONE_WAY);
        MuleMessage mockMuleMessage = createMock(MuleMessage.class);
        expect(mockMuleEvent.getMessage()).andReturn(mockMuleMessage);
        expect(mockMuleMessage.getOutboundPropertyNames()).andReturn(new HashSet<>());
        expect(mockMuleMessage.getInboundPropertyNames()).andReturn(new HashSet<>());
        expect(mockMuleMessage.getInvocationPropertyNames()).andReturn(new HashSet<>());
        Capture<AuditEvent> capturedAuditEvent = newCapture();
        mockAuditService.audit(capture(capturedAuditEvent));
        expectLastCall();

        // when
        replayAll();
        listener.onNotification(mockNotification);

        // then
        AuditEvent auditEvent = capturedAuditEvent.getValue();
        assertNull(auditEvent.getEventProperties().get("Message"));
        verifyAll();
    }

    @Test
    public void onNotificationDoesNotAuditMuleMessagePropertiesWhenMuleFlowsLoggingMessagePropertiesDisabled()
            throws URISyntaxException, MuleException
    {
        // given
        auditConfig.setMuleFlowsLoggingEnabled(true);
        auditConfig.setMuleFlowsLoggingMessageEnabled(true);
        auditConfig.setMuleFlowsLoggingMessagePropertiesEnabled(false);
        expect(mockNotification.getSource()).andReturn(mockMuleEvent).anyTimes();
        expect(mockNotification.getAction()).andReturn(MessageProcessorNotification.MESSAGE_PROCESSOR_POST_INVOKE).anyTimes();
        expect(mockNotification.getTimestamp()).andReturn(new Date().getTime());
        expect(mockNotification.getServerId()).andReturn("some server");
        expect(mockNotification.getProcessorPath()).andReturn("processor path");
        MessageProcessor mockMessageProcessor = createMock(MessageProcessor.class);
        expect(mockNotification.getProcessor()).andReturn(mockMessageProcessor).anyTimes();
        expect(mockMuleEvent.getMessageSourceURI()).andReturn(new URI("http://someUri")).times(2);
        MuleMessage mockMuleMessage = createMock(MuleMessage.class);
        expect(mockMuleEvent.getMessage()).andReturn(mockMuleMessage);
        expect(mockMuleMessage.getProperty("contentType", PropertyScope.INVOCATION)).andReturn(null);
        expect(mockMuleEvent.getMessageAsString("UTF-8")).andReturn("Message");
        FlowConstruct mockFlowConstruct = createMock(FlowConstruct.class);
        expect(mockMuleEvent.getFlowConstruct()).andReturn(mockFlowConstruct);
        expect(mockFlowConstruct.getName()).andReturn("Flow name");
        expect(mockMuleEvent.getExchangePattern()).andReturn(MessageExchangePattern.ONE_WAY);
        Capture<AuditEvent> capturedAuditEvent = newCapture();
        mockAuditService.audit(capture(capturedAuditEvent));
        expectLastCall();

        // when
        replayAll();
        listener.onNotification(mockNotification);

        // then
        verifyAll();
    }

    @Test
    public void onNotificationAuditsEverything() throws URISyntaxException, MuleException
    {
        // given
        final String serverId = "some server";
        final String processorPath = "processor path";
        final String message = "Message";
        final String uri = "http://someUri";
        final String flowName = "Flow name";
        final MessageExchangePattern messageExchangePattern = MessageExchangePattern.ONE_WAY;
        final long time = new Date().getTime();
        final String outProp1Key = "outProp1Key";
        final String outProp1Value = "outProp1Value";
        final String outProp2Key = "outProp2Key";
        final String outProp2Value = "outProp2Value";
        final String outProp3Key = "outProp3Key";
        final String outProp3Value = "outProp3Value";
        final String inProp1Key = "inProp1Key";
        final String inProp1Value = "inProp1Value";
        final String inProp2Key = "inProp2Key";
        final String inProp2Value = "inProp2Value";
        final String invProp1Key = "invProp1Key";
        final String invProp1Value = "invProp1Value";
        final LinkedHashSet<String> outboundProperties = new LinkedHashSet<String>(
                Arrays.asList(outProp1Key, outProp2Key, outProp3Key));
        final Set<String> inboundProperties = new LinkedHashSet<String>(Arrays.asList(inProp1Key, inProp2Key));
        final Set<String> invocationProperties = new LinkedHashSet<String>(Arrays.asList(invProp1Key));
        final UUID requestID = UUID.randomUUID();
        final String remoteAddress = "remote.address";
        final String userId = "userId";

        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, requestID.toString());
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY, remoteAddress);
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, userId);

        auditConfig.setMuleFlowsLoggingEnabled(true);
        auditConfig.setMuleFlowsLoggingMessageEnabled(true);
        auditConfig.setMuleFlowsLoggingMessagePropertiesEnabled(true);
        expect(mockNotification.getSource()).andReturn(mockMuleEvent).anyTimes();
        expect(mockNotification.getAction()).andReturn(MessageProcessorNotification.MESSAGE_PROCESSOR_POST_INVOKE).anyTimes();
        expect(mockNotification.getTimestamp()).andReturn(time);
        expect(mockNotification.getServerId()).andReturn(serverId);
        expect(mockNotification.getProcessorPath()).andReturn(processorPath);
        MessageProcessor mockMessageProcessor = createMock(MessageProcessor.class);
        expect(mockNotification.getProcessor()).andReturn(mockMessageProcessor).anyTimes();
        expect(mockMuleEvent.getMessageSourceURI()).andReturn(new URI(uri)).times(2);
        expect(mockMuleEvent.getMessageAsString("UTF-8")).andReturn(message);
        FlowConstruct mockFlowConstruct = createMock(FlowConstruct.class);
        expect(mockMuleEvent.getFlowConstruct()).andReturn(mockFlowConstruct);
        expect(mockFlowConstruct.getName()).andReturn(flowName);
        expect(mockMuleEvent.getExchangePattern()).andReturn(messageExchangePattern);
        MuleMessage mockMuleMessage = createMock(MuleMessage.class);
        expect(mockMuleMessage.getProperty("contentType", PropertyScope.INVOCATION)).andReturn(null);
        expect(mockMuleEvent.getMessage()).andReturn(mockMuleMessage).anyTimes();
        expect(mockMuleMessage.getOutboundPropertyNames()).andReturn(outboundProperties);
        expect(mockMuleMessage.getOutboundProperty(outProp1Key)).andReturn(outProp1Value);
        expect(mockMuleMessage.getOutboundProperty(outProp2Key)).andReturn(outProp2Value);
        expect(mockMuleMessage.getOutboundProperty(outProp3Key)).andReturn(outProp3Value);
        expect(mockMuleMessage.getInboundPropertyNames()).andReturn(inboundProperties);
        expect(mockMuleMessage.getInboundProperty(inProp1Key)).andReturn(inProp1Value);
        expect(mockMuleMessage.getInboundProperty(inProp2Key)).andReturn(inProp2Value);
        expect(mockMuleMessage.getInvocationPropertyNames()).andReturn(invocationProperties);
        expect(mockMuleMessage.getInvocationProperty(invProp1Key)).andReturn(invProp1Value);
        Capture<AuditEvent> capturedAuditEvent = newCapture();
        mockAuditService.audit(capture(capturedAuditEvent));
        expectLastCall();

        // when
        replayAll();
        listener.onNotification(mockNotification);

        // then
        AuditEvent auditEvent = capturedAuditEvent.getValue();
        assertEquals(remoteAddress, auditEvent.getIpAddress());
        assertEquals(requestID, auditEvent.getRequestId());
        assertEquals(userId, auditEvent.getUserId());
        assertEquals(AcmMessageProcessorNotificationListener.EVENT_TYPE + " | " + uri, auditEvent.getFullEventType());
        assertEquals(AuditConstants.EVENT_RESULT_SUCCESS, auditEvent.getEventResult());
        assertEquals(AuditConstants.EVENT_OBJECT_TYPE_MULE_FLOW, auditEvent.getObjectType());
        assertEquals(AuditConstants.EVENT_STATUS_COMPLETE, auditEvent.getStatus());
        assertEquals(time, auditEvent.getEventDate().getTime());
        assertEquals(flowName, auditEvent.getEventProperties().get("Flow name"));
        assertEquals(serverId, auditEvent.getEventProperties().get("Server ID"));
        assertEquals(uri, auditEvent.getEventProperties().get("Message source URI"));
        assertEquals(messageExchangePattern.toString(), auditEvent.getEventProperties().get("Exchange pattern"));
        assertEquals(processorPath, auditEvent.getEventProperties().get("Processor path"));
        assertEquals(message, auditEvent.getEventProperties().get("Message"));
        assertEquals(outProp1Value, auditEvent.getEventProperties().get(outProp1Key + "(outbound property)"));
        assertEquals(outProp2Value, auditEvent.getEventProperties().get(outProp2Key + "(outbound property)"));
        assertEquals(outProp3Value, auditEvent.getEventProperties().get(outProp3Key + "(outbound property)"));
        assertEquals(inProp1Value, auditEvent.getEventProperties().get(inProp1Key + "(inbound property)"));
        assertEquals(inProp2Value, auditEvent.getEventProperties().get(inProp2Key + "(inbound property)"));
        assertEquals(invProp1Value, auditEvent.getEventProperties().get(invProp1Key + "(invocation property)"));
        verifyAll();
    }

    @Test
    public void onNotificationDoesNotAuditEventsWhenProcessorOfTypeEchoComponent()
    {
        // given
        auditConfig.setMuleFlowsLoggingEnabled(true);
        auditConfig.setMuleFlowsLoggingMessageEnabled(true);
        auditConfig.setMuleFlowsLoggingMessagePropertiesEnabled(true);
        expect(mockNotification.getSource()).andReturn(mockMuleEvent).anyTimes();
        expect(mockNotification.getAction()).andReturn(MessageProcessorNotification.MESSAGE_PROCESSOR_POST_INVOKE).anyTimes();

        DefaultJavaComponent messageProcessor = new DefaultJavaComponent();
        ObjectFactory mockObjectFactory = new SingletonObjectFactory(EchoComponent.class);
        messageProcessor.setObjectFactory(mockObjectFactory);
        expect(mockNotification.getProcessor()).andReturn(messageProcessor).anyTimes();

        // when
        replayAll();
        listener.onNotification(mockNotification);

        // then
        verifyAll();
    }

    @Test
    public void onNotificationDoesNotAuditEventsWhenProcessorOfTimerInterceptor()
    {
        // given
        auditConfig.setMuleFlowsLoggingEnabled(true);
        auditConfig.setMuleFlowsLoggingMessageEnabled(true);
        auditConfig.setMuleFlowsLoggingMessagePropertiesEnabled(true);
        expect(mockNotification.getSource()).andReturn(mockMuleEvent).anyTimes();
        expect(mockNotification.getAction()).andReturn(MessageProcessorNotification.MESSAGE_PROCESSOR_POST_INVOKE).anyTimes();

        TimerInterceptor messageProcessor = new TimerInterceptor();
        expect(mockNotification.getProcessor()).andReturn(messageProcessor).anyTimes();

        // when
        replayAll();
        listener.onNotification(mockNotification);

        // then
        verifyAll();
    }

    @Test
    public void onNotificationDoesNotAuditEventsWhenProcessorOfLoggerMessageProcessor()
    {
        // given
        auditConfig.setMuleFlowsLoggingEnabled(true);
        auditConfig.setMuleFlowsLoggingMessageEnabled(true);
        auditConfig.setMuleFlowsLoggingMessagePropertiesEnabled(true);
        expect(mockNotification.getSource()).andReturn(mockMuleEvent).anyTimes();
        expect(mockNotification.getAction()).andReturn(MessageProcessorNotification.MESSAGE_PROCESSOR_POST_INVOKE).anyTimes();

        LoggerMessageProcessor messageProcessor = new LoggerMessageProcessor();
        expect(mockNotification.getProcessor()).andReturn(messageProcessor).anyTimes();

        // when
        replayAll();
        listener.onNotification(mockNotification);

        // then
        verifyAll();
    }
}
