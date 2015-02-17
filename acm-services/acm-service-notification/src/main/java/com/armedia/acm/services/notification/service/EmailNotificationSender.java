/**
 * 
 */
package com.armedia.acm.services.notification.service;

import java.util.HashMap;
import java.util.Map;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;

/**
 * @author riste.tutureski
 *
 */
public class EmailNotificationSender implements NotificationSender {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private PropertyFileManager propertyFileManager;
    private String notificationPropertyFileLocation;
    private MuleClient muleClient;
	
	@Override
	public Notification send(Notification notification) 
	{
		Exception exception = null;
		
		try 
		{
			getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);
			
			Map<String, Object> messageProps = new HashMap<>();
			messageProps.put("host", getPropertyFileManager().load(getNotificationPropertyFileLocation(), NotificationConstants.EMAIL_HOST_KEY, null));
			messageProps.put("port", getPropertyFileManager().load(getNotificationPropertyFileLocation(), NotificationConstants.EMAIL_PORT_KEY, null));
			messageProps.put("user", getPropertyFileManager().load(getNotificationPropertyFileLocation(), NotificationConstants.EMAIL_USER_KEY, null));
			messageProps.put("password", getPropertyFileManager().load(getNotificationPropertyFileLocation(), NotificationConstants.EMAIL_PASSWORD_KEY, null));
			messageProps.put("from", getPropertyFileManager().load(getNotificationPropertyFileLocation(), NotificationConstants.EMAIL_FROM_KEY, null));
			messageProps.put("to", notification.getUserEmail());
			messageProps.put("subject", notification.getTitle());
			
			MuleMessage received = getMuleClient().send("vm://sendEmail.in", notification.getNote(), messageProps);
			
			exception = received.getInboundProperty("sendEmailException");
		} 
		catch (MuleException e) 
		{
			exception = e;
		}
		
		if (notification != null)
		{
			if (exception == null)
			{
				notification.setState(NotificationConstants.STATE_SENT);
			}
			else
			{
				LOG.error("Notification message not sent ...", exception);
				notification.setState(NotificationConstants.STATE_NOT_SENT);
			}
		}

		return notification;
	}

	public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter() {
		return auditPropertyEntityAdapter;
	}

	public void setAuditPropertyEntityAdapter(
			AuditPropertyEntityAdapter auditPropertyEntityAdapter) {
		this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
	}

	public PropertyFileManager getPropertyFileManager() {
		return propertyFileManager;
	}

	public void setPropertyFileManager(PropertyFileManager propertyFileManager) {
		this.propertyFileManager = propertyFileManager;
	}

	public String getNotificationPropertyFileLocation() {
		return notificationPropertyFileLocation;
	}

	public void setNotificationPropertyFileLocation(
			String notificationPropertyFileLocation) {
		this.notificationPropertyFileLocation = notificationPropertyFileLocation;
	}

	public MuleClient getMuleClient() {
		return muleClient;
	}

	public void setMuleClient(MuleClient muleClient) {
		this.muleClient = muleClient;
	}

}
