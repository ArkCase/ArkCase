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
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.email.model.EmailSenderConfig;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.service.AcmEmailSenderService;
import com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.ApplicationNotificationEvent;
import com.armedia.acm.services.notification.model.BasicNotificationRule;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConfig;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.model.NotificationRule;
import com.armedia.acm.services.templateconfiguration.model.Template;
import com.armedia.acm.services.templateconfiguration.service.TemplateConfigurationManager;
import com.armedia.acm.services.templateconfiguration.service.TemplatingEngine;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.io.FileUtils;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationServiceTest extends EasyMockSupport
{
    private NotificationServiceImpl notificationService;
    private NotificationDao mockNotificationDao;
    private NotificationEventPublisher mockNotificationEventPublisher;
    private AuditPropertyEntityAdapter mockAuditPropertyEntityAdapter;
    private SpringContextHolder mockSpringContextHolder;
    private SendExecutor sendExecutor;
    private EmailSenderConfig emailSenderConfig;
    private TemplateModelProvider<Notification> mockTemplateModelProvider;
    private AcmMailTemplateConfigurationService mockTemplateService;
    private TemplatingEngine mockTemplatingEngine;
    private UserDao mockUserDao;
    private AcmEmailSenderService mockEmailSenderService;
    private Resource templateConfiguration;
    private List<Template> templateConfigurations = new ArrayList<>();
    private TemplateConfigurationManager templateConfigurationManager;

    @Before
    public void setUp() throws Exception
    {
        notificationService = new NotificationServiceImpl();

        mockNotificationDao = createMock(NotificationDao.class);
        mockNotificationEventPublisher = createMock(NotificationEventPublisher.class);
        mockAuditPropertyEntityAdapter = createMock(AuditPropertyEntityAdapter.class);
        mockSpringContextHolder = createMock(SpringContextHolder.class);
        mockTemplateModelProvider = createMock(TemplateModelProvider.class);
        mockTemplateService = createMock(AcmMailTemplateConfigurationService.class);
        mockTemplatingEngine = createMock(TemplatingEngine.class);
        mockUserDao = createMock(UserDao.class);
        mockEmailSenderService = createMock(AcmEmailSenderService.class);
        templateConfiguration = new ClassPathResource("templates-configuration.json");
        templateConfigurationManager = new TemplateConfigurationManager();
        templateConfigurationManager.setTemplatesConfiguration(templateConfiguration);
        templateConfigurationManager.setObjectConverter(ObjectConverter.createObjectConverterForTests());
        templateConfigurations = templateConfigurationManager.getObjectConverter().getJsonUnmarshaller()
                .unmarshallCollection(FileUtils.readFileToString(templateConfiguration.getFile()), List.class, Template.class);
        templateConfigurationManager.setTemplateConfigurations(templateConfigurations);

        sendExecutor = new SendExecutor();
        sendExecutor.setSpringContextHolder(mockSpringContextHolder);
        sendExecutor.setTemplateModelProvider(mockTemplateModelProvider);


        notificationService.setNotificationDao(mockNotificationDao);
        notificationService.setNotificationEventPublisher(mockNotificationEventPublisher);
        notificationService.setSpringContextHolder(mockSpringContextHolder);
        notificationService.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);

        NotificationConfig notificationConfig = new NotificationConfig();
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
        notification1.setTitle("title1");
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
        notification1.setTemplateModelName("modelName");
        notification1.setEmailAddresses("user email");
        notification1.setCcEmailAddresses("cc user email");
        notification1.setBccEmailAddresses("bcc user email");

        Notification notification2 = new Notification();
        notification2.setTitle("title2");
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
        notification2.setTemplateModelName("modelName");
        notification2.setEmailAddresses("user email");
        notification2.setCcEmailAddresses("cc user email");
        notification2.setBccEmailAddresses("bcc user email");

        notifications.add(notification1);
        notifications.add(notification2);

        BasicNotificationRule assignRule = new BasicNotificationRule();
        assignRule.setGlobalRule(true);
        assignRule.setJpaQuery("query");
        assignRule.setExecutor(sendExecutor);

        BasicNotificationRule unassignRule = new BasicNotificationRule();
        unassignRule.setGlobalRule(true);
        unassignRule.setJpaQuery("query");
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
        Capture<Map<String, Object>> propertiesCapture = EasyMock.newCapture();
        Capture<NotificationRule> ruleCapture = Capture.newInstance();

        expect(mockSpringContextHolder.getAllBeansOfType(NotificationRule.class)).andReturn(rules).anyTimes();
        expect(mockSpringContextHolder.getAllBeansOfType(NotificationSenderFactory.class)).andReturn(senders).anyTimes();
        expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(0), eq(10), capture(ruleCapture))).andReturn(notifications)
                .anyTimes();
        mockAuditPropertyEntityAdapter.setUserId(eq("NOTIFICATION-BATCH-INSERT"));
        expectLastCall().anyTimes();

        Capture<Notification> capturedNotification = EasyMock.newCapture();
        expect(mockNotificationDao.save(capture(capturedNotification))).andReturn(notification1).anyTimes();

        Capture<ApplicationNotificationEvent> capturedEvent = EasyMock.newCapture();
        mockNotificationEventPublisher.publishNotificationEvent(capture(capturedEvent));
        expectLastCall().anyTimes();
        expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(10), eq(10), capture(ruleCapture)))
                .andReturn(new ArrayList<>()).anyTimes();
        String template = "Template";
        Capture<String> subjectCapture = EasyMock.newCapture();

        smtpNotificationServer.setTemplateService(mockTemplateService);
        smtpNotificationServer.setTemplatingEngine(mockTemplatingEngine);
        smtpNotificationServer.setUserDao(mockUserDao);
        smtpNotificationServer.setEmailSenderService(mockEmailSenderService);
        smtpNotificationServer.setTemplateConfigurationManager(templateConfigurationManager);

        expect(mockTemplateService.getTemplate("modelName.html")).andReturn(template).times(2);

        for (Notification notification : notifications)
        {
            Capture<EmailWithAttachmentsDTO> emailWithAttachmentsDTOCapture = Capture.newInstance();

            expect(smtpNotificationServer.getTemplatingEngine().process(template, notification.getTemplateModelName(), notification))
                    .andReturn("Body").times(1);
            expect(smtpNotificationServer.getTemplatingEngine().process(capture(subjectCapture), eq(notification.getTemplateModelName()), eq(notification)))
                    .andReturn("subject").times(1);
            expect(mockTemplateModelProvider.getModel(notification)).andReturn(notification).times(1);

            smtpNotificationServer.getEmailSenderService().sendEmail(capture(emailWithAttachmentsDTOCapture), eq(null), eq(null));
            expectLastCall().andStubAnswer(() -> {
                emailWithAttachmentsDTOCapture.getValue().setMailSent(true);
                return null;
            });
        }

        expect(mockNotificationUtils.buildNotificationLink(anyString(), anyLong(), anyString(), anyLong())).andReturn(null).anyTimes();

        replayAll();

        notificationService.run(new Date(0));

        verifyAll();

        assertEquals(NotificationConstants.STATE_SENT, notification1.getState());
        assertEquals(NotificationConstants.STATE_SENT, notification2.getState());
    }

    @Test
    public void testRunEmailNotSent() throws Exception
    {
        List<Notification> notifications = new ArrayList<>();

        Notification notification1 = new Notification();
        notification1.setTitle("title1");
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
        notification2.setTitle("title2");
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

        BasicNotificationRule assignRule = new BasicNotificationRule();
        assignRule.setGlobalRule(true);
        assignRule.setJpaQuery("query");
        assignRule.setExecutor(sendExecutor);

        BasicNotificationRule unassignRule = new BasicNotificationRule();
        unassignRule.setGlobalRule(true);
        unassignRule.setJpaQuery("query");
        unassignRule.setExecutor(sendExecutor);

        Map<String, NotificationRule> rules = new HashMap<>();
        rules.put("assignRule", assignRule);
        rules.put("unassignRule", unassignRule);

        NotificationSenderFactory notificationSenderFactory = new NotificationSenderFactory();
        Map<String, NotificationSender> notificationSenderMap = new HashMap<>();

        SmtpNotificationSender smtpNotificationServer = new SmtpNotificationSender();

        NotificationUtils mockNotificationUtils = createMock(NotificationUtils.class);
        smtpNotificationServer.setNotificationUtils(mockNotificationUtils);
        smtpNotificationServer.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);

        notificationSenderMap.put("smtp", smtpNotificationServer);
        notificationSenderFactory.setNotificationSenderMap(notificationSenderMap);
        notificationSenderFactory.setEmailSenderConfig(emailSenderConfig);

        Map<String, NotificationSenderFactory> senders = new HashMap<>();
        senders.put("notificationSender", notificationSenderFactory);

        // I am using the same captures below multiple times because we don't need to check these captures
        Capture<Map<String, Object>> propertiesCapture = EasyMock.newCapture();
        Capture<NotificationRule> ruleCapture = Capture.newInstance();

        expect(mockSpringContextHolder.getAllBeansOfType(NotificationRule.class)).andReturn(rules).anyTimes();
        expect(mockSpringContextHolder.getAllBeansOfType(NotificationSenderFactory.class)).andReturn(senders).anyTimes();
        expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(0), eq(10), capture(ruleCapture))).andReturn(notifications)
                .anyTimes();

        expect(mockNotificationUtils.buildNotificationLink(anyString(), anyLong(), anyString(), anyLong())).andReturn(null).anyTimes();

        Capture<Notification> capturedNotification = EasyMock.newCapture();
        expect(mockNotificationDao.save(capture(capturedNotification))).andReturn(notification1).anyTimes();

        Capture<ApplicationNotificationEvent> capturedEvent = EasyMock.newCapture();
        mockNotificationEventPublisher.publishNotificationEvent(capture(capturedEvent));
        expectLastCall().anyTimes();

        expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(10), eq(10), capture(ruleCapture)))
                .andReturn(new ArrayList<>()).anyTimes();
        String template = "Template";
        Capture<String> subjectCapture = EasyMock.newCapture();

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
            expect(smtpNotificationServer.getTemplatingEngine().process(capture(subjectCapture), eq(notification.getTemplateModelName()), eq(notification)))
                    .andReturn("subject").times(2);
            expect(mockTemplateModelProvider.getModel(notification)).andReturn(notification).times(2);

            smtpNotificationServer.getEmailSenderService().sendEmail(capture(emailWithAttachmentsDTOCapture), eq(null), eq(null));
            expectLastCall().andStubAnswer(() -> {
                emailWithAttachmentsDTOCapture.getValue().setMailSent(false);
                return null;
            });
        }
        mockAuditPropertyEntityAdapter.setUserId(eq("NOTIFICATION-BATCH-INSERT"));
        expectLastCall().anyTimes();

        replayAll();

        notificationService.run(new Date(0));

        verifyAll();
    }
}
