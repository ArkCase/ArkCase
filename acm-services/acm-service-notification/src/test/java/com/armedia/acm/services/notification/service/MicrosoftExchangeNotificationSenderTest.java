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
import com.armedia.acm.service.outlook.model.OutlookDTO;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.email.sender.model.EmailSenderConfigurationConstants;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

public class MicrosoftExchangeNotificationSenderTest extends EasyMockSupport
{

    private MicrosoftExchangeNotificationSender microsoftExchangeNotificationSender;
    private AuditPropertyEntityAdapter mockAuditPropertyEntityAdapter;
    private PropertyFileManager mockPropertyFileManager;
    private Authentication mockAuthentication;
    private EmailWithEmbeddedLinksDTO mockEmailWithEmbeddedLinksDTO;
    private EmailWithAttachmentsDTO mockEmailWithAttachmentsDTO;
    private AcmUser mockAcmUser;
    private EcmFileService mockEcmFileService;
    private OutlookService mockOutlookService;
    private NotificationUtils mockNotificationUtils;
    private UserDao mockUserDao;

    @Before
    public void setUp()
    {
        microsoftExchangeNotificationSender = new MicrosoftExchangeNotificationSender();
        mockAuditPropertyEntityAdapter = createMock(AuditPropertyEntityAdapter.class);
        mockPropertyFileManager = createMock(PropertyFileManager.class);
        mockAuthentication = createMock(Authentication.class);
        mockAcmUser = createMock(AcmUser.class);
        mockEcmFileService = createMock(EcmFileService.class);
        mockOutlookService = createMock(OutlookService.class);
        mockEmailWithAttachmentsDTO = createMock(EmailWithAttachmentsDTO.class);
        mockEmailWithEmbeddedLinksDTO = createMock(EmailWithEmbeddedLinksDTO.class);
        mockNotificationUtils = createMock(NotificationUtils.class);
        mockUserDao = createMock(UserDao.class);

        microsoftExchangeNotificationSender.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);
        microsoftExchangeNotificationSender.setPropertyFileManager(mockPropertyFileManager);
        microsoftExchangeNotificationSender.setEmailSenderPropertyFileLocation("");
        microsoftExchangeNotificationSender.setEcmFileService(mockEcmFileService);
        microsoftExchangeNotificationSender.setEmailSenderService(mockOutlookService);
        microsoftExchangeNotificationSender.setNotificationUtils(mockNotificationUtils);
        microsoftExchangeNotificationSender.setUserDao(mockUserDao);
    }

    @Test
    public void testSendWhenException() throws AcmEncryptionException, Exception
    {
        microsoftExchangeNotificationSender.getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);

        Notification notification = new Notification();
        notification.setUserEmail("user_email");
        notification.setTitle("title");
        notification.setNote("the_note");

        expect(mockNotificationUtils.buildNotificationLink(notification.getParentType(), notification.getParentId(),
                notification.getRelatedObjectType(), notification.getRelatedObjectId())).andReturn(null);

        setSendExpectations();

        Capture<EmailWithAttachmentsDTO> emailWithAttachmentsDTOCapture = EasyMock.newCapture();
        Capture<AcmUser> outlookCapture = EasyMock.newCapture();
        Authentication authentication = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication()
                : null;
        mockOutlookService.sendEmail(capture(emailWithAttachmentsDTOCapture), eq(authentication), capture(outlookCapture));
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
        microsoftExchangeNotificationSender.getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);

        Notification notification = new Notification();
        notification.setUserEmail("user_email");
        notification.setTitle("title");
        notification.setNote("the_note");

        expect(mockNotificationUtils.buildNotificationLink(notification.getParentType(), notification.getParentId(),
                notification.getRelatedObjectType(), notification.getRelatedObjectId())).andReturn(null);

        setSendExpectations();

        Capture<EmailWithAttachmentsDTO> emailWithAttachmentsDTOCapture = EasyMock.newCapture();
        Capture<AcmUser> userCapture = EasyMock.newCapture();
        Authentication authentication = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication()
                : null;
        mockOutlookService.sendEmail(capture(emailWithAttachmentsDTOCapture), eq(authentication), capture(userCapture));
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
        EmailWithEmbeddedLinksResultDTO emailWithEmbeddedLinksResultDTO = new EmailWithEmbeddedLinksResultDTO("user@armedia.com", true);
        List<EmailWithEmbeddedLinksResultDTO> emailWithEmbeddedLinksResultDTOList = new ArrayList<>();
        emailWithEmbeddedLinksResultDTOList.add(emailWithEmbeddedLinksResultDTO);

        OutlookDTO outlookDTO = new OutlookDTO();
        outlookDTO.setOutlookPassword("outlookPassword");

        mockEmailWithEmbeddedLinksDTO.setTemplate(null);

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
        assertEquals(result.get(0).isState(), true);
    }

    @Test
    public void testSendEmailWithAttachments() throws AcmEncryptionException, Exception
    {
        OutlookDTO outlookDTO = new OutlookDTO();
        outlookDTO.setOutlookPassword("outlookPassword");

        Capture<EmailWithAttachmentsDTO> emailWithAttachmentsDTOCapture = EasyMock.newCapture();
        Capture<AcmUser> userCapture = EasyMock.newCapture();
        mockOutlookService.sendEmailWithAttachments(capture(emailWithAttachmentsDTOCapture), eq(mockAuthentication), capture(userCapture));
        mockEmailWithAttachmentsDTO.setTemplate(null);
        replayAll();
        microsoftExchangeNotificationSender.sendEmailWithAttachments(mockEmailWithAttachmentsDTO, mockAuthentication, mockAcmUser);
        verifyAll();
    }

    private void setSendExpectations() throws AcmEncryptionException
    {
        expect(mockPropertyFileManager.load("", EmailSenderConfigurationConstants.USERNAME, null)).andReturn("email_user_value");
        expect(mockUserDao.findByUserId("email_user_value")).andReturn(mockAcmUser);
    }

}
