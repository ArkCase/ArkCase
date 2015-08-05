/**
 * 
 */
package com.armedia.acm.services.notification.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.notification.model.EmailNotificationDto;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

/**
 * @author riste.tutureski
 *
 */
public class EmailNotificationSender implements NotificationSender {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private PropertyFileManager propertyFileManager;
    private String notificationPropertyFileLocation;
    private MuleContextManager muleContextManager;
	private AuthenticationTokenService authenticationTokenService;


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

			MuleMessage received = getMuleContextManager().send("vm://sendEmail.in", notification.getNote(), messageProps);
			
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

	public String makeNote(EmailNotificationDto emailNotificationDto, Authentication authentication){
		String note="";
		String token = generateAuthenticationToken(authentication);
		note += emailNotificationDto.getHeader();

		for(String url: emailNotificationDto.getUrls()){
			note= url+ "?acm_ticket=" + token + "\n";
		}
		note+= emailNotificationDto.getFooter();
		return note;
	}


	public String generateAuthenticationToken(Authentication authentication){
		return getAuthenticationTokenService().getTokenForAuthentication(authentication);
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

	public MuleContextManager getMuleContextManager()
	{
		return muleContextManager;
	}

	public void setMuleContextManager(MuleContextManager muleContextManager)
	{
		this.muleContextManager = muleContextManager;
	}

	public AuthenticationTokenService getAuthenticationTokenService() {
		return authenticationTokenService;
	}

	public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService) {
		this.authenticationTokenService = authenticationTokenService;
	}
}
