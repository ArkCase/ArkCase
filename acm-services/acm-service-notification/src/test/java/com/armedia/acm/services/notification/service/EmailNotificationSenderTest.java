package com.armedia.acm.services.notification.service;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.springframework.security.core.Authentication;

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

public class EmailNotificationSenderTest extends EasyMockSupport
{
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
    public void testSendWhenException() throws MuleException
    {
        setupEmailNotificationSender();
        Map<String, Object> messageProps = getMessageProps();
        expect(muleContextManager.send("vm://sendEmail.in", "the_note", messageProps)).andThrow(muleException);
        expect(muleException.getLocalizedMessage()).andReturn(null);
        expect(muleException.getStackTrace()).andReturn(new StackTraceElement[1]);
        setSendExpectations();

        // when
        replayAll();
        Notification returnedNotification = emailNotificationSender.send(notification);

        // then
        verifyAll();
        assertEquals(NotificationConstants.STATE_NOT_SENT, returnedNotification.getState());
    }

    @Test
    public void testSend() throws MuleException
    {
        // given
        setupEmailNotificationSender();
        Map<String, Object> messageProps = getMessageProps();
        expect(muleContextManager.send("vm://sendEmail.in", "the_note", messageProps)).andReturn(muleMessage);
        setSendExpectations();
        expect(muleMessage.getInboundProperty("sendEmailException")).andReturn(null);

        // when
        replayAll();
        Notification returnedNotification = emailNotificationSender.send(notification);
        // then
        verifyAll();
        assertEquals(NotificationConstants.STATE_SENT, returnedNotification.getState());
    }

    @Test
    public void testSendEmailNotificationWithLinks() throws MuleException
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

        expect(muleContextManager.send("vm://sendEmail.in", note, getMessageProps())).andReturn(muleMessage);

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

    private void setSendExpectations()
    {
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_HOST_KEY, null)).andReturn("host_value");
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_PORT_KEY, null)).andReturn("port_value");
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_USER_KEY, null)).andReturn("email_user_value");
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_PASSWORD_KEY, null)).andReturn("email_password_value");
        expect(propertyFileManager.load("", NotificationConstants.EMAIL_FROM_KEY, null)).andReturn("email_from_value");
    }

    private Map<String, Object> getMessageProps()
    {
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("host", "host_value");
        messageProps.put("port", "port_value");
        messageProps.put("user", "email_user_value");
        messageProps.put("password", "email_password_value");
        messageProps.put("from", "email_from_value");
        messageProps.put("to", "user_email");
        messageProps.put("subject", "title");
        return messageProps;
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

}
