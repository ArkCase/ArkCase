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

import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.email.model.EmailSenderConfig;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.service.outlook.model.OutlookDTO;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.email.service.AcmEmailConfigurationIOException;
import com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService;
import com.armedia.acm.services.email.service.TemplatingEngine;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConfig;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

public class MicrosoftExchangeNotificationSenderTest extends EasyMockSupport
{
    private static final String BASE_URL = "/arkcase";
    private MicrosoftExchangeNotificationSender microsoftExchangeNotificationSender;
    private AuditPropertyEntityAdapter mockAuditPropertyEntityAdapter;
    private Authentication mockAuthentication;
    private EmailWithEmbeddedLinksDTO mockEmailWithEmbeddedLinksDTO;
    private EmailWithAttachmentsDTO mockEmailWithAttachmentsDTO;
    private AcmUser mockAcmUser;
    private EcmFileService mockEcmFileService;
    private OutlookService mockOutlookService;
    private NotificationUtils mockNotificationUtils;
    private UserDao mockUserDao;
    private AcmMailTemplateConfigurationService templateService;
    private AcmDataService acmDataService;
    private TemplatingEngine templatingEngine;

    @Before
    public void setUp()
    {
        microsoftExchangeNotificationSender = new MicrosoftExchangeNotificationSender();
        mockAuditPropertyEntityAdapter = createMock(AuditPropertyEntityAdapter.class);
        mockAuthentication = createMock(Authentication.class);
        mockAcmUser = createMock(AcmUser.class);
        mockEcmFileService = createMock(EcmFileService.class);
        mockOutlookService = createMock(OutlookService.class);
        mockEmailWithAttachmentsDTO = createMock(EmailWithAttachmentsDTO.class);
        mockEmailWithEmbeddedLinksDTO = createMock(EmailWithEmbeddedLinksDTO.class);
        mockNotificationUtils = createMock(NotificationUtils.class);
        mockUserDao = createMock(UserDao.class);
        templateService = createMock(AcmMailTemplateConfigurationService.class);
        acmDataService = createMock(AcmDataService.class);
        templatingEngine = createMock(TemplatingEngine.class);

        microsoftExchangeNotificationSender.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);
        microsoftExchangeNotificationSender.setEcmFileService(mockEcmFileService);
        microsoftExchangeNotificationSender.setEmailSenderService(mockOutlookService);
        microsoftExchangeNotificationSender.setNotificationUtils(mockNotificationUtils);
        microsoftExchangeNotificationSender.setUserDao(mockUserDao);

        EmailSenderConfig senderConfig = new EmailSenderConfig();
        senderConfig.setUsername("email_user_value");
        microsoftExchangeNotificationSender.setTemplateService(templateService);
        microsoftExchangeNotificationSender.setDataService(acmDataService);
        microsoftExchangeNotificationSender.setTemplatingEngine(templatingEngine);

        NotificationConfig notificationConfig = new NotificationConfig();
        notificationConfig.setBaseUrl(BASE_URL);
        microsoftExchangeNotificationSender.setNotificationConfig(notificationConfig);
    }

    @Test
    public void testSendWhenNoTemplateAndException() throws Exception
    {
        microsoftExchangeNotificationSender.getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);

        Notification notification = new Notification();
        notification.setEmailAddresses("user_email");
        notification.setTitle("title");
        notification.setNote("the_note");

        expect(templateService.getTemplate(anyString())).andThrow(new AcmEmailConfigurationIOException("No such template"));
        expect(mockNotificationUtils.buildNotificationLink(notification.getParentType(), notification.getParentId(),
                notification.getRelatedObjectType(), notification.getRelatedObjectId())).andReturn(null);

        Capture<EmailWithAttachmentsDTO> emailWithAttachmentsDTOCapture = EasyMock.newCapture();
        Capture<AcmUser> outlookCapture = EasyMock.newCapture();
        Authentication authentication = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication()
                : null;
        mockOutlookService.sendEmail(capture(emailWithAttachmentsDTOCapture), eq(authentication), capture(outlookCapture));
        EasyMock.expectLastCall().andThrow(new Exception("Message not sent"));

        Object object = new Object();
        // when
        replayAll();
        Notification returnedNotification = microsoftExchangeNotificationSender.send(notification, object);

        // then
        verifyAll();
        assertEquals(NotificationConstants.STATE_NOT_SENT, returnedNotification.getState());

    }

    @Test
    public void testSend() throws Exception
    {
        microsoftExchangeNotificationSender.getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);

        Notification notification = new Notification();
        notification.setUserEmail("user_email");
        notification.setEmailAddresses("no-suchmail@armedia.com");
        notification.setTitle("title");
        notification.setNote("the_note");
        notification.setTemplateModelName("modelName");
        notification.setParentType("parentType");
        notification.setParentId(111L);
        Object object = new Object();

        String templateName = String.format("%s.html", notification.getTemplateModelName());
        String template = "template";

        expect(templateService.getTemplate(templateName)).andReturn(template);

        String body = "body";
        expect(templatingEngine.process(template, notification.getTemplateModelName(), object)).andReturn(body);

        Capture<EmailWithAttachmentsDTO> emailWithAttachmentsDTOCapture = EasyMock.newCapture();
        Capture<AcmUser> userCapture = EasyMock.newCapture();
        Authentication authentication = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication()
                : null;
        mockOutlookService.sendEmail(capture(emailWithAttachmentsDTOCapture), eq(authentication), capture(userCapture));
        expectLastCall().andStubAnswer(() -> {
            emailWithAttachmentsDTOCapture.getValue().setMailSent(true);
            return null;
        });

        // when
        replayAll();
        Notification returnedNotification = microsoftExchangeNotificationSender.send(notification, object);

        // then
        verifyAll();
        assertEquals(NotificationConstants.STATE_SENT, returnedNotification.getState());
    }

    @Test
    public void testSendEmailWithEmbeddedLinks() throws Exception
    {
        EmailWithEmbeddedLinksResultDTO emailWithEmbeddedLinksResultDTO = new EmailWithEmbeddedLinksResultDTO("user@armedia.com", true);
        List<EmailWithEmbeddedLinksResultDTO> emailWithEmbeddedLinksResultDTOList = new ArrayList<>();
        emailWithEmbeddedLinksResultDTOList.add(emailWithEmbeddedLinksResultDTO);

        OutlookDTO outlookDTO = new OutlookDTO();
        outlookDTO.setOutlookPassword("outlookPassword");

        expect(mockEmailWithEmbeddedLinksDTO.getTemplate()).andReturn("Some template");

        Capture<EmailWithEmbeddedLinksDTO> emailWithEmbeddedLinksCapture = EasyMock.newCapture();
        Capture<AcmUser> userCapture = EasyMock.newCapture();
        expect(mockOutlookService.sendEmailWithEmbeddedLinks(capture(emailWithEmbeddedLinksCapture), eq(mockAuthentication),
                capture(userCapture))).andReturn(emailWithEmbeddedLinksResultDTOList);
        replayAll();
        List<EmailWithEmbeddedLinksResultDTO> result = microsoftExchangeNotificationSender
                .sendEmailWithEmbeddedLinks(mockEmailWithEmbeddedLinksDTO, mockAuthentication, mockAcmUser);

        verifyAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(result.get(0).getEmailAddress(), "user@armedia.com");
        assertTrue(result.get(0).isState());
    }

    @Test
    public void testSendEmailWithAttachments() throws Exception
    {
        expect(mockEmailWithAttachmentsDTO.getTemplate()).andReturn("Some template");

        Capture<AcmUser> userCapture = EasyMock.newCapture();
        mockOutlookService.sendEmailWithAttachments(eq(mockEmailWithAttachmentsDTO), eq(mockAuthentication), capture(userCapture));

        replayAll();
        microsoftExchangeNotificationSender.sendEmailWithAttachments(mockEmailWithAttachmentsDTO, mockAuthentication, mockAcmUser);
        verifyAll();
    }
}
