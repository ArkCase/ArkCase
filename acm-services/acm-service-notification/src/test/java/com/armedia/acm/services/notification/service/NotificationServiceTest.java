/**
 * 
 */
package com.armedia.acm.services.notification.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.capture;
import static org.junit.Assert.assertEquals;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.ApplicationNotificationEvent;
import com.armedia.acm.services.notification.model.BasicNotificationRule;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationRule;
import com.armedia.acm.services.notification.model.QueryType;
import com.armedia.acm.spring.SpringContextHolder;

/**
 * @author riste.tutureski
 *
 */
public class NotificationServiceTest extends EasyMockSupport {

	private NotificationServiceImpl notificationService;
	private NotificationDao mockNotificationDao;
	private PropertyFileManager mockPropertyFileManager;
	private MuleClient mockMuleClient;
	private NotificationEventPublisher mockNotificationEventPublisher;
	private AuditPropertyEntityAdapter mockAuditPropertyEntityAdapter;
	private MuleMessage mockMuleMessage;
	private SpringContextHolder mockSpringContextHolder;
	private SendExecutor sendExecutor;
	private PurgeExecutor purgeExecutor;
	
	@Before
	public void setUp() throws Exception 
	{
		notificationService = new NotificationServiceImpl();
		
		mockNotificationDao = createMock(NotificationDao.class);
		mockPropertyFileManager = createMock(PropertyFileManager.class);
		mockMuleClient = createMock(MuleClient.class);
		mockNotificationEventPublisher = createMock(NotificationEventPublisher.class);
		mockAuditPropertyEntityAdapter = createMock(AuditPropertyEntityAdapter.class);
		mockMuleMessage = createMock(MuleMessage.class);
		mockSpringContextHolder = createMock(SpringContextHolder.class);
		
		sendExecutor = new SendExecutor();
		sendExecutor.setSpringContextHolder(mockSpringContextHolder);
		
		purgeExecutor = new PurgeExecutor();
		purgeExecutor.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);
		
		notificationService.setNotificationDao(mockNotificationDao);
		notificationService.setPropertyFileManager(mockPropertyFileManager);
		notificationService.setMuleClient(mockMuleClient);
		notificationService.setNotificationEventPublisher(mockNotificationEventPublisher);
		notificationService.setSpringContextHolder(mockSpringContextHolder);
		notificationService.setBatchRun(true);
		notificationService.setBatchSize(10);
		notificationService.setPurgeDays(30);
	}

	@Test
	public void testRunEmailSent() 
	{
		List<Notification> notifications = new ArrayList<Notification>();
		
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
		
		EmailNotificationSender emailNotificationSender = new EmailNotificationSender();
		emailNotificationSender.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);
		emailNotificationSender.setMuleClient(mockMuleClient);
		emailNotificationSender.setPropertyFileManager(mockPropertyFileManager);
		
		Map<String, NotificationSender> senders = new HashMap<>();
		senders.put("emailNotificationSender", emailNotificationSender);
		
		// I am using the same captures below multiple times because we don't need to check these captures
		Capture<String> stringCapture = new Capture<String>();
		Capture<Map<String, String>> mapCapture = new Capture<Map<String, String>>();
		Capture<Map<String, Object>> propertiesCapture = new Capture<Map<String, Object>>();
		
		expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.last.run.date"), capture(stringCapture))).andReturn(lastRunDate).anyTimes();
		mockPropertyFileManager.storeMultiple(capture(mapCapture), capture(stringCapture), eq(false));
		expectLastCall().anyTimes();
		expect(mockSpringContextHolder.getAllBeansOfType(NotificationRule.class)).andReturn(rules).anyTimes();
		expect(mockSpringContextHolder.getAllBeansOfType(NotificationSender.class)).andReturn(senders).anyTimes();
		expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(0), eq(10), eq("query"), eq(QueryType.CREATE))).andReturn(notifications).anyTimes();
		mockAuditPropertyEntityAdapter.setUserId(eq("NOTIFICATION-BATCH-INSERT"));
		expectLastCall().anyTimes();
		expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.email.host"), capture(stringCapture))).andReturn("host").anyTimes();
		expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.email.port"), capture(stringCapture))).andReturn("port").anyTimes();
		expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.email.user"), capture(stringCapture))).andReturn("user").anyTimes();
		expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.email.password"), capture(stringCapture))).andReturn("password").anyTimes();
		expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.email.from"), capture(stringCapture))).andReturn("from").anyTimes();
		try 
		{
			expect(mockMuleClient.send(eq("vm://sendEmail.in"), eq("note"), eq(messageProps))).andReturn(mockMuleMessage).anyTimes();
		} catch (MuleException e) 
		{
			
		}
		
		// Return null - SUCCESSFULLY SENT
		expect(mockMuleMessage.getInboundProperty(eq("sendEmailException"))).andReturn(null).anyTimes();
		
		Capture<Notification> capturedNotification = new Capture<Notification>();
		expect(mockNotificationDao.save(capture(capturedNotification))).andReturn(notification1).anyTimes();
		
		Capture<ApplicationNotificationEvent> capturedEvent = new Capture<ApplicationNotificationEvent>();
		mockNotificationEventPublisher.publishNotificationEvent(capture(capturedEvent));
		expectLastCall().anyTimes();
		expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(10), eq(10), eq("query"), eq(QueryType.CREATE))).andReturn(new ArrayList<Notification>()).anyTimes();
		
		replayAll();
		
		notificationService.run();
		
		verifyAll();
	}
	
	@Test
	public void testRunEmailNotSent() 
	{
		List<Notification> notifications = new ArrayList<Notification>();
		
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
		
		EmailNotificationSender emailNotificationSender = new EmailNotificationSender();
		emailNotificationSender.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);
		emailNotificationSender.setMuleClient(mockMuleClient);
		emailNotificationSender.setPropertyFileManager(mockPropertyFileManager);
		
		Map<String, NotificationSender> senders = new HashMap<>();
		senders.put("emailNotificationSender", emailNotificationSender);
		
		// I am using the same captures below multiple times because we don't need to check these captures
		Capture<String> stringCapture = new Capture<String>();
		Capture<Map<String, String>> mapCapture = new Capture<Map<String, String>>();
		Capture<Map<String, Object>> propertiesCapture = new Capture<Map<String, Object>>();
		
		expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.last.run.date"), capture(stringCapture))).andReturn(lastRunDate).anyTimes();
		mockPropertyFileManager.storeMultiple(capture(mapCapture), capture(stringCapture), eq(false));
		expectLastCall().anyTimes();
		expect(mockSpringContextHolder.getAllBeansOfType(NotificationRule.class)).andReturn(rules).anyTimes();
		expect(mockSpringContextHolder.getAllBeansOfType(NotificationSender.class)).andReturn(senders).anyTimes();
		expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(0), eq(10), eq("query"), eq(QueryType.CREATE))).andReturn(notifications).anyTimes();
		mockAuditPropertyEntityAdapter.setUserId(eq("NOTIFICATION-BATCH-INSERT"));
		expectLastCall().anyTimes();
		expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.email.host"), capture(stringCapture))).andReturn("host").anyTimes();
		expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.email.port"), capture(stringCapture))).andReturn("port").anyTimes();
		expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.email.user"), capture(stringCapture))).andReturn("user").anyTimes();
		expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.email.password"), capture(stringCapture))).andReturn("password").anyTimes();
		expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.email.from"), capture(stringCapture))).andReturn("from").anyTimes();
		try 
		{
			expect(mockMuleClient.send(eq("vm://sendEmail.in"), eq("note"), eq(messageProps))).andReturn(mockMuleMessage).anyTimes();
		} catch (MuleException e) 
		{
			
		}
		
		// Return Exception - UNSUCCESSFULLY SENT
		expect(mockMuleMessage.getInboundProperty(eq("sendEmailException"))).andReturn(new Exception("exception")).anyTimes();
		
		Capture<Notification> capturedNotification = new Capture<Notification>();
		expect(mockNotificationDao.save(capture(capturedNotification))).andReturn(notification1).anyTimes();
		
		Capture<ApplicationNotificationEvent> capturedEvent = new Capture<ApplicationNotificationEvent>();
		mockNotificationEventPublisher.publishNotificationEvent(capture(capturedEvent));
		expectLastCall().anyTimes();
		expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(10), eq(10), eq("query"), eq(QueryType.CREATE))).andReturn(new ArrayList<Notification>()).anyTimes();
		
		replayAll();
		
		notificationService.run();
		
		verifyAll();
	}
	
	@Test
	public void testRunPurge() 
	{
		List<Notification> notifications = new ArrayList<Notification>();
		
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
		Capture<String> stringCapture = new Capture<String>();
		Capture<Map<String, String>> mapCapture = new Capture<Map<String, String>>();
		Capture<Map<String, Object>> propertiesCapture = new Capture<Map<String, Object>>();
		
		expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.last.run.date"), capture(stringCapture))).andReturn(lastRunDate).anyTimes();
		mockPropertyFileManager.storeMultiple(capture(mapCapture), capture(stringCapture), eq(false));
		expectLastCall().anyTimes();
		expect(mockSpringContextHolder.getAllBeansOfType(NotificationRule.class)).andReturn(rules).anyTimes();
		expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(0), eq(10), eq("query"), eq(QueryType.SELECT))).andReturn(notifications).anyTimes();
		mockAuditPropertyEntityAdapter.setUserId(eq("NOTIFICATION-BATCH-INSERT"));
		expectLastCall().anyTimes();
		
		Capture<Notification> capturedNotification = new Capture<Notification>();
		expect(mockNotificationDao.save(capture(capturedNotification))).andReturn(notification1).anyTimes();
		
		Capture<ApplicationNotificationEvent> capturedEvent = new Capture<ApplicationNotificationEvent>();
		mockNotificationEventPublisher.publishNotificationEvent(capture(capturedEvent));
		expectLastCall().anyTimes();
		expect(mockNotificationDao.executeQuery(capture(propertiesCapture), eq(10), eq(10), eq("query"), eq(QueryType.SELECT))).andReturn(new ArrayList<Notification>()).anyTimes();
		
		replayAll();
		
		notificationService.run();
		
		verifyAll();
		
		assertEquals("DELETE", capturedNotification.getValue().getStatus());
	}

}
