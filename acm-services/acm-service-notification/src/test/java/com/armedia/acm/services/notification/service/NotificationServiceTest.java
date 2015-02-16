/**
 * 
 */
package com.armedia.acm.services.notification.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.capture;

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
import com.armedia.acm.services.notification.model.AssignmentRule;
import com.armedia.acm.services.notification.model.Notification;

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
		
		notificationService.setNotificationDao(mockNotificationDao);
		notificationService.setPropertyFileManager(mockPropertyFileManager);
		notificationService.setMuleClient(mockMuleClient);
		notificationService.setNotificationEventPublisher(mockNotificationEventPublisher);
		notificationService.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);
		
		AssignmentRule assignRule = new AssignmentRule();
		assignRule.setGlobalRule(true);
		assignRule.setJpaQuery("query");
		
		AssignmentRule unassignRule = new AssignmentRule();
		unassignRule.setGlobalRule(true);
		unassignRule.setJpaQuery("query");
		
		notificationService.setAssignRule(assignRule);
		notificationService.setUnassignRule(unassignRule);
		notificationService.setBatchRun(true);
		notificationService.setBatchSize(10);
	}

	@Test
	public void testRunSent() 
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
		
		// I am using the same captures below multiple times because we don't need to check these captures
		Capture<String> stringCapture = new Capture<String>();
		Capture<Map<String, String>> mapCapture = new Capture<Map<String, String>>();
		Capture<Date> dateCapture = new Capture<Date>();
		
		expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.last.run.date"), capture(stringCapture))).andReturn(lastRunDate).anyTimes();
		mockPropertyFileManager.storeMultiple(capture(mapCapture), capture(stringCapture), eq(false));
		expectLastCall().anyTimes();
		expect(mockNotificationDao.executeQuery(capture(dateCapture), eq(0), eq(10), eq("query"))).andReturn(notifications).anyTimes();
		mockAuditPropertyEntityAdapter.setUserId(eq("ACM3"));
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
		expect(mockNotificationDao.executeQuery(capture(dateCapture), eq(10), eq(10), eq("query"))).andReturn(new ArrayList<Notification>()).anyTimes();
		
		replayAll();
		
		notificationService.run();
	}
	
	@Test
	public void testRunNotSent() 
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
		
		// I am using the same captures below multiple times because we don't need to check these captures
		Capture<String> stringCapture = new Capture<String>();
		Capture<Map<String, String>> mapCapture = new Capture<Map<String, String>>();
		Capture<Date> dateCapture = new Capture<Date>();
		
		expect(mockPropertyFileManager.load(capture(stringCapture), eq("notification.user.last.run.date"), capture(stringCapture))).andReturn(lastRunDate).anyTimes();
		mockPropertyFileManager.storeMultiple(capture(mapCapture), capture(stringCapture), eq(false));
		expectLastCall().anyTimes();
		expect(mockNotificationDao.executeQuery(capture(dateCapture), eq(0), eq(10), eq("query"))).andReturn(notifications).anyTimes();
		mockAuditPropertyEntityAdapter.setUserId(eq("ACM3"));
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
		expect(mockNotificationDao.executeQuery(capture(dateCapture), eq(10), eq(10), eq("query"))).andReturn(new ArrayList<Notification>()).anyTimes();
		
		replayAll();
		
		notificationService.run();
	}

}
