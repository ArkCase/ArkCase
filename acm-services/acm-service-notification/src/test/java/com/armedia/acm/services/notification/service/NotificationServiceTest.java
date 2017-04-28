package com.armedia.acm.services.notification.service;

import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.contains;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.ApplicationNotificationEvent;
import com.armedia.acm.services.notification.model.BasicNotificationRule;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationRule;
import com.armedia.acm.services.notification.model.QueryType;
import com.armedia.acm.spring.SpringContextHolder;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationServiceTest extends EasyMockSupport
{

    private NotificationServiceImpl notificationService;
    private NotificationDao mockNotificationDao;
    private PropertyFileManager mockPropertyFileManager;
    private MuleContextManager mockMuleContextManager;
    private NotificationEventPublisher mockNotificationEventPublisher;
    private AuditPropertyEntityAdapter mockAuditPropertyEntityAdapter;
    private MuleMessage mockMuleMessage;
    private SpringContextHolder mockSpringContextHolder;
    private SendExecutor sendExecutor;
    private PurgeExecutor purgeExecutor;
    private NotificationFormatter mockNotificationFormatter;

    @Before
    public void setUp() throws Exception
    {
        notificationService = new NotificationServiceImpl();

        mockNotificationDao = createMock(NotificationDao.class);
        mockPropertyFileManager = createMock(PropertyFileManager.class);
        mockMuleContextManager = createMock(MuleContextManager.class);
        mockNotificationEventPublisher = createMock(NotificationEventPublisher.class);
        mockAuditPropertyEntityAdapter = createMock(AuditPropertyEntityAdapter.class);
        mockMuleMessage = createMock(MuleMessage.class);
        mockSpringContextHolder = createMock(SpringContextHolder.class);

        sendExecutor = new SendExecutor();
        sendExecutor.setSpringContextHolder(mockSpringContextHolder);

        purgeExecutor = new PurgeExecutor();
        purgeExecutor.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);

        mockNotificationFormatter = createMock(NotificationFormatter.class);

        notificationService.setNotificationDao(mockNotificationDao);
        notificationService.setPropertyFileManager(mockPropertyFileManager);
        notificationService.setNotificationEventPublisher(mockNotificationEventPublisher);
        notificationService.setSpringContextHolder(mockSpringContextHolder);
        notificationService.setBatchRun(true);
        notificationService.setBatchSize(10);
        notificationService.setPurgeDays(30);
        notificationService.setNotificationFormatter(mockNotificationFormatter);
        notificationService.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);
    }

    @Test
    public void testRunEmailSent() throws AcmEncryptionException
    {
        List<Notification> notifications = new ArrayList<>();

        Notification notification1 = new Notification();
        notification1.setUser("user");
        notification1.setTitle("title");
        notification1.setNote("note");
        notification1.setType("type");
        notification1.setParentId(2L);
        notification1.setParentType("parent type");
        notification1.setParentName("parent name");
        notification1.setParentTitle("parent title");
        notification1.setUserEmail("user email");
        notification1.setStatus("status");
        notification1.setAction("action");
        notification1.setData("data");
        notification1.setState("state");

        Notification notification2 = new Notification();
        notification2.setUser("user");
        notification2.setTitle("title");
        notification2.setNote("note");
        notification2.setType("type");
        notification2.setParentId(2L);
        notification2.setParentType("parent type");
        notification2.setParentName("parent name");
        notification2.setParentTitle("parent title");
        notification2.setUserEmail("user email");
        notification2.setStatus("status");
        notification2.setAction("action");
        notification2.setData("data");
        notification2.setState("state");

        notifications.add(notification1);
        notifications.add(notification2);

        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("host", "host");
        messageProps.put("port", "port");
        messageProps.put("user", "user");
        messageProps.put("password", "password");
        messageProps.put("from", "from");
        messageProps.put("to", "user email");
        messageProps.put("subject", "title");

        String lastRunDate = "1970-01-01T00:00:00Z";

        BasicNotificationRule assignRule = new BasicNotificationRule();
        assignRule.setGlobalRule(true);
        assignRule.setJpaQuery("query");
        assignRule.setQueryType(QueryType.CREATE);
        assignRule.setExecutor(sendExecutor);

        BasicNotificationRule unassignRule = new BasicNotificationRule();
        unassignRule.setGlobalRule(true);
        unassignRule.setJpaQuery("query");
        unassignRule.setQueryType(QueryType.CREATE);
        unassignRule.setExecutor(sendExecutor);

        Map<String, NotificationRule> rules = new HashMap<>();
        rules.put("assignRule", assignRule);
        rules.put("unassignRule", unassignRule);

        NotificationSenderFactory notificationSenderFactory = new NotificationSenderFactory();
        // notificationSenderFactory.setPropertyFileManager(mockPropertyFileManager);
        Map<String, NotificationSender> notificationSenderMap = new HashMap<>();
        SmtpNotificationSender smtpNotificationServer = new SmtpNotificationSender();
        smtpNotificationServer.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);
        smtpNotificationServer.setMuleContextManager(mockMuleContextManager);
        smtpNotificationServer.setPropertyFileManager(mockPropertyFileManager);
        notificationSenderMap.put("smtp", smtpNotificationServer);
        notificationSenderFactory.setNotificationSenderMap(notificationSenderMap);

        NotificationUtils mockNotificationUtils = createMock(NotificationUtils.class);
        smtpNotificationServer.setNotificationUtils(mockNotificationUtils);

        Map<String, NotificationSenderFactory> senders = new HashMap<>();
        senders.put("notificationSender", notificationSenderFactory);

        // I am using the same captures below multiple times because we don't need to check these captures
        Capture<String> stringCapture = new Capture<>();
        Capture<Map<String, String>> mapCapture = new Capture<>();
        Capture<Map<String, Object>> propertiesCapture = new Capture<>();
        Capture<NotificationRule> ruleCapture = Capture.newInstance();

        expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.last.run.date"), capture(stringCapture)))
                .andReturn(lastRunDate).anyTimes();
        mockPropertyFileManager.storeMultiple(capture(mapCapture), capture(stringCapture), eq(true));
        expectLastCall().anyTimes();
        expect(mockSpringContextHolder.getAllBeansOfType(NotificationRule.class)).andReturn(rules).anyTimes();
        expect(mockSpringContextHolder.getAllBeansOfType(NotificationSenderFactory.class)).andReturn(senders).anyTimes();
        expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(0), eq(10), capture(ruleCapture))).andReturn(notifications)
                .anyTimes();
        mockAuditPropertyEntityAdapter.setUserId(eq("NOTIFICATION-BATCH-INSERT"));
        expectLastCall().anyTimes();
        expect(mockPropertyFileManager.load(capture(stringCapture), eq("email.sender.host"), capture(stringCapture))).andReturn("host")
                .anyTimes();
        expect(mockPropertyFileManager.load(capture(stringCapture), eq("email.sender.port"), capture(stringCapture))).andReturn("port")
                .anyTimes();
        expect(mockPropertyFileManager.load(capture(stringCapture), eq("email.sender.username"), capture(stringCapture))).andReturn("user")
                .anyTimes();
        expect(mockPropertyFileManager.load(capture(stringCapture), eq("email.sender.password"), capture(stringCapture)))
                .andReturn("password").anyTimes();
        expect(mockPropertyFileManager.load(capture(stringCapture), eq("email.sender.userFrom"), capture(stringCapture))).andReturn("from")
                .anyTimes();
        expect(mockPropertyFileManager.load(capture(stringCapture), eq("email.sender.type"), capture(stringCapture))).andReturn("smtp")
                .anyTimes();
        try
        {
            Capture<Map<String, Object>> messagePropsCapture = EasyMock.newCapture();
            expect(mockMuleContextManager.send(eq("vm://sendEmailViaSmtp.in"), contains("note"), capture(messagePropsCapture)))
                    .andReturn(mockMuleMessage).anyTimes();
        } catch (MuleException e)
        {

        }

        // Return null - SUCCESSFULLY SENT
        expect(mockMuleMessage.getInboundProperty(eq("sendEmailException"))).andReturn(null).anyTimes();

        Capture<Notification> capturedNotification = new Capture<>();
        expect(mockNotificationDao.save(capture(capturedNotification))).andReturn(notification1).anyTimes();

        Capture<ApplicationNotificationEvent> capturedEvent = new Capture<>();
        mockNotificationEventPublisher.publishNotificationEvent(capture(capturedEvent));
        expectLastCall().anyTimes();
        expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(10), eq(10), capture(ruleCapture)))
                .andReturn(new ArrayList<>()).anyTimes();
        expect(mockNotificationFormatter.replaceFormatPlaceholders(notification1)).andReturn(notification1).atLeastOnce();
        expect(mockNotificationFormatter.replaceFormatPlaceholders(notification2)).andReturn(notification2).atLeastOnce();
        expect(mockNotificationUtils.buildNotificationLink(anyString(), anyLong(), anyString(), anyLong())).andReturn(null).anyTimes();

        replayAll();

        notificationService.run();

        verifyAll();
    }

    @Test
    public void testRunEmailNotSent() throws AcmEncryptionException
    {
        List<Notification> notifications = new ArrayList<>();

        Notification notification1 = new Notification();
        notification1.setUser("user");
        notification1.setTitle("title");
        notification1.setNote("note");
        notification1.setType("type");
        notification1.setParentId(2L);
        notification1.setParentType("parent type");
        notification1.setParentName("parent name");
        notification1.setParentTitle("parent title");
        notification1.setUserEmail("user email");
        notification1.setStatus("status");
        notification1.setAction("action");
        notification1.setData("data");
        notification1.setState("state");

        Notification notification2 = new Notification();
        notification2.setUser("user");
        notification2.setTitle("title");
        notification2.setNote("note");
        notification2.setType("type");
        notification2.setParentId(2L);
        notification2.setParentType("parent type");
        notification2.setParentName("parent name");
        notification2.setParentTitle("parent title");
        notification2.setUserEmail("user email");
        notification2.setStatus("status");
        notification2.setAction("action");
        notification2.setData("data");
        notification2.setState("state");

        notifications.add(notification1);
        notifications.add(notification2);

        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("host", "host");
        messageProps.put("port", "port");
        messageProps.put("user", "user");
        messageProps.put("password", "password");
        messageProps.put("from", "from");
        messageProps.put("to", "user email");
        messageProps.put("subject", "title");

        String lastRunDate = "1970-01-01T00:00:00Z";

        BasicNotificationRule assignRule = new BasicNotificationRule();
        assignRule.setGlobalRule(true);
        assignRule.setJpaQuery("query");
        assignRule.setQueryType(QueryType.CREATE);
        assignRule.setExecutor(sendExecutor);

        BasicNotificationRule unassignRule = new BasicNotificationRule();
        unassignRule.setGlobalRule(true);
        unassignRule.setJpaQuery("query");
        unassignRule.setQueryType(QueryType.CREATE);
        unassignRule.setExecutor(sendExecutor);

        Map<String, NotificationRule> rules = new HashMap<>();
        rules.put("assignRule", assignRule);
        rules.put("unassignRule", unassignRule);

        NotificationUtils mockNotificationUtils = createMock(NotificationUtils.class);

        NotificationSenderFactory notificationSenderFactory = new NotificationSenderFactory();
        // notificationSenderFactory.setPropertyFileManager(mockPropertyFileManager);
        Map<String, NotificationSender> notificationSenderMap = new HashMap<>();

        SmtpNotificationSender smtpNotificationServer = new SmtpNotificationSender();
        smtpNotificationServer.setNotificationUtils(mockNotificationUtils);
        smtpNotificationServer.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);
        smtpNotificationServer.setMuleContextManager(mockMuleContextManager);
        smtpNotificationServer.setPropertyFileManager(mockPropertyFileManager);
        notificationSenderMap.put("smtp", smtpNotificationServer);
        notificationSenderFactory.setNotificationSenderMap(notificationSenderMap);

        Map<String, NotificationSenderFactory> senders = new HashMap<>();
        senders.put("notificationSender", notificationSenderFactory);

        // I am using the same captures below multiple times because we don't need to check these captures
        Capture<String> stringCapture = new Capture<>();
        Capture<Map<String, String>> mapCapture = new Capture<>();
        Capture<Map<String, Object>> propertiesCapture = new Capture<>();
        Capture<NotificationRule> ruleCapture = Capture.newInstance();

        expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.last.run.date"), capture(stringCapture)))
                .andReturn(lastRunDate).anyTimes();
        mockPropertyFileManager.storeMultiple(capture(mapCapture), capture(stringCapture), eq(true));
        expectLastCall().anyTimes();
        expect(mockSpringContextHolder.getAllBeansOfType(NotificationRule.class)).andReturn(rules).anyTimes();
        expect(mockSpringContextHolder.getAllBeansOfType(NotificationSenderFactory.class)).andReturn(senders).anyTimes();
        expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(0), eq(10), capture(ruleCapture))).andReturn(notifications)
                .anyTimes();

        mockAuditPropertyEntityAdapter.setUserId(eq("NOTIFICATION-BATCH-INSERT"));
        expectLastCall().anyTimes();
        expect(mockPropertyFileManager.load(capture(stringCapture), eq("email.sender.host"), capture(stringCapture))).andReturn("host")
                .anyTimes();
        expect(mockPropertyFileManager.load(capture(stringCapture), eq("email.sender.port"), capture(stringCapture))).andReturn("port")
                .anyTimes();
        expect(mockPropertyFileManager.load(capture(stringCapture), eq("email.sender.username"), capture(stringCapture))).andReturn("user")
                .anyTimes();
        expect(mockPropertyFileManager.load(capture(stringCapture), eq("email.sender.password"), capture(stringCapture)))
                .andReturn("password").anyTimes();
        expect(mockPropertyFileManager.load(capture(stringCapture), eq("email.sender.userFrom"), capture(stringCapture))).andReturn("from")
                .anyTimes();
        expect(mockPropertyFileManager.load(capture(stringCapture), eq("email.sender.type"), capture(stringCapture))).andReturn("smtp")
                .anyTimes();

        expect(mockNotificationFormatter.replaceFormatPlaceholders(notification1)).andReturn(notification1).atLeastOnce();
        expect(mockNotificationFormatter.replaceFormatPlaceholders(notification2)).andReturn(notification2).atLeastOnce();
        expect(mockNotificationUtils.buildNotificationLink(anyString(), anyLong(), anyString(), anyLong())).andReturn(null).anyTimes();

        try
        {
            Capture<Map<String, Object>> messagePropsCapture = EasyMock.newCapture();
            expect(mockMuleContextManager.send(eq("vm://sendEmailViaSmtp.in"), contains("note"), capture(messagePropsCapture)))
                    .andReturn(mockMuleMessage).anyTimes();
        } catch (MuleException e)
        {

        }

        // Return Exception - UNSUCCESSFULLY SENT
        expect(mockMuleMessage.getInboundProperty(eq("sendEmailException"))).andReturn(new Exception("exception")).anyTimes();

        Capture<Notification> capturedNotification = new Capture<>();
        expect(mockNotificationDao.save(capture(capturedNotification))).andReturn(notification1).anyTimes();

        Capture<ApplicationNotificationEvent> capturedEvent = new Capture<>();
        mockNotificationEventPublisher.publishNotificationEvent(capture(capturedEvent));
        expectLastCall().anyTimes();
        expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(10), eq(10), capture(ruleCapture)))
                .andReturn(new ArrayList<>()).anyTimes();

        replayAll();

        notificationService.run();

        verifyAll();
    }

    @Test
    public void testRunPurge() throws AcmEncryptionException
    {
        List<Notification> notifications = new ArrayList<>();

        Notification notification1 = new Notification();
        notification1.setUser("user");
        notification1.setTitle("title");
        notification1.setNote("note");
        notification1.setType("type");
        notification1.setParentId(2L);
        notification1.setParentType("parent type");
        notification1.setParentName("parent name");
        notification1.setParentTitle("parent title");
        notification1.setUserEmail("user email");
        notification1.setStatus("status");
        notification1.setAction("action");
        notification1.setData("data");
        notification1.setState("state");

        Notification notification2 = new Notification();
        notification2.setUser("user");
        notification2.setTitle("title");
        notification2.setNote("note");
        notification2.setType("type");
        notification2.setParentId(2L);
        notification2.setParentType("parent type");
        notification2.setParentName("parent name");
        notification2.setParentTitle("parent title");
        notification2.setUserEmail("user email");
        notification2.setStatus("status");
        notification2.setAction("action");
        notification2.setData("data");
        notification2.setState("state");

        // Return only notification 1 - imagine that notification 2 should not be deleted
        notifications.add(notification1);

        String lastRunDate = "1970-01-01T00:00:00Z";

        BasicNotificationRule singleQueryRule = new BasicNotificationRule();
        singleQueryRule.setGlobalRule(true);
        singleQueryRule.setJpaQuery("query");
        singleQueryRule.setQueryType(QueryType.SELECT);
        singleQueryRule.setExecutor(purgeExecutor);

        Map<String, NotificationRule> rules = new HashMap<>();
        rules.put("purgeRule", singleQueryRule);

        // I am using the same captures below multiple times because we don't need to check these captures
        Capture<String> stringCapture = new Capture<>();
        Capture<Map<String, String>> mapCapture = new Capture<>();
        Capture<Map<String, Object>> propertiesCapture = new Capture<>();

        expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.last.run.date"), capture(stringCapture)))
                .andReturn(lastRunDate).anyTimes();
        mockPropertyFileManager.storeMultiple(capture(mapCapture), capture(stringCapture), eq(true));
        expectLastCall().anyTimes();
        expect(mockSpringContextHolder.getAllBeansOfType(NotificationRule.class)).andReturn(rules).anyTimes();
        expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(0), eq(10), eq(singleQueryRule))).andReturn(notifications)
                .anyTimes();
        mockAuditPropertyEntityAdapter.setUserId(eq("NOTIFICATION-BATCH-INSERT"));
        expectLastCall().anyTimes();

        Capture<Notification> capturedNotification = new Capture<>();
        expect(mockNotificationDao.save(capture(capturedNotification))).andReturn(notification1).anyTimes();

        Capture<ApplicationNotificationEvent> capturedEvent = new Capture<>();
        mockNotificationEventPublisher.publishNotificationEvent(capture(capturedEvent));
        expectLastCall().anyTimes();
        expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(10), eq(10), eq(singleQueryRule)))
                .andReturn(new ArrayList<>()).anyTimes();

        expect(mockNotificationFormatter.replaceFormatPlaceholders(notification1)).andReturn(notification1).atLeastOnce();

        replayAll();

        notificationService.run();

        verifyAll();

        assertEquals("DELETE", capturedNotification.getValue().getStatus());
    }

}
