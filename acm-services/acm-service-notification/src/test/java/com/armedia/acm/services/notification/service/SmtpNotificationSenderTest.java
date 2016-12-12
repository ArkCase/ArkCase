package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.service.outlook.model.EmailWithAttachmentsDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationTokenConstants;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.model.SmtpEventSentEvent;
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

import javax.activation.DataHandler;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class SmtpNotificationSenderTest extends EasyMockSupport
{

    private SmtpNotificationSender smtpNotificationSender;
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
    private ApplicationEventPublisher mockApplicationEventPublisher;
    private NotificationUtils mockNotificationUtils;

    @Before
    public void setUp()
    {
        smtpNotificationSender = new SmtpNotificationSender();
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
        mockEcmFile = createMock(EcmFile.class);
        mockApplicationEventPublisher = createMock(ApplicationEventPublisher.class);
        mockNotificationUtils = createMock(NotificationUtils.class);

        smtpNotificationSender.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);
        smtpNotificationSender.setPropertyFileManager(mockPropertyFileManager);
        smtpNotificationSender.setMuleContextManager(mockMuleContextManager);
        smtpNotificationSender.setNotificationPropertyFileLocation("");
        smtpNotificationSender.setAuthenticationTokenService(mockAuthenticationTokenService);
        smtpNotificationSender.setAuthenticationTokenDao(mockAuthenticationTokenDao);
        smtpNotificationSender.setEcmFileService(mockEcmFileService);
        smtpNotificationSender.setApplicationEventPublisher(mockApplicationEventPublisher);
        smtpNotificationSender.setNotificationUtils(mockNotificationUtils);
    }

    @Test
    public void testSendWhenException() throws MuleException, AcmEncryptionException
    {
        Notification notification = new Notification();
        notification.setUserEmail("user_email");
        notification.setTitle("title");
        notification.setNote("the_note");

        expect(mockNotificationUtils.buildNotificationLink(notification.getParentType(), notification.getParentId(),
                notification.getRelatedObjectType(), notification.getRelatedObjectId())).andReturn(null);

        smtpNotificationSender.getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);
        Capture<Map<String, Object>> messagePropsCapture = EasyMock.newCapture();
        expect(mockMuleContextManager.send(eq("vm://sendEmailViaSmtp.in"), matches("\\s*the_note\\s*"), capture(messagePropsCapture)))
                .andThrow(mockMuleException);
        expect(mockMuleException.getLocalizedMessage()).andReturn(null);
        expect(mockMuleException.getStackTrace()).andReturn(new StackTraceElement[1]);
        setSendExpectations();

        // when
        replayAll();
        Notification returnedNotification = smtpNotificationSender.send(notification);

        // then
        verifyAll();
        assertEquals(NotificationConstants.STATE_NOT_SENT, returnedNotification.getState());
    }

    @Test
    public void testSend() throws MuleException, AcmEncryptionException
    {
        Notification notification = new Notification();
        notification.setUserEmail("user_email");
        notification.setTitle("title");
        notification.setNote("the_note");

        expect(mockNotificationUtils.buildNotificationLink(notification.getParentType(), notification.getParentId(),
                notification.getRelatedObjectType(), notification.getRelatedObjectId())).andReturn(null);

        smtpNotificationSender.getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);
        Capture<Map<String, Object>> messagePropsCapture = EasyMock.newCapture();
        expect(mockMuleContextManager.send(eq("vm://sendEmailViaSmtp.in"), contains("the_note"), capture(messagePropsCapture)))
                .andReturn(mockMuleMessage);
        setSendExpectations();
        expect(mockMuleMessage.getInboundProperty("sendEmailException")).andReturn(null);

        // when
        replayAll();
        Notification returnedNotification = smtpNotificationSender.send(notification);
        // then
        verifyAll();
        assertEquals(NotificationConstants.STATE_SENT, returnedNotification.getState());
    }

    @Test
    public void testSendEmailWithEmbeddedLinks() throws MuleException, AcmEncryptionException, Exception
    {
        final String email = "user_email";
        final String header = "header";
        final String baseUrl = "base_url";
        final String title = "title";
        final String footer = "footer";
        final long fileId = 1234;
        final String token = "token";
        final String note = header + "\\s* http://" + baseUrl + fileId + "&acm_email_ticket=" + token + "\\s*" + footer;

        List<String> addresses = new ArrayList<>();
        addresses.add(email);
        List<Long> fileIds = new ArrayList<>();
        fileIds.add(fileId);
        EmailWithEmbeddedLinksDTO emailWithEmbeddedLinksDTO = new EmailWithEmbeddedLinksDTO();
        emailWithEmbeddedLinksDTO.setTitle(title);
        emailWithEmbeddedLinksDTO.setHeader(header);
        emailWithEmbeddedLinksDTO.setEmailAddresses(addresses);
        emailWithEmbeddedLinksDTO.setBaseUrl(baseUrl);
        emailWithEmbeddedLinksDTO.setFileIds(fileIds);
        emailWithEmbeddedLinksDTO.setFooter(footer);

        Capture<Map<String, Object>> messagePropsCapture = EasyMock.newCapture();
        expect(mockMuleContextManager.send(eq("vm://sendEmailViaSmtp.in"), matches(note), capture(messagePropsCapture)))
                .andReturn(mockMuleMessage);

        setSendExpectations();
        expect(mockMuleMessage.getInboundProperty("sendEmailException")).andReturn(null);

        expect(mockAuthenticationTokenService.getUncachedTokenForAuthentication(mockAuthentication)).andReturn(token);
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setKey(token);
        authenticationToken.setStatus(AuthenticationTokenConstants.ACTIVE);
        authenticationToken.setEmail(email);
        authenticationToken.setFileId(fileId);

        expect(mockAuthenticationTokenDao.save(EasyMock.anyObject(AuthenticationToken.class))).andReturn(authenticationToken);

        expect(mockAcmUser.getUserId()).andReturn("ann-acm");

        mockApplicationEventPublisher.publishEvent(EasyMock.anyObject(SmtpEventSentEvent.class));
        EasyMock.expectLastCall();

        // when
        replayAll();
        List<EmailWithEmbeddedLinksResultDTO> results = smtpNotificationSender.sendEmailWithEmbeddedLinks(emailWithEmbeddedLinksDTO,
                mockAuthentication, mockAcmUser);

        // then
        verifyAll();
        EmailWithEmbeddedLinksResultDTO emailWithEmbeddedLinksResultDTO = results.get(0);
        assertEquals(Boolean.TRUE, emailWithEmbeddedLinksResultDTO.isState());
        assertEquals(email, emailWithEmbeddedLinksResultDTO.getEmailAddress());
    }

    @Test
    public void testSendEmailWithAttachments() throws MuleException, AcmEncryptionException, Exception
    {
        final String email = "user_email";
        final String header = "header";
        final String body = "body";
        final String footer = "footer";
        final String note = header + "\\s*" + body + "\\s*" + footer;

        List<String> addresses = new ArrayList<>();
        addresses.add(email);
        EmailWithAttachmentsDTO emailWithAttachmentsDTO = new EmailWithAttachmentsDTO();
        emailWithAttachmentsDTO.setEmailAddresses(addresses);
        emailWithAttachmentsDTO.setHeader(header);
        emailWithAttachmentsDTO.setBody(body);
        emailWithAttachmentsDTO.setFooter(footer);

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(new Long(999));
        emailWithAttachmentsDTO.setAttachmentIds(attachmentIds);

        Capture<Map<String, Object>> messagePropsCapture = EasyMock.newCapture();
        Capture<String> capturedNote = EasyMock.newCapture();
        Capture<Map<String, DataHandler>> capturedAttachments = EasyMock.newCapture();
        expect(mockMuleContextManager.send(eq("vm://sendEmailViaSmtp.in"), capture(capturedNote), capture(capturedAttachments),
                capture(messagePropsCapture))).andReturn(mockMuleMessage);

        setSendExpectations();

        expect(mockMuleMessage.getInboundProperty("sendEmailException")).andReturn(null);

        Capture<byte[]> read = EasyMock.newCapture();
        expect(mockEcmFileService.downloadAsInputStream(attachmentIds.get(0))).andReturn(mockInputStream);
        expect(mockEcmFileService.findById(attachmentIds.get(0))).andReturn(mockEcmFile);
        expect(mockInputStream.read(capture(read), eq(0), eq(16384))).andReturn(-1);

        expect(mockEcmFile.getFileName()).andReturn("fileName").anyTimes();
        expect(mockEcmFile.getFileActiveVersionNameExtension()).andReturn(".extension").anyTimes();

        mockInputStream.close();
        EasyMock.expectLastCall();

        expect(mockAcmUser.getUserId()).andReturn("ann-acm");
        expect(mockEcmFile.getParentObjectId()).andReturn(103L);
        expect(mockEcmFile.getParentObjectType()).andReturn("COMPLAINT");

        mockApplicationEventPublisher.publishEvent(EasyMock.anyObject(SmtpEventSentEvent.class));
        EasyMock.expectLastCall();

        // when
        replayAll();
        smtpNotificationSender.sendEmailWithAttachments(emailWithAttachmentsDTO, mockAuthentication, mockAcmUser);

        // then
        verifyAll();

        assertTrue(Pattern.compile(note).matcher(capturedNote.getValue()).matches());

        assertNotNull(capturedAttachments.getValue());
        assertEquals(1, capturedAttachments.getValue().size());
        assertNotNull(capturedAttachments.getValue().get("fileName.extension"));
    }

    private void setSendExpectations() throws AcmEncryptionException
    {
        expect(mockPropertyFileManager.load("", NotificationConstants.EMAIL_HOST_KEY, null)).andReturn("host_value");
        expect(mockPropertyFileManager.load("", NotificationConstants.EMAIL_PORT_KEY, null)).andReturn("port_value");
        expect(mockPropertyFileManager.load("", NotificationConstants.EMAIL_USER_KEY, null)).andReturn("email_user_value");
        expect(mockPropertyFileManager.load("", NotificationConstants.EMAIL_PASSWORD_KEY, null)).andReturn("email_password_value");
        expect(mockPropertyFileManager.load("", NotificationConstants.EMAIL_FROM_KEY, null)).andReturn("email_from_value");
    }

}
