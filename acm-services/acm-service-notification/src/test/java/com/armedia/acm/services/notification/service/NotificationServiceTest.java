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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.email.model.EmailSenderConfig;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.service.AcmEmailSenderService;
import com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.ApplicationNotificationEvent;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConfig;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.provider.NotificationTemplateModelProvider;
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
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Ignore
public class NotificationServiceTest extends EasyMockSupport
{
    private NotificationServiceImpl notificationService;
    private NotificationDao mockNotificationDao;
    private NotificationEventPublisher mockNotificationEventPublisher;
    private AuditPropertyEntityAdapter mockAuditPropertyEntityAdapter;
    private SpringContextHolder mockSpringContextHolder;
    private AcmMailTemplateConfigurationService mockTemplateService;
    private NotificationUtils mockNotificationUtils;
    private SmtpNotificationSender smtpNotificationServer;

    @Before
    public void setUp() throws Exception
    {
        notificationService = new NotificationServiceImpl();

        mockNotificationDao = createMock(NotificationDao.class);
        mockNotificationEventPublisher = createMock(NotificationEventPublisher.class);
        mockAuditPropertyEntityAdapter = createMock(AuditPropertyEntityAdapter.class);
        mockSpringContextHolder = createMock(SpringContextHolder.class);
        mockTemplateService = createMock(AcmMailTemplateConfigurationService.class);
        TemplatingEngine mockTemplatingEngine = createMock(TemplatingEngine.class);
        UserDao mockUserDao = createMock(UserDao.class);
        AcmEmailSenderService mockEmailSenderService = createMock(AcmEmailSenderService.class);
        mockNotificationUtils = createMock(NotificationUtils.class);

        Resource templateConfiguration = new ClassPathResource("templates-configuration.json");
        TemplateConfigurationManager templateConfigurationManager = new TemplateConfigurationManager();
        templateConfigurationManager.setTemplatesConfiguration(templateConfiguration);
        templateConfigurationManager.setObjectConverter(ObjectConverter.createObjectConverterForTests());
        List<Template> templateConfigurations = templateConfigurationManager.getObjectConverter().getJsonUnmarshaller()
                .unmarshallCollection(FileUtils.readFileToString(templateConfiguration.getFile()), List.class, Template.class);
        templateConfigurationManager.setTemplateConfigurations(templateConfigurations);

        SendExecutor sendExecutor = new SendExecutor();
        sendExecutor.setSpringContextHolder(mockSpringContextHolder);


        notificationService.setNotificationDao(mockNotificationDao);
        notificationService.setNotificationEventPublisher(mockNotificationEventPublisher);
        notificationService.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);

        NotificationConfig notificationConfig = new NotificationConfig();
        notificationConfig.setUserBatchRun(true);
        notificationConfig.setUserBatchSize(10);
        notificationConfig.setPurgeDays(30);
        notificationService.setNotificationConfig(notificationConfig);
        notificationService.setSendExecutor(sendExecutor);

        smtpNotificationServer = new SmtpNotificationSender();
        smtpNotificationServer.setTemplateService(mockTemplateService);
        smtpNotificationServer.setTemplatingEngine(mockTemplatingEngine);
        smtpNotificationServer.setUserDao(mockUserDao);
        smtpNotificationServer.setEmailSenderService(mockEmailSenderService);
        smtpNotificationServer.setNotificationUtils(mockNotificationUtils);
        smtpNotificationServer.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);
        smtpNotificationServer.setTemplateConfigurationManager(templateConfigurationManager);

        NotificationSenderFactory notificationSenderFactory = new NotificationSenderFactory();
        Map<String, NotificationSender> notificationSenderMap = new HashMap<>();
        notificationSenderMap.put("smtp", smtpNotificationServer);
        notificationSenderFactory.setNotificationSenderMap(notificationSenderMap);
        sendExecutor.setNotificationSenderFactory(notificationSenderFactory);

        EmailSenderConfig emailSenderConfig = new EmailSenderConfig();
        emailSenderConfig.setHost("host");
        emailSenderConfig.setPort(8080);
        emailSenderConfig.setUsername("user");
        emailSenderConfig.setPassword("password");
        emailSenderConfig.setUserFrom("from");
        emailSenderConfig.setType("smtp");
        emailSenderConfig.setEncryption("off");
        notificationSenderFactory.setEmailSenderConfig(emailSenderConfig);
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
        notification1.setState(null);
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
        notification2.setState(null);
        notification2.setTemplateModelName("modelName");
        notification2.setEmailAddresses("user email");
        notification2.setCcEmailAddresses("cc user email");
        notification2.setBccEmailAddresses("bcc user email");

        notifications.add(notification1);
        notifications.add(notification2);

        expect(mockNotificationDao.getNotificationsToProcess()).andReturn(notifications).times(1);

        mockAuditPropertyEntityAdapter.setUserId(eq("NOTIFICATION-BATCH-INSERT"));
        expectLastCall().anyTimes();

        Capture<Notification> capturedNotification = EasyMock.newCapture();
        expect(mockNotificationDao.save(capture(capturedNotification))).andReturn(notification1).anyTimes();

        Capture<ApplicationNotificationEvent> capturedEvent = EasyMock.newCapture();
        mockNotificationEventPublisher.publishNotificationEvent(capture(capturedEvent));
        expectLastCall().anyTimes();

        String template = "Template";
        expect(mockTemplateService.getTemplate("modelName.html")).andReturn(template).times(2);

        NotificationTemplateModelProvider notificationTemplateModelProvider = createMock(NotificationTemplateModelProvider.class);
        Map<String, NotificationTemplateModelProvider> providerMap = new HashMap<>();
        providerMap.put("modelProvider", notificationTemplateModelProvider);

        expect(mockSpringContextHolder.getAllBeansOfType(NotificationTemplateModelProvider.class)).andReturn(providerMap).anyTimes();

        for (Notification notification : notifications)
        {
            Capture<EmailWithAttachmentsDTO> emailWithAttachmentsDTOCapture = Capture.newInstance();

            expect(smtpNotificationServer.getTemplatingEngine().process(template, notification.getTemplateModelName(), notification))
                    .andReturn("Body").times(1);
            expect(smtpNotificationServer.getTemplatingEngine().process(anyString(), eq(notification.getTemplateModelName()),
                    eq(notification))).andReturn("subject").times(1);
            expect(notificationTemplateModelProvider.getModel(notification)).andReturn(notification).times(1);

            smtpNotificationServer.getEmailSenderService().sendEmail(capture(emailWithAttachmentsDTOCapture), eq(null), eq(null));
            expectLastCall().andStubAnswer(() -> {
                emailWithAttachmentsDTOCapture.getValue().setMailSent(true);
                return null;
            });
        }

        expect(mockNotificationUtils.buildNotificationLink(anyString(), anyLong(), anyString(), anyLong())).andReturn(null).anyTimes();

        replayAll();

        notificationService.run();

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
        notification1.setState(null);
        notification1.setTemplateModelName("modelName");
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
        notification2.setState(null);
        notification2.setTemplateModelName("modelName");
        notification2.setEmailAddresses("user email");

        notifications.add(notification1);
        notifications.add(notification2);

        NotificationTemplateModelProvider notificationTemplateModelProvider = createMock(NotificationTemplateModelProvider.class);
        Map<String, NotificationTemplateModelProvider> providerMap = new HashMap<>();
        providerMap.put("modelProvider", notificationTemplateModelProvider);

        expect(mockSpringContextHolder.getAllBeansOfType(NotificationTemplateModelProvider.class)).andReturn(providerMap).anyTimes();

        expect(mockNotificationDao.getNotificationsToProcess()).andReturn(notifications).anyTimes();

        expect(mockNotificationUtils.buildNotificationLink(anyString(), anyLong(), anyString(), anyLong())).andReturn(null).anyTimes();

        Capture<Notification> capturedNotification = EasyMock.newCapture();
        expect(mockNotificationDao.save(capture(capturedNotification))).andReturn(notification1).anyTimes();

        Capture<ApplicationNotificationEvent> capturedEvent = EasyMock.newCapture();
        mockNotificationEventPublisher.publishNotificationEvent(capture(capturedEvent));
        expectLastCall().anyTimes();

        String template = "Template";
        Capture<String> subjectCapture = EasyMock.newCapture();

        expect(mockTemplateService.getTemplate("modelName.html")).andReturn(template).times(2);

        for (Notification notification : notifications)
        {
            Capture<EmailWithAttachmentsDTO> emailWithAttachmentsDTOCapture = Capture.newInstance();

            expect(smtpNotificationServer.getTemplatingEngine().process(template, notification.getTemplateModelName(), notification))
                    .andReturn("Body").times(1);
            expect(smtpNotificationServer.getTemplatingEngine().process(capture(subjectCapture), eq(notification.getTemplateModelName()),
                    eq(notification)))
                            .andReturn("subject").times(1);
            expect(notificationTemplateModelProvider.getModel(notification)).andReturn(notification).times(1);

            smtpNotificationServer.getEmailSenderService().sendEmail(capture(emailWithAttachmentsDTOCapture), eq(null), eq(null));
            expectLastCall().andStubAnswer(() -> {
                emailWithAttachmentsDTOCapture.getValue().setMailSent(false);
                return null;
            });
        }
        mockAuditPropertyEntityAdapter.setUserId(eq("NOTIFICATION-BATCH-INSERT"));
        expectLastCall().anyTimes();

        replayAll();

        notificationService.run();

        verifyAll();
    }

    @Test
    public void testRunEmailNotSentDueToTemplateError() throws Exception
    {
        Notification notification1 = new Notification();
        notification1.setTitle("title1");
        notification1.setNote("note");
        notification1.setType("type");
        notification1.setEmailAddresses("user email");

        expect(mockNotificationDao.getNotificationsToProcess()).andReturn(Collections.singletonList(notification1)).anyTimes();

        Capture<Notification> capturedNotification = EasyMock.newCapture();
        expect(mockNotificationDao.save(capture(capturedNotification))).andReturn(notification1).anyTimes();

        Capture<ApplicationNotificationEvent> capturedEvent = EasyMock.newCapture();
        mockNotificationEventPublisher.publishNotificationEvent(capture(capturedEvent));
        expectLastCall().anyTimes();

        mockAuditPropertyEntityAdapter.setUserId(eq("NOTIFICATION-BATCH-INSERT"));
        expectLastCall().anyTimes();

        replayAll();

        notificationService.run();

        verifyAll();
        assertEquals(NotificationConstants.STATE_TEMPLATE_ERROR, capturedNotification.getValue().getState());
    }
}
