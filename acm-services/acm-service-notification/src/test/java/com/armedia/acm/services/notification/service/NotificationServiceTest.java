package com.armedia.acm.services.notification.service;

/*-
 * #%L
 * ACM Service: Notification
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

import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.contains;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.email.model.EmailSenderConfig;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.service.AcmEmailSenderService;
import com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService;
import com.armedia.acm.services.email.service.TemplatingEngine;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.ApplicationNotificationEvent;
import com.armedia.acm.services.notification.model.BasicNotificationRule;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConfig;
import com.armedia.acm.services.notification.model.NotificationRule;
import com.armedia.acm.services.notification.model.QueryType;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.spring.SpringContextHolder;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationServiceTest extends EasyMockSupport
{

    private NotificationServiceImpl notificationService;
    private NotificationDao mockNotificationDao;
    private MuleContextManager mockMuleContextManager;
    private NotificationEventPublisher mockNotificationEventPublisher;
    private AuditPropertyEntityAdapter mockAuditPropertyEntityAdapter;
    private MuleMessage mockMuleMessage;
    private SpringContextHolder mockSpringContextHolder;
    private SendExecutor sendExecutor;
    private PurgeExecutor purgeExecutor;
    private NotificationFormatter mockNotificationFormatter;
    private NotificationConfig notificationConfig;
    private EmailSenderConfig emailSenderConfig;
    private TemplateModelProvider<Notification> mockTemplateModelProvider;
    private AcmMailTemplateConfigurationService mockTemplateService;
    private TemplatingEngine mockTemplatingEngine;
    private UserDao mockUserDao;
    private AcmEmailSenderService mockEmailSenderService;

    @Before
    public void setUp() throws Exception
    {
        notificationService = new NotificationServiceImpl();

        mockNotificationDao = createMock(NotificationDao.class);
        mockMuleContextManager = createMock(MuleContextManager.class);
        mockNotificationEventPublisher = createMock(NotificationEventPublisher.class);
        mockAuditPropertyEntityAdapter = createMock(AuditPropertyEntityAdapter.class);
        mockMuleMessage = createMock(MuleMessage.class);
        mockSpringContextHolder = createMock(SpringContextHolder.class);
        mockTemplateModelProvider = createMock(TemplateModelProvider.class);
        mockTemplateService = createMock(AcmMailTemplateConfigurationService.class);
        mockTemplatingEngine = createMock(TemplatingEngine.class);
        mockUserDao = createMock(UserDao.class);
        mockEmailSenderService = createMock(AcmEmailSenderService.class);

        sendExecutor = new SendExecutor();
        sendExecutor.setSpringContextHolder(mockSpringContextHolder);
        sendExecutor.setTemplateModelProvider(mockTemplateModelProvider);

        purgeExecutor = new PurgeExecutor();
        purgeExecutor.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);

        mockNotificationFormatter = createMock(NotificationFormatter.class);

        notificationService.setNotificationDao(mockNotificationDao);
        notificationService.setNotificationEventPublisher(mockNotificationEventPublisher);
        notificationService.setSpringContextHolder(mockSpringContextHolder);
        notificationService.setNotificationFormatter(mockNotificationFormatter);
        notificationService.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);

        notificationConfig = new NotificationConfig();
        notificationConfig.setUserBatchRun(true);
        notificationConfig.setUserBatchSize(10);
        notificationConfig.setPurgeDays(30);
        notificationService.setNotificationConfig(notificationConfig);

        emailSenderConfig = new EmailSenderConfig();
        emailSenderConfig.setHost("host");
        emailSenderConfig.setPort(8080);
        emailSenderConfig.setUsername("user");
        emailSenderConfig.setPassword("password");
        emailSenderConfig.setUserFrom("from");
        emailSenderConfig.setType("smtp");
        emailSenderConfig.setEncryption("off");
    }

    @Test
    public void testRunEmailSent() throws Exception
    {
        List<Notification> notifications = new ArrayList<>();

        Notification notification1 = new Notification();
        notification1.setTitle("title");
        notification1.setNote("note");
        notification1.setType("type");
        notification1.setParentId(2L);
        notification1.setParentType("parent type");
        notification1.setParentName("parent name");
        notification1.setParentTitle("parent title");
        notification1.setUserEmail("user email");
        notification1.setStatus("status");
        notification1.setAction("action");
        notification1.setData("data");
        notification1.setState("state");
        notification1.setTemplateModelName("test");
        notification1.setEmailAddresses("user email");

        Notification notification2 = new Notification();
        notification2.setTitle("title");
        notification2.setNote("note");
        notification2.setType("type");
        notification2.setParentId(2L);
        notification2.setParentType("parent type");
        notification2.setParentName("parent name");
        notification2.setParentTitle("parent title");
        notification2.setUserEmail("user email");
        notification2.setStatus("status");
        notification2.setAction("action");
        notification2.setData("data");
        notification2.setState("state");
        notification2.setTemplateModelName("test");
        notification2.setEmailAddresses("user email");

        notifications.add(notification1);
        notifications.add(notification2);

        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("host", "host");
        messageProps.put("port", "port");
        messageProps.put("user", "user");
        messageProps.put("password", "password");
        messageProps.put("from", "from");
        messageProps.put("to", "user email");
        messageProps.put("subject", "title");

        BasicNotificationRule assignRule = new BasicNotificationRule();
        assignRule.setGlobalRule(true);
        assignRule.setJpaQuery("query");
        assignRule.setQueryType(QueryType.CREATE);
        assignRule.setExecutor(sendExecutor);

        BasicNotificationRule unassignRule = new BasicNotificationRule();
        unassignRule.setGlobalRule(true);
        unassignRule.setJpaQuery("query");
        unassignRule.setQueryType(QueryType.CREATE);
        unassignRule.setExecutor(sendExecutor);

        Map<String, NotificationRule> rules = new HashMap<>();
        rules.put("assignRule", assignRule);
        rules.put("unassignRule", unassignRule);

        NotificationSenderFactory notificationSenderFactory = new NotificationSenderFactory();
        Map<String, NotificationSender> notificationSenderMap = new HashMap<>();

        SmtpNotificationSender smtpNotificationServer = new SmtpNotificationSender();
        smtpNotificationServer.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);

        notificationSenderMap.put("smtp", smtpNotificationServer);
        notificationSenderFactory.setNotificationSenderMap(notificationSenderMap);
        notificationSenderFactory.setEmailSenderConfig(emailSenderConfig);

        NotificationUtils mockNotificationUtils = createMock(NotificationUtils.class);
        smtpNotificationServer.setNotificationUtils(mockNotificationUtils);

        Map<String, NotificationSenderFactory> senders = new HashMap<>();
        senders.put("notificationSender", notificationSenderFactory);

        // I am using the same captures below multiple times because we don't need to check these captures
        Capture<Map<String, Object>> propertiesCapture = new Capture<>();
        Capture<NotificationRule> ruleCapture = Capture.newInstance();

        expect(mockSpringContextHolder.getAllBeansOfType(NotificationRule.class)).andReturn(rules).anyTimes();
        expect(mockSpringContextHolder.getAllBeansOfType(NotificationSenderFactory.class)).andReturn(senders).anyTimes();
        expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(0), eq(10), capture(ruleCapture))).andReturn(notifications)
                .anyTimes();
        mockAuditPropertyEntityAdapter.setUserId(eq("NOTIFICATION-BATCH-INSERT"));
        expectLastCall().anyTimes();

        Capture<Map<String, Object>> messagePropsCapture = null;
        try
        {
            messagePropsCapture = EasyMock.newCapture();
            expect(mockMuleContextManager.send(eq("vm://sendEmailViaSmtp.in"), contains("note"), capture(messagePropsCapture)))
                    .andReturn(mockMuleMessage).anyTimes();
        }
        catch (MuleException e)
        {

        }

        // Return null - SUCCESSFULLY SENT
        expect(mockMuleMessage.getInboundProperty(eq("sendEmailException"))).andReturn(null).anyTimes();

        Capture<Notification> capturedNotification = new Capture<>();
        expect(mockNotificationDao.save(capture(capturedNotification))).andReturn(notification1).anyTimes();

        Capture<ApplicationNotificationEvent> capturedEvent = new Capture<>();
        mockNotificationEventPublisher.publishNotificationEvent(capture(capturedEvent));
        expectLastCall().anyTimes();
        expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(10), eq(10), capture(ruleCapture)))
                .andReturn(new ArrayList<>()).anyTimes();
        String template = "Template";

        smtpNotificationServer.setTemplateService(mockTemplateService);
        smtpNotificationServer.setTemplatingEngine(mockTemplatingEngine);
        smtpNotificationServer.setUserDao(mockUserDao);
        smtpNotificationServer.setEmailSenderService(mockEmailSenderService);

        expect(mockTemplateService.getTemplate("test.html")).andReturn(template).times(4);

        for (Notification notification : notifications)
        {
            Capture<EmailWithAttachmentsDTO> emailWithAttachmentsDTOCapture = Capture.newInstance();

            expect(smtpNotificationServer.getTemplatingEngine().process(template, notification.getTemplateModelName(), notification))
                    .andReturn("Body").times(2);
            expect(mockTemplateModelProvider.getModel(notification)).andReturn(notification).times(2);

            smtpNotificationServer.getEmailSenderService().sendEmail(capture(emailWithAttachmentsDTOCapture), eq(null), eq(null));
            expectLastCall().anyTimes();
        }
        expect(mockNotificationFormatter.replaceFormatPlaceholders(notification1)).andReturn(notification1).atLeastOnce();
        expect(mockNotificationFormatter.replaceFormatPlaceholders(notification2)).andReturn(notification2).atLeastOnce();
        expect(mockNotificationUtils.buildNotificationLink(anyString(), anyLong(), anyString(), anyLong())).andReturn(null).anyTimes();

        replayAll();

        notificationService.run(new Date(0));

        verifyAll();

        // Boolean starttls = (Boolean) messagePropsCapture.getValue().get(NotificationConstants.SMTP_STARTTLS);
        // assertFalse(starttls);
    }

    @Test
    public void testRunEmailNotSent() throws AcmEncryptionException, Exception
    {
        List<Notification> notifications = new ArrayList<>();

        Notification notification1 = new Notification();
        notification1.setTitle("title");
        notification1.setNote("note");
        notification1.setType("type");
        notification1.setParentId(2L);
        notification1.setParentType("parent type");
        notification1.setParentName("parent name");
        notification1.setParentTitle("parent title");
        notification1.setUserEmail("user email");
        notification1.setStatus("status");
        notification1.setAction("action");
        notification1.setData("data");
        notification1.setState("state");
        notification1.setTemplateModelName("test");
        notification1.setEmailAddresses("user email");

        Notification notification2 = new Notification();
        notification2.setTitle("title");
        notification2.setNote("note");
        notification2.setType("type");
        notification2.setParentId(2L);
        notification2.setParentType("parent type");
        notification2.setParentName("parent name");
        notification2.setParentTitle("parent title");
        notification2.setUserEmail("user email");
        notification2.setStatus("status");
        notification2.setAction("action");
        notification2.setData("data");
        notification2.setState("state");
        notification2.setTemplateModelName("test");
        notification2.setEmailAddresses("user email");

        notifications.add(notification1);
        notifications.add(notification2);

        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("host", "host");
        messageProps.put("port", "port");
        messageProps.put("user", "user");
        messageProps.put("password", "password");
        messageProps.put("from", "from");
        messageProps.put("to", "user email");
        messageProps.put("subject", "title");

        BasicNotificationRule assignRule = new BasicNotificationRule();
        assignRule.setGlobalRule(true);
        assignRule.setJpaQuery("query");
        assignRule.setQueryType(QueryType.CREATE);
        assignRule.setExecutor(sendExecutor);

        BasicNotificationRule unassignRule = new BasicNotificationRule();
        unassignRule.setGlobalRule(true);
        unassignRule.setJpaQuery("query");
        unassignRule.setQueryType(QueryType.CREATE);
        unassignRule.setExecutor(sendExecutor);

        Map<String, NotificationRule> rules = new HashMap<>();
        rules.put("assignRule", assignRule);
        rules.put("unassignRule", unassignRule);

        NotificationUtils mockNotificationUtils = createMock(NotificationUtils.class);

        NotificationSenderFactory notificationSenderFactory = new NotificationSenderFactory();
        Map<String, NotificationSender> notificationSenderMap = new HashMap<>();

        SmtpNotificationSender smtpNotificationServer = new SmtpNotificationSender();
        smtpNotificationServer.setNotificationUtils(mockNotificationUtils);
        smtpNotificationServer.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);
        // smtpNotificationServer.setPropertyFileManager(mockPropertyFileManager);

        notificationSenderMap.put("smtp", smtpNotificationServer);
        notificationSenderFactory.setNotificationSenderMap(notificationSenderMap);
        notificationSenderFactory.setEmailSenderConfig(emailSenderConfig);

        Map<String, NotificationSenderFactory> senders = new HashMap<>();
        senders.put("notificationSender", notificationSenderFactory);

        // I am using the same captures below multiple times because we don't need to check these captures
        Capture<Map<String, Object>> propertiesCapture = new Capture<>();
        Capture<NotificationRule> ruleCapture = Capture.newInstance();

        expect(mockSpringContextHolder.getAllBeansOfType(NotificationRule.class)).andReturn(rules).anyTimes();
        expect(mockSpringContextHolder.getAllBeansOfType(NotificationSenderFactory.class)).andReturn(senders).anyTimes();
        expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(0), eq(10), capture(ruleCapture))).andReturn(notifications)
                .anyTimes();

        String template = "Template";

        smtpNotificationServer.setTemplateService(mockTemplateService);
        smtpNotificationServer.setTemplatingEngine(mockTemplatingEngine);
        smtpNotificationServer.setUserDao(mockUserDao);
        smtpNotificationServer.setEmailSenderService(mockEmailSenderService);

        expect(mockTemplateService.getTemplate("test.html")).andReturn(template).times(4);

        for (Notification notification : notifications)
        {
            Capture<EmailWithAttachmentsDTO> emailWithAttachmentsDTOCapture = Capture.newInstance();

            expect(smtpNotificationServer.getTemplatingEngine().process(template, notification.getTemplateModelName(), notification))
                    .andReturn("Body").times(2);
            expect(mockTemplateModelProvider.getModel(notification)).andReturn(notification).times(2);

            smtpNotificationServer.getEmailSenderService().sendEmail(capture(emailWithAttachmentsDTOCapture), eq(null), eq(null));
            expectLastCall().anyTimes();
        }

        mockAuditPropertyEntityAdapter.setUserId(eq("NOTIFICATION-BATCH-INSERT"));
        expectLastCall().anyTimes();

        expect(mockNotificationFormatter.replaceFormatPlaceholders(notification1)).andReturn(notification1).atLeastOnce();
        expect(mockNotificationFormatter.replaceFormatPlaceholders(notification2)).andReturn(notification2).atLeastOnce();
        expect(mockNotificationUtils.buildNotificationLink(anyString(), anyLong(), anyString(), anyLong())).andReturn(null).anyTimes();

        try
        {
            Capture<Map<String, Object>> messagePropsCapture = EasyMock.newCapture();
            expect(mockMuleContextManager.send(eq("vm://sendEmailViaSmtp.in"), contains("note"), capture(messagePropsCapture)))
                    .andReturn(mockMuleMessage).anyTimes();
        }
        catch (MuleException e)
        {

        }

        // Return Exception - UNSUCCESSFULLY SENT
        expect(mockMuleMessage.getInboundProperty(eq("sendEmailException"))).andReturn(new Exception("exception")).anyTimes();

        Capture<Notification> capturedNotification = new Capture<>();
        expect(mockNotificationDao.save(capture(capturedNotification))).andReturn(notification1).anyTimes();

        Capture<ApplicationNotificationEvent> capturedEvent = new Capture<>();
        mockNotificationEventPublisher.publishNotificationEvent(capture(capturedEvent));
        expectLastCall().anyTimes();
        expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(10), eq(10), capture(ruleCapture)))
                .andReturn(new ArrayList<>()).anyTimes();

        replayAll();

        notificationService.run(new Date(0));

        verifyAll();

        // Boolean starttls = (Boolean) messagePropsCapture.getValue().get(NotificationConstants.SMTP_STARTTLS);
        // assertTrue(starttls);
    }

    @Test
    public void testRunPurge()
    {
        List<Notification> notifications = new ArrayList<>();

        Notification notification1 = new Notification();
        notification1.setUser("user");
        notification1.setTitle("title");
        notification1.setNote("note");
        notification1.setType("type");
        notification1.setParentId(2L);
        notification1.setParentType("parent type");
        notification1.setParentName("parent name");
        notification1.setParentTitle("parent title");
        notification1.setUserEmail("user email");
        notification1.setStatus("status");
        notification1.setAction("action");
        notification1.setData("data");
        notification1.setState("state");

        Notification notification2 = new Notification();
        notification2.setUser("user");
        notification2.setTitle("title");
        notification2.setNote("note");
        notification2.setType("type");
        notification2.setParentId(2L);
        notification2.setParentType("parent type");
        notification2.setParentName("parent name");
        notification2.setParentTitle("parent title");
        notification2.setUserEmail("user email");
        notification2.setStatus("status");
        notification2.setAction("action");
        notification2.setData("data");
        notification2.setState("state");

        // Return only notification 1 - imagine that notification 2 should not be deleted
        notifications.add(notification1);

        BasicNotificationRule singleQueryRule = new BasicNotificationRule();
        singleQueryRule.setGlobalRule(true);
        singleQueryRule.setJpaQuery("query");
        singleQueryRule.setQueryType(QueryType.SELECT);
        singleQueryRule.setExecutor(purgeExecutor);

        Map<String, NotificationRule> rules = new HashMap<>();
        rules.put("purgeRule", singleQueryRule);

        // I am using the same captures below multiple times because we don't need to check these captures
        Capture<Map<String, Object>> propertiesCapture = new Capture<>();

        expect(mockSpringContextHolder.getAllBeansOfType(NotificationRule.class)).andReturn(rules).anyTimes();
        expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(0), eq(10), eq(singleQueryRule))).andReturn(notifications)
                .anyTimes();
        mockAuditPropertyEntityAdapter.setUserId(eq("NOTIFICATION-BATCH-INSERT"));
        expectLastCall().anyTimes();

        Capture<Notification> capturedNotification = new Capture<>();
        expect(mockNotificationDao.save(capture(capturedNotification))).andReturn(notification1).anyTimes();

        Capture<ApplicationNotificationEvent> capturedEvent = new Capture<>();
        mockNotificationEventPublisher.publishNotificationEvent(capture(capturedEvent));
        expectLastCall().anyTimes();
        expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(10), eq(10), eq(singleQueryRule)))
                .andReturn(new ArrayList<>()).anyTimes();

        expect(mockNotificationFormatter.replaceFormatPlaceholders(notification1)).andReturn(notification1).atLeastOnce();

        replayAll();

        notificationService.run(new Date(0));

        verifyAll();

        assertEquals("DELETE", capturedNotification.getValue().getStatus());
    }

}
