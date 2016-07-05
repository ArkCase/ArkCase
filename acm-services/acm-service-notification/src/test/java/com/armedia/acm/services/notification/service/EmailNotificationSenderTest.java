package com.armedia.acm.services.notification.service;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationTokenConstants;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.notification.model.EmailNotificationDto;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmailNotificationSenderTest extends EasyMockSupport
{
/*
    private EmailNotificationSender emailNotificationSender;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private MuleContextManager muleContextManager;
    private PropertyFileManager propertyFileManager;
    private Notification notification;
    private MuleException muleException;
    private MuleMessage muleMessage;
    private Authentication authentication;
    private EmailNotificationDto emailNotificationDto;
    private AuthenticationTokenService authenticationTokenService;
    private AuthenticationTokenDao authenticationTokenDao;

    @Before
    public void setUp()
    {
        emailNotificationSender = new EmailNotificationSender();
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
    }

    @Test
    public void testSendWhenExceptionSmtp() throws MuleException, AcmEncryptionException
    {
        testSendWhenException("smtp");
    }

    @Test
    public void testSendWhenExceptionOutlook() throws MuleException, AcmEncryptionException
    {
        testSendWhenException("outlook");
    }

    @Test
    public void testSendSmtp() throws MuleException, AcmEncryptionException
    {
        testSend("smtp");
    }

    @Test
    public void testSendOutlook() throws MuleException, AcmEncryptionException
    {
        testSend("outlook");
    }

    @Test
    public void testSendEmailNotificationWithLinksSmtp() throws MuleException, AcmEncryptionException
    {
        testSendEmailNotificationWithLinks("smtp");
    }

    @Test
    public void testSendEmailNotificationWithLinksOutlook() throws MuleException, AcmEncryptionException
    {
        testSendEmailNotificationWithLinks("outlook");
    }

    private void testSendWhenException(String flowType) throws MuleException, AcmEncryptionException
    {
        setupEmailNotificationSender();
        Capture<Map<String, Object>> messagePropsCapture = EasyMock.newCapture();
        if (flowType.equalsIgnoreCase("smtp"))
        {
            expect(muleContextManager.send(eq("vm://sendEmail.in"), eq("the_note"), capture(messagePropsCapture))).andThrow(muleException);
        } else
        {
            expect(muleContextManager.send(eq("vm://sendEmailViaOutlook.in"), eq("the_note"), capture(messagePropsCapture)))
                    .andThrow(muleException);
        }
        expect(muleException.getLocalizedMessage()).andReturn(null);
        expect(muleException.getStackTrace()).andReturn(new StackTraceElement[1]);
        setSendExpectations(flowType);

        // when
        replayAll();
        Notification returnedNotification = emailNotificationSender.send(notification);

        // then
        verifyAll();
        assertEquals(NotificationConstants.STATE_NOT_SENT, returnedNotification.getState());
    }

    private void testSend(String flowType) throws MuleException, AcmEncryptionException
    {
        // given
        setupEmailNotificationSender();
        Capture<Map<String, Object>> messagePropsCapture = EasyMock.newCapture();
        if (flowType.equalsIgnoreCase("smtp"))
        {
            expect(muleContextManager.send(eq("vm://sendEmail.in"), eq("the_note"), capture(messagePropsCapture))).andReturn(muleMessage);
        } else
        {
            expect(muleContextManager.send(eq("vm://sendEmailViaOutlook.in"), eq("the_note"), capture(messagePropsCapture)))
                    .andReturn(muleMessage);
        }
        setSendExpectations(flowType);
        expect(muleMessage.getInboundProperty("sendEmailException")).andReturn(null);

        // when
        replayAll();
        Notification returnedNotification = emailNotificationSender.send(notification);
        // then
        verifyAll();
        assertEquals(NotificationConstants.STATE_SENT, returnedNotification.getState());
    }

    private void testSendEmailNotificationWithLinks(String flowType) throws MuleException, AcmEncryptionException
    {
        final String email = "user_email";
        final String header = "header";
        final String baseUrl = "base_url";
        final String title = "title";
        final String footer = "footer";
        final long fileId = 1234;
        final String token = "token";
        final String note = header + baseUrl + fileId + "&acm_email_ticket=" + token + "\n" + footer;

        List<String> addresses = new ArrayList<>();
        addresses.add(email);
        List<Long> fileIds = new ArrayList<>();
        fileIds.add(fileId);
        emailNotificationDto = new EmailNotificationDto();
        emailNotificationDto.setTitle(title);
        emailNotificationDto.setHeader(header);
        emailNotificationDto.setEmailAddresses(addresses);
        emailNotificationDto.setBaseUrl(baseUrl);
        emailNotificationDto.setFileIds(fileIds);
        emailNotificationDto.setFooter(footer);

        setupEmailNotificationSender();

        List<EmailNotificationDto> emailNotificationDtoList = new ArrayList<>();
        emailNotificationDtoList.add(emailNotificationDto);

        Capture<Map<String, Object>> messagePropsCapture = EasyMock.newCapture();
        if (flowType.equalsIgnoreCase("smtp"))
        {
            expect(muleContextManager.send(eq("vm://sendEmail.in"), eq(note), capture(messagePropsCapture))).andReturn(muleMessage);
        } else
        {
            expect(muleContextManager.send(eq("vm://sendEmailViaOutlook.in"), eq(note), capture(messagePropsCapture)))
                    .andReturn(muleMessage);
        }

        setSendExpectations(flowType);
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
        List<Notification> returnedNotifications = emailNotificationSender.sendEmailNotificationWithLinks(emailNotificationDtoList,
                authentication);

        // then
        verifyAll();
        Notification notification = returnedNotifications.get(0);
        assertEquals(NotificationConstants.STATE_SENT, notification.getState());
        assertEquals(email, notification.getUserEmail());
        assertEquals(title, notification.getTitle());
        assertEquals(NotificationConstants.STATUS_NEW, notification.getStatus());
        assertEquals(note, notification.getNote());
    }

    private void setSendExpectations(String flowType) throws AcmEncryptionException
    {
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_FLOW_TYPE, null)).andReturn(flowType);
        if (flowType.equalsIgnoreCase("smtp"))
        {
            expect(propertyFileManager.load("", NotificationConstants.EMAIL_HOST_KEY, null)).andReturn("host_value");
            expect(propertyFileManager.load("", NotificationConstants.EMAIL_PORT_KEY, null)).andReturn("port_value");
        }
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_USER_KEY, null)).andReturn("email_user_value");
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_PASSWORD_KEY, null)).andReturn("email_password_value");
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_FROM_KEY, null)).andReturn("email_from_value");
    }

    private void setupEmailNotificationSender()
    {
        emailNotificationSender.setAuditPropertyEntityAdapter(auditPropertyEntityAdapter);
        emailNotificationSender.getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);
        emailNotificationSender.setPropertyFileManager(propertyFileManager);
        emailNotificationSender.setMuleContextManager(muleContextManager);
        emailNotificationSender.setNotificationPropertyFileLocation("");
        emailNotificationSender.setAuthenticationTokenService(authenticationTokenService);
        emailNotificationSender.setAuthenticationTokenDao(authenticationTokenDao);
    }
*/
}
