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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.email.model.EmailWithAttachmentsAndLinksDTO;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.email.sender.model.EmailSenderConfigurationConstants;
import com.armedia.acm.services.email.smtp.SmtpService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SmtpNotificationSenderTest extends EasyMockSupport
{

    private SmtpNotificationSender smtpNotificationSender;
    private SmtpService mockSmtpService;
    private AuditPropertyEntityAdapter mockAuditPropertyEntityAdapter;
    private MuleContextManager mockMuleContextManager;
    private PropertyFileManager mockPropertyFileManager;
    private MuleException mockMuleException;
    private MuleMessage mockMuleMessage;
    private Authentication mockAuthentication;
    private AuthenticationTokenService mockAuthenticationTokenService;
    private AuthenticationTokenDao mockAuthenticationTokenDao;
    private AcmUser mockAcmUser;
    private EcmFileService mockEcmFileService;
    private InputStream mockInputStream;
    private EcmFile mockEcmFile;
    private File mockFile;
    private FileInputStream mockFileInputStream;
    private ApplicationEventPublisher mockApplicationEventPublisher;
    private NotificationUtils mockNotificationUtils;
    private UserDao mockUserDao;
    private EmailWithAttachmentsDTO mockEmailWithAttachmentsDTO;
    private EmailWithEmbeddedLinksDTO mockEmailWithEmbeddedLinksDTO;
    private EmailWithAttachmentsAndLinksDTO mockEmailWithAttachmentsAndLinksDTO;

    @Before
    public void setUp()
    {
        smtpNotificationSender = new SmtpNotificationSender();
        mockSmtpService = createMock(SmtpService.class);
        mockMuleContextManager = createMock(MuleContextManager.class);
        mockAuditPropertyEntityAdapter = createMock(AuditPropertyEntityAdapter.class);
        mockPropertyFileManager = createMock(PropertyFileManager.class);
        mockMuleException = createMock(MuleException.class);
        mockMuleMessage = createMock(MuleMessage.class);
        mockAuthenticationTokenService = createMock(AuthenticationTokenService.class);
        mockAuthenticationTokenDao = createMock(AuthenticationTokenDao.class);
        mockAuthentication = createMock(Authentication.class);
        mockAcmUser = createMock(AcmUser.class);
        mockEcmFileService = createMock(EcmFileService.class);
        mockInputStream = createMock(InputStream.class);
        mockFile = createMock(File.class);
        mockFileInputStream = createMock(FileInputStream.class);
        mockEcmFile = createMock(EcmFile.class);
        mockApplicationEventPublisher = createMock(ApplicationEventPublisher.class);
        mockNotificationUtils = createMock(NotificationUtils.class);
        mockUserDao = createMock(UserDao.class);
        mockEmailWithAttachmentsDTO = createMock(EmailWithAttachmentsDTO.class);
        mockEmailWithEmbeddedLinksDTO = createMock(EmailWithEmbeddedLinksDTO.class);
        mockEmailWithAttachmentsAndLinksDTO = createMock(EmailWithAttachmentsAndLinksDTO.class);

        smtpNotificationSender.setEmailSenderService(mockSmtpService);
        smtpNotificationSender.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);
        smtpNotificationSender.setPropertyFileManager(mockPropertyFileManager);
        smtpNotificationSender.setEmailSenderPropertyFileLocation("");
        smtpNotificationSender.setAuthenticationTokenService(mockAuthenticationTokenService);
        smtpNotificationSender.setAuthenticationTokenDao(mockAuthenticationTokenDao);
        smtpNotificationSender.setEcmFileService(mockEcmFileService);
        smtpNotificationSender.setNotificationUtils(mockNotificationUtils);
        smtpNotificationSender.setUserDao(mockUserDao);
    }

    @Test
    public void testSendWhenException() throws Exception
    {
        Notification notification = new Notification();
        notification.setUserEmail("user_email");
        notification.setTitle("title");
        notification.setNote("the_note");

        expect(mockNotificationUtils.buildNotificationLink(notification.getParentType(), notification.getParentId(),
                notification.getRelatedObjectType(), notification.getRelatedObjectId())).andReturn(null);

        smtpNotificationSender.getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);
        // Capture<Map<String, Object>> messagePropsCapture = EasyMock.newCapture();
        // expect(mockMuleContextManager.send(eq("vm://sendEmailViaSmtp.in"), matches("\\s*the_note\\s*"),
        // capture(messagePropsCapture)))
        // .andThrow(mockMuleException);
        expect(mockMuleException.getLocalizedMessage()).andReturn(null);
        expect(mockMuleException.getStackTrace()).andReturn(new StackTraceElement[1]);
        setSendExpectations();
        // expect(mockPropertyFileManager.load("", EmailSenderConfigurationConstants.ENCRYPTION,
        // null)).andReturn("off");
        expect(mockUserDao.findByUserId("email_user_value")).andReturn(mockAcmUser);
        Capture<EmailWithAttachmentsDTO> dtoCapture = EasyMock.newCapture();
        mockSmtpService.sendEmail(capture(dtoCapture), eq(null), eq(mockAcmUser));
        expectLastCall().andThrow(mockMuleException);

        // when
        replayAll();
        Notification returnedNotification = smtpNotificationSender.send(notification);

        // then
        verifyAll();
        assertEquals(NotificationConstants.STATE_NOT_SENT, returnedNotification.getState());

    }

    @Test
    public void testSend() throws Exception
    {
        Notification notification = new Notification();
        notification.setUserEmail("user_email");
        notification.setTitle("title");
        notification.setNote("the_note");

        expect(mockNotificationUtils.buildNotificationLink(notification.getParentType(), notification.getParentId(),
                notification.getRelatedObjectType(), notification.getRelatedObjectId())).andReturn(null);

        smtpNotificationSender.getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);
        setSendExpectations();
        expect(mockUserDao.findByUserId("email_user_value")).andReturn(mockAcmUser);
        Capture<EmailWithAttachmentsDTO> dtoCapture = EasyMock.newCapture();
        mockSmtpService.sendEmail(capture(dtoCapture), eq(null), eq(mockAcmUser));
        expectLastCall();

        // when
        replayAll();
        Notification returnedNotification = smtpNotificationSender.send(notification);

        // then
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
    public void testSendEmailWithAttachments_withAcmUser() throws MuleException, AcmEncryptionException, Exception
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
    public void testSendEmailWithAttachments_withUserId() throws MuleException, AcmEncryptionException, Exception
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
    public void testSendEmailWithAttachmentsAndLinks() throws MuleException, AcmEncryptionException, Exception
    {
        Capture<String> templateCapture = EasyMock.newCapture();
        mockEmailWithAttachmentsAndLinksDTO.setTemplate(capture(templateCapture));

        mockSmtpService.sendEmailWithAttachmentsAndLinks(mockEmailWithAttachmentsAndLinksDTO, mockAuthentication, mockAcmUser);
        expectLastCall();

        // when
        replayAll();
        smtpNotificationSender.sendEmailWithAttachmentsAndLinks(mockEmailWithAttachmentsAndLinksDTO, mockAuthentication, mockAcmUser);

        // then
        verifyAll();
    }

    private void setSendExpectations() throws AcmEncryptionException
    {
        expect(mockPropertyFileManager.load("", EmailSenderConfigurationConstants.USERNAME, null)).andReturn("email_user_value");
    }

}
