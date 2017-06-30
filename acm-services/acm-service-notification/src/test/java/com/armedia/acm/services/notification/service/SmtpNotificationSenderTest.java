package com.armedia.acm.services.notification.service;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.contains;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.matches;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationTokenConstants;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.email.model.EmailWithAttachmentsAndLinksDTO;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.email.sender.model.EmailSenderConfigurationConstants;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.model.SmtpEventSentEvent;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.powermock.api.easymock.PowerMock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;

import javax.activation.DataHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

    @Test
    @Ignore
    public void testSendWhenExceptionSTARTTLS() throws MuleException, AcmEncryptionException
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
        expect(mockPropertyFileManager.load("", EmailSenderConfigurationConstants.ENCRYPTION, null)).andReturn("starttls");

        // when
        replayAll();
        Notification returnedNotification = smtpNotificationSender.send(notification);

        // then
        verifyAll();
        assertEquals(NotificationConstants.STATE_NOT_SENT, returnedNotification.getState());
        assertEquals("starttls", messagePropsCapture.getValue().get("encryption"));

    }

    @Test
    @Ignore
    public void testSendSTARTTLS() throws MuleException, AcmEncryptionException
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
        expect(mockPropertyFileManager.load("", EmailSenderConfigurationConstants.ENCRYPTION, null)).andReturn("starttls");
        expect(mockMuleMessage.getInboundProperty("sendEmailException")).andReturn(null);

        // when
        replayAll();
        Notification returnedNotification = smtpNotificationSender.send(notification);
        // then
        verifyAll();
        assertEquals(NotificationConstants.STATE_SENT, returnedNotification.getState());
        assertEquals("starttls", messagePropsCapture.getValue().get("encryption"));

    }

    @Test
    @Ignore
    public void testSendEmailWithEmbeddedLinksSTARTTLS() throws MuleException, AcmEncryptionException, Exception
    {
        final String email = "user_email";
        final String header = "header";
        final String baseUrl = "base_url";
        final String title = "title";
        final String footer = "footer";
        final long fileId = 1234;
        final String token = "token";
        final String note = header + "\\s*" + "<br/>" + baseUrl + fileId + "&acm_email_ticket=" + token + "<br/>" + "\\s*" + footer;

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
        expect(mockPropertyFileManager.load("", EmailSenderConfigurationConstants.ENCRYPTION, null)).andReturn("starttls");
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
        assertEquals("starttls", messagePropsCapture.getValue().get("encryption"));

    }

    @Test
    @Ignore
    public void testSendEmailWithAttachmentsSTARTTLS() throws MuleException, AcmEncryptionException, Exception
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

        List<String> filePaths = new ArrayList<>();
        Resource resource = new ClassPathResource("temp.zip");
        filePaths.add(resource.getFile().getAbsolutePath());
        emailWithAttachmentsDTO.setFilePaths(filePaths);

        Capture<Map<String, Object>> messagePropsCapture = EasyMock.newCapture();
        Capture<String> capturedNote = EasyMock.newCapture();
        Capture<Map<String, DataHandler>> capturedAttachments = EasyMock.newCapture();
        expect(mockMuleContextManager.send(eq("vm://sendEmailViaSmtp.in"), capture(capturedNote), capture(capturedAttachments),
                capture(messagePropsCapture))).andReturn(mockMuleMessage);

        setSendExpectations();
        expect(mockPropertyFileManager.load("", EmailSenderConfigurationConstants.ENCRYPTION, null)).andReturn("starttls");

        expect(mockMuleMessage.getInboundProperty("sendEmailException")).andReturn(null);

        Capture<byte[]> read = EasyMock.newCapture();
        expect(mockEcmFileService.downloadAsInputStream(attachmentIds.get(0))).andReturn(mockInputStream);
        expect(mockEcmFileService.findById(attachmentIds.get(0))).andReturn(mockEcmFile);
        expect(mockInputStream.read(capture(read), eq(0), eq(16384))).andReturn(-1);

        expect(mockEcmFile.getFileName()).andReturn("fileName").anyTimes();
        expect(mockEcmFile.getFileActiveVersionNameExtension()).andReturn(".extension").anyTimes();

        mockInputStream.close();
        EasyMock.expectLastCall();

        expect(mockAcmUser.getUserId()).andReturn("ann-acm").atLeastOnce();
        expect(mockEcmFile.getParentObjectId()).andReturn(103L);
        expect(mockEcmFile.getParentObjectType()).andReturn("COMPLAINT");

        // expected calls to raise the file emailed event on the file itself - AFDP-3029
        expect(mockEcmFile.getId()).andReturn(attachmentIds.get(0));
        expect(mockEcmFile.getObjectType()).andReturn(EcmFileConstants.OBJECT_FILE_TYPE);

        mockApplicationEventPublisher.publishEvent(EasyMock.anyObject(SmtpEventSentEvent.class));
        EasyMock.expectLastCall().times(2);

        PowerMock.expectNew(File.class, filePaths.get(0)).andReturn(mockFile);
        PowerMock.expectNew(FileInputStream.class, mockFile).andReturn(mockFileInputStream);
        expect(mockFile.getName()).andReturn("temp.zip").anyTimes();

        // when
        replayAll();
        smtpNotificationSender.sendEmailWithAttachments(emailWithAttachmentsDTO, mockAuthentication, mockAcmUser);

        // then
        verifyAll();

        assertTrue(Pattern.compile(note).matcher(capturedNote.getValue()).matches());

        assertNotNull(capturedAttachments.getValue());
        assertEquals(2, capturedAttachments.getValue().size());
        assertNotNull(capturedAttachments.getValue().get("fileName.extension"));
        assertNotNull(capturedAttachments.getValue().get("temp.zip"));
        assertEquals("starttls", messagePropsCapture.getValue().get("encryption"));

    }

    private void setSendExpectations() throws AcmEncryptionException
    {
        // expect(mockPropertyFileManager.load("", EmailSenderConfigurationConstants.HOST,
        // null)).andReturn("host_value");
        // expect(mockPropertyFileManager.load("", EmailSenderConfigurationConstants.PORT,
        // null)).andReturn("port_value");
        expect(mockPropertyFileManager.load("", EmailSenderConfigurationConstants.USERNAME, null)).andReturn("email_user_value");
        // expect(mockPropertyFileManager.load("", EmailSenderConfigurationConstants.PASSWORD,
        // null)).andReturn("email_password_value");
        // expect(mockPropertyFileManager.load("", EmailSenderConfigurationConstants.USER_FROM,
        // null)).andReturn("email_from_value");
    }

}
