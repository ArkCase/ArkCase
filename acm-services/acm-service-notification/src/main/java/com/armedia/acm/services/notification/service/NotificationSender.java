/**
 * 
 */
package com.armedia.acm.services.notification.service;

import com.armedia.acm.services.notification.model.EmailNotificationDto;
import com.armedia.acm.services.notification.model.Notification;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public interface NotificationSender {

	public Notification send(Notification notification);
	public List<Notification> sendEmailNotificationWithLinks(List<EmailNotificationDto> in, Authentication authentication);
	
}
