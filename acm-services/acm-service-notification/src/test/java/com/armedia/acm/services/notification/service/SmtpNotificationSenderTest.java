package com.armedia.acm.services.notification.service;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import com.armedia.acm.services.users.model.AcmUser;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.springframework.security.core.Authentication;

import javax.activation.DataHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SmtpNotificationSenderTest extends EasyMockSupport
{

    private SmtpNotificationSender smtpNotificationSender;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private MuleContextManager muleContextManager;
    private PropertyFileManager propertyFileManager;
    private Notification notification;
    private MuleException muleException;
    private MuleMessage muleMessage;
    private Authentication authentication;
    private AuthenticationTokenService authenticationTokenService;
    private AuthenticationTokenDao authenticationTokenDao;
    private EmailWithEmbeddedLinksDTO emailWithEmbeddedLinksDTO;
    private EmailWithAttachmentsDTO emailWithAttachmentsDTO;
    private AcmUser acmUser;
    private EcmFileService ecmFileService;
    private InputStream inputStream;
    private EcmFile ecmFile;

    @Before
    public void setUp()
    {
        smtpNotificationSender = new SmtpNotificationSender();
        muleContextManager = createMock(MuleContextManager.class);
        auditPropertyEntityAdapter = createMock(AuditPropertyEntityAdapter.class);
        propertyFileManager = createMock(PropertyFileManager.class);
        notification = new Notification();
        notification.setUserEmail("user_email");
        notification.setTitle("title");
        notification.setNote("the_note");
        muleException = createMock(MuleException.class);
        muleMessage = createMock(MuleMessage.class);
        authenticationTokenService = createMock(AuthenticationTokenService.class);
        authenticationTokenDao = createMock(AuthenticationTokenDao.class);
        authentication = createMock(Authentication.class);
        acmUser = createMock(AcmUser.class);
        ecmFileService = createMock(EcmFileService.class);
        inputStream = createMock(InputStream.class);
        ecmFile = createMock(EcmFile.class);
    }

    @Test
    public void testSendWhenException() throws MuleException, AcmEncryptionException
    {
        setupSmtpNotificationSender();
        smtpNotificationSender.getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);
        Capture<Map<String, Object>> messagePropsCapture = EasyMock.newCapture();
        expect(muleContextManager.send(eq("vm://sendEmailViaSmtp.in"), eq("the_note"), capture(messagePropsCapture)))
                .andThrow(muleException);
        expect(muleException.getLocalizedMessage()).andReturn(null);
        expect(muleException.getStackTrace()).andReturn(new StackTraceElement[1]);
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
        // given
        setupSmtpNotificationSender();
        smtpNotificationSender.getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);
        Capture<Map<String, Object>> messagePropsCapture = EasyMock.newCapture();
        expect(muleContextManager.send(eq("vm://sendEmailViaSmtp.in"), eq("the_note"), capture(messagePropsCapture)))
                .andReturn(muleMessage);
        setSendExpectations();
        expect(muleMessage.getInboundProperty("sendEmailException")).andReturn(null);

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
        final String note = header + " http://" + baseUrl + fileId + "&acm_email_ticket=" + token + "\n" + footer;

        List<String> addresses = new ArrayList<>();
        addresses.add(email);
        List<Long> fileIds = new ArrayList<>();
        fileIds.add(fileId);
        emailWithEmbeddedLinksDTO = new EmailWithEmbeddedLinksDTO();
        emailWithEmbeddedLinksDTO.setTitle(title);
        emailWithEmbeddedLinksDTO.setHeader(header);
        emailWithEmbeddedLinksDTO.setEmailAddresses(addresses);
        emailWithEmbeddedLinksDTO.setBaseUrl(baseUrl);
        emailWithEmbeddedLinksDTO.setFileIds(fileIds);
        emailWithEmbeddedLinksDTO.setFooter(footer);

        setupSmtpNotificationSender();

        Capture<Map<String, Object>> messagePropsCapture = EasyMock.newCapture();
        expect(muleContextManager.send(eq("vm://sendEmailViaSmtp.in"), eq(note), capture(messagePropsCapture))).andReturn(muleMessage);

        setSendExpectations();
        expect(muleMessage.getInboundProperty("sendEmailException")).andReturn(null);

        expect(authenticationTokenService.getUncachedTokenForAuthentication(authentication)).andReturn(token);
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setKey(token);
        authenticationToken.setStatus(AuthenticationTokenConstants.ACTIVE);
        authenticationToken.setEmail(email);
        authenticationToken.setFileId(fileId);

        expect(authenticationTokenDao.save(EasyMock.anyObject(AuthenticationToken.class))).andReturn(authenticationToken);

        // when
        replayAll();
        List<EmailWithEmbeddedLinksResultDTO> results = smtpNotificationSender.sendEmailWithEmbeddedLinks(emailWithEmbeddedLinksDTO,
                authentication, acmUser);

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
        final String note = header + "\r\r" + body + "\r\r\r" + footer;

        List<String> addresses = new ArrayList<>();
        addresses.add(email);
        emailWithAttachmentsDTO = new EmailWithAttachmentsDTO();
        emailWithAttachmentsDTO.setEmailAddresses(addresses);
        emailWithAttachmentsDTO.setHeader(header);
        emailWithAttachmentsDTO.setBody(body);
        emailWithAttachmentsDTO.setFooter(footer);

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(new Long(999));
        emailWithAttachmentsDTO.setAttachmentIds(attachmentIds);

        setupSmtpNotificationSender();

        Capture<Map<String, Object>> messagePropsCapture = EasyMock.newCapture();
        Capture<String> capturedNote = EasyMock.newCapture();
        Capture<Map<String, DataHandler>> capturedAttachments = EasyMock.newCapture();
        expect(muleContextManager.send(eq("vm://sendEmailViaSmtp.in"), capture(capturedNote), capture(capturedAttachments),
                capture(messagePropsCapture))).andReturn(muleMessage);

        setSendExpectations();

        expect(muleMessage.getInboundProperty("sendEmailException")).andReturn(null);

        Capture<byte[]> read = EasyMock.newCapture();
        expect(ecmFileService.downloadAsInputStream(attachmentIds.get(0))).andReturn(inputStream);
        expect(ecmFileService.findById(attachmentIds.get(0))).andReturn(ecmFile);
        expect(inputStream.read(capture(read), eq(0), eq(16384))).andReturn(-1);

        expect(ecmFile.getFileName()).andReturn("fileName").anyTimes();

        inputStream.close();
        EasyMock.expectLastCall();

        // when
        replayAll();
        smtpNotificationSender.sendEmailWithAttachments(emailWithAttachmentsDTO, authentication, acmUser);

        // then
        verifyAll();

        assertEquals(note, capturedNote.getValue());

        assertNotNull(capturedAttachments.getValue());
        assertEquals(1, capturedAttachments.getValue().size());
        assertNotNull(capturedAttachments.getValue().get("fileName"));
    }

    private void setSendExpectations() throws AcmEncryptionException
    {
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_HOST_KEY, null)).andReturn("host_value");
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_PORT_KEY, null)).andReturn("port_value");
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_USER_KEY, null)).andReturn("email_user_value");
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_PASSWORD_KEY, null)).andReturn("email_password_value");
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_FROM_KEY, null)).andReturn("email_from_value");
    }

    private void setupSmtpNotificationSender()
    {
        smtpNotificationSender.setAuditPropertyEntityAdapter(auditPropertyEntityAdapter);
        smtpNotificationSender.setPropertyFileManager(propertyFileManager);
        smtpNotificationSender.setMuleContextManager(muleContextManager);
        smtpNotificationSender.setNotificationPropertyFileLocation("");
        smtpNotificationSender.setAuthenticationTokenService(authenticationTokenService);
        smtpNotificationSender.setAuthenticationTokenDao(authenticationTokenDao);
        smtpNotificationSender.setEcmFileService(ecmFileService);
    }

}
