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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.email.model.EmailSenderConfig;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.email.model.EmailWithAttachmentsAndLinksDTO;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.email.service.AcmEmailConfigurationIOException;
import com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService;
import com.armedia.acm.services.email.service.TemplatingEngine;
import com.armedia.acm.services.email.smtp.SmtpService;
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

public class SmtpNotificationSenderTest extends EasyMockSupport
{
    private static final String BASE_URL = "/arkcase";
    private SmtpNotificationSender smtpNotificationSender;
    private SmtpService mockSmtpService;
    private AuditPropertyEntityAdapter mockAuditPropertyEntityAdapter;
    private Authentication mockAuthentication;
    private AuthenticationTokenService mockAuthenticationTokenService;
    private AuthenticationTokenDao mockAuthenticationTokenDao;
    private AcmUser mockAcmUser;
    private EcmFileService mockEcmFileService;
    private NotificationUtils mockNotificationUtils;
    private UserDao mockUserDao;
    private EmailWithAttachmentsDTO mockEmailWithAttachmentsDTO;
    private EmailWithEmbeddedLinksDTO mockEmailWithEmbeddedLinksDTO;
    private EmailWithAttachmentsAndLinksDTO mockEmailWithAttachmentsAndLinksDTO;
    private AcmMailTemplateConfigurationService templateService;
    private AcmDataService acmDataService;
    private TemplatingEngine templatingEngine;

    @Before
    public void setUp()
    {
        smtpNotificationSender = new SmtpNotificationSender();
        EmailSenderConfig senderConfig = new EmailSenderConfig();
        senderConfig.setUsername("email_user_value");
        mockSmtpService = createMock(SmtpService.class);
        mockAuditPropertyEntityAdapter = createMock(AuditPropertyEntityAdapter.class);
        mockAuthenticationTokenService = createMock(AuthenticationTokenService.class);
        mockAuthenticationTokenDao = createMock(AuthenticationTokenDao.class);
        mockAuthentication = createMock(Authentication.class);
        mockAcmUser = createMock(AcmUser.class);
        mockEcmFileService = createMock(EcmFileService.class);
        mockNotificationUtils = createMock(NotificationUtils.class);
        mockUserDao = createMock(UserDao.class);
        mockEmailWithAttachmentsDTO = createMock(EmailWithAttachmentsDTO.class);
        mockEmailWithEmbeddedLinksDTO = createMock(EmailWithEmbeddedLinksDTO.class);
        mockEmailWithAttachmentsAndLinksDTO = createMock(EmailWithAttachmentsAndLinksDTO.class);
        templateService = createMock(AcmMailTemplateConfigurationService.class);
        acmDataService = createMock(AcmDataService.class);
        templatingEngine = createMock(TemplatingEngine.class);

        smtpNotificationSender.setEmailSenderService(mockSmtpService);
        smtpNotificationSender.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);
        smtpNotificationSender.setAuthenticationTokenService(mockAuthenticationTokenService);
        smtpNotificationSender.setAuthenticationTokenDao(mockAuthenticationTokenDao);
        smtpNotificationSender.setEcmFileService(mockEcmFileService);
        smtpNotificationSender.setNotificationUtils(mockNotificationUtils);
        smtpNotificationSender.setUserDao(mockUserDao);
        smtpNotificationSender.setTemplateService(templateService);
        smtpNotificationSender.setDataService(acmDataService);
        smtpNotificationSender.setTemplatingEngine(templatingEngine);
        NotificationConfig notificationConfig = new NotificationConfig();
        notificationConfig.setBaseUrl(BASE_URL);
        smtpNotificationSender.setNotificationConfig(notificationConfig);
    }

    @Test
    public void testSendWhenNoTemplateAndException() throws Exception
    {
        Notification notification = new Notification();
        notification.setEmailAddresses("user_email");
        notification.setTitle("title");
        notification.setNote("the_note");

        expect(templateService.getTemplate(anyString())).andThrow(new AcmEmailConfigurationIOException("No such template"));
        expect(mockNotificationUtils.buildNotificationLink(notification.getParentType(), notification.getParentId(),
                notification.getRelatedObjectType(), notification.getRelatedObjectId())).andReturn(null);

        smtpNotificationSender.getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);

        Capture<EmailWithAttachmentsDTO> emailWithAttachmentsDTOCapture = EasyMock.newCapture();
        Capture<AcmUser> user = EasyMock.newCapture();
        Authentication authentication = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication()
                : null;

        mockSmtpService.sendEmail(capture(emailWithAttachmentsDTOCapture), eq(authentication), capture(user));
        EasyMock.expectLastCall().andThrow(new Exception("Message not sent"));

        Object object = new Object();
        // when
        replayAll();
        Notification returnedNotification = smtpNotificationSender.send(notification, object);

        // then
        verifyAll();
        assertEquals(NotificationConstants.STATE_NOT_SENT, returnedNotification.getState());

    }

    @Test
    public void testSend_WithNewEmailTemplate() throws Exception
    {
        smtpNotificationSender.getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);

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

        Capture<EmailWithAttachmentsDTO> dtoCapture = EasyMock.newCapture();
        Capture<AcmUser> userCapture = EasyMock.newCapture();
        Authentication authentication = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication()
                : null;
        mockSmtpService.sendEmail(capture(dtoCapture), eq(authentication), capture(userCapture));
        expectLastCall().andStubAnswer(() -> {
            dtoCapture.getValue().setMailSent(true);
            return null;
        });

        // when
        replayAll();
        Notification returnedNotification = smtpNotificationSender.send(notification, object);

        verifyAll();
        assertEquals(NotificationConstants.STATE_SENT, returnedNotification.getState());

    }

    @Test
    public void testSendEmailWithEmbeddedLinks() throws Exception
    {
        List<EmailWithEmbeddedLinksResultDTO> returnList = new ArrayList<>();

        Capture<String> templateCapture = EasyMock.newCapture();
        mockEmailWithEmbeddedLinksDTO.setTemplate(capture(templateCapture));
        expect(mockEmailWithEmbeddedLinksDTO.getTemplate()).andReturn(null);

        expect(mockSmtpService.sendEmailWithEmbeddedLinks(mockEmailWithEmbeddedLinksDTO, mockAuthentication, mockAcmUser))
                .andReturn(returnList);

        // when
        replayAll();
        List<EmailWithEmbeddedLinksResultDTO> resultList = smtpNotificationSender.sendEmailWithEmbeddedLinks(mockEmailWithEmbeddedLinksDTO,
                mockAuthentication, mockAcmUser);

        // then
        verifyAll();
        assertThat(resultList.isEmpty(), is(true));
    }

    @Test
    public void testSendEmailWithAttachments_withAcmUser() throws AcmEncryptionException, Exception
    {
        Capture<String> templateCapture = EasyMock.newCapture();
        mockEmailWithAttachmentsDTO.setTemplate(capture(templateCapture));
        expect(mockEmailWithAttachmentsDTO.getTemplate()).andReturn(null);

        mockSmtpService.sendEmailWithAttachments(mockEmailWithAttachmentsDTO, mockAuthentication, mockAcmUser);
        expectLastCall();

        // when
        replayAll();
        smtpNotificationSender.sendEmailWithAttachments(mockEmailWithAttachmentsDTO, mockAuthentication, mockAcmUser);

        // then
        verifyAll();
    }

    @Test
    public void testSendEmailWithAttachments_withUserId() throws AcmEncryptionException, Exception
    {
        expect(mockUserDao.findByUserId("email_user_value")).andReturn(mockAcmUser);

        Capture<String> templateCapture = EasyMock.newCapture();
        mockEmailWithAttachmentsDTO.setTemplate(capture(templateCapture));
        expect(mockEmailWithAttachmentsDTO.getTemplate()).andReturn(null);

        mockSmtpService.sendEmailWithAttachments(mockEmailWithAttachmentsDTO, mockAuthentication, mockAcmUser);
        expectLastCall();

        // when
        replayAll();
        smtpNotificationSender.sendEmailWithAttachments(mockEmailWithAttachmentsDTO, mockAuthentication, "email_user_value");

        // then
        verifyAll();
    }

    @Test
    public void testSendEmailWithAttachmentsAndLinks() throws AcmEncryptionException, Exception
    {
        Capture<String> templateCapture = EasyMock.newCapture();
        mockEmailWithAttachmentsAndLinksDTO.setTemplate(capture(templateCapture));
        expect(mockEmailWithAttachmentsAndLinksDTO.getTemplate()).andReturn(null);

        mockSmtpService.sendEmailWithAttachmentsAndLinks(mockEmailWithAttachmentsAndLinksDTO, mockAuthentication, mockAcmUser);
        expectLastCall();

        // when
        replayAll();
        smtpNotificationSender.sendEmailWithAttachmentsAndLinks(mockEmailWithAttachmentsAndLinksDTO, mockAuthentication, mockAcmUser);

        // then
        verifyAll();
    }
}
