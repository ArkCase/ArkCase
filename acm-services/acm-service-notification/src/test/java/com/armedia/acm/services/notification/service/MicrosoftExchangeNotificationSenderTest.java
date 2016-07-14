package com.armedia.acm.services.notification.service;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.EmailWithAttachmentsDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.service.outlook.model.OutlookDTO;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.users.model.AcmUser;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

public class MicrosoftExchangeNotificationSenderTest extends EasyMockSupport
{

    private MicrosoftExchangeNotificationSender microsoftExchangeNotificationSender;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private PropertyFileManager propertyFileManager;
    private Authentication authentication;
    private EmailWithEmbeddedLinksDTO emailWithEmbeddedLinksDTO;
    private EmailWithAttachmentsDTO emailWithAttachmentsDTO;
    private AcmUser acmUser;
    private EcmFileService ecmFileService;
    private OutlookService outlookService;

    @Before
    public void setUp()
    {
        microsoftExchangeNotificationSender = new MicrosoftExchangeNotificationSender();
        auditPropertyEntityAdapter = createMock(AuditPropertyEntityAdapter.class);
        propertyFileManager = createMock(PropertyFileManager.class);
        authentication = createMock(Authentication.class);
        acmUser = createMock(AcmUser.class);
        ecmFileService = createMock(EcmFileService.class);
        outlookService = createMock(OutlookService.class);
        emailWithAttachmentsDTO = createMock(EmailWithAttachmentsDTO.class);
        emailWithEmbeddedLinksDTO = createMock(EmailWithEmbeddedLinksDTO.class);
    }

    @Test
    public void testSendWhenException() throws AcmEncryptionException, Exception
    {

        setupMicrosoftExchangeNotificationSender();
        microsoftExchangeNotificationSender.getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);

        Notification notification = new Notification();
        notification.setUserEmail("user_email");
        notification.setTitle("title");
        notification.setNote("the_note");

        setSendExpectations();

        Capture<EmailWithAttachmentsDTO> emailWithAttachmentsDTOCapture = EasyMock.newCapture();
        Capture<AcmOutlookUser> outlookUserCapture = EasyMock.newCapture();
        Authentication authentication = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication()
                : null;
        outlookService.sendEmail(capture(emailWithAttachmentsDTOCapture), capture(outlookUserCapture), eq(authentication));
        EasyMock.expectLastCall().andThrow(new Exception("Message not sent"));
        // when
        replayAll();
        Notification returnedNotification = microsoftExchangeNotificationSender.send(notification);

        // then
        verifyAll();
        assertEquals(NotificationConstants.STATE_NOT_SENT, returnedNotification.getState());

    }

    @Test
    public void testSend() throws AcmEncryptionException, Exception
    {
        setupMicrosoftExchangeNotificationSender();
        microsoftExchangeNotificationSender.getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);

        Notification notification = new Notification();
        notification.setUserEmail("user_email");
        notification.setTitle("title");
        notification.setNote("the_note");

        setSendExpectations();

        Capture<EmailWithAttachmentsDTO> emailWithAttachmentsDTOCapture = EasyMock.newCapture();
        Capture<AcmOutlookUser> outlookUserCapture = EasyMock.newCapture();
        Authentication authentication = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication()
                : null;
        outlookService.sendEmail(capture(emailWithAttachmentsDTOCapture), capture(outlookUserCapture), eq(authentication));
        // when
        replayAll();
        Notification returnedNotification = microsoftExchangeNotificationSender.send(notification);

        // then
        verifyAll();
        assertEquals(NotificationConstants.STATE_SENT, returnedNotification.getState());
    }

    @Test
    public void testSendEmailWithEmbeddedLinks() throws MuleException, AcmEncryptionException, Exception
    {
        setupMicrosoftExchangeNotificationSender();

        EmailWithEmbeddedLinksResultDTO emailWithEmbeddedLinksResultDTO = new EmailWithEmbeddedLinksResultDTO("user@armedia.com", true);
        List<EmailWithEmbeddedLinksResultDTO> emailWithEmbeddedLinksResultDTOList = new ArrayList<EmailWithEmbeddedLinksResultDTO>();
        emailWithEmbeddedLinksResultDTOList.add(emailWithEmbeddedLinksResultDTO);

        OutlookDTO outlookDTO = new OutlookDTO();
        outlookDTO.setOutlookPassword("outlookPassword");
        expect(outlookService.retrieveOutlookPassword(authentication)).andReturn(outlookDTO);

        expect(authentication.getName()).andReturn("user");
        expect(acmUser.getMail()).andReturn("user@armedia.com").anyTimes();

        Capture<EmailWithEmbeddedLinksDTO> emailWithEmbeddedLinksCapture = EasyMock.newCapture();
        Capture<AcmOutlookUser> outlookUserCapture = EasyMock.newCapture();
        expect(outlookService.sendEmailWithEmbeddedLinks(capture(emailWithEmbeddedLinksCapture), capture(outlookUserCapture),
                eq(authentication))).andReturn(emailWithEmbeddedLinksResultDTOList);
        replayAll();
        List<EmailWithEmbeddedLinksResultDTO> result = microsoftExchangeNotificationSender
                .sendEmailWithEmbeddedLinks(emailWithEmbeddedLinksDTO, authentication, acmUser);
        verifyAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(result.get(0).getEmailAddress(), "user@armedia.com");
        assertEquals(result.get(0).isState(), true);
    }

    @Test
    public void testSendEmailWithAttachments() throws AcmEncryptionException, Exception
    {

        setupMicrosoftExchangeNotificationSender();

        OutlookDTO outlookDTO = new OutlookDTO();
        outlookDTO.setOutlookPassword("outlookPassword");
        expect(outlookService.retrieveOutlookPassword(authentication)).andReturn(outlookDTO);

        expect(authentication.getName()).andReturn("user");
        expect(acmUser.getMail()).andReturn("user@armedia.com");
        Capture<EmailWithAttachmentsDTO> emailWithAttachmentsDTOCapture = EasyMock.newCapture();
        Capture<AcmOutlookUser> outlookUserCapture = EasyMock.newCapture();
        outlookService.sendEmailWithAttachments(capture(emailWithAttachmentsDTOCapture), capture(outlookUserCapture), eq(authentication));
        replayAll();
        microsoftExchangeNotificationSender.sendEmailWithAttachments(emailWithAttachmentsDTO, authentication, acmUser);
        verifyAll();
    }

    private void setSendExpectations() throws AcmEncryptionException
    {
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_USER_KEY, null)).andReturn("email_user_value");
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_PASSWORD_KEY, null)).andReturn("email_password_value");
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_FROM_KEY, null)).andReturn("email_from_value");
    }

    private void setupMicrosoftExchangeNotificationSender()
    {
        microsoftExchangeNotificationSender.setAuditPropertyEntityAdapter(auditPropertyEntityAdapter);
        microsoftExchangeNotificationSender.setPropertyFileManager(propertyFileManager);
        microsoftExchangeNotificationSender.setNotificationPropertyFileLocation("");
        microsoftExchangeNotificationSender.setEcmFileService(ecmFileService);
        microsoftExchangeNotificationSender.setOutlookService(outlookService);
    }

}
