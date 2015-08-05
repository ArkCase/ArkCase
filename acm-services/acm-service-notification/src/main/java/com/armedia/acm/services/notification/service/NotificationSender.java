/**
 * 
 */
package com.armedia.acm.services.notification.service;

import com.armedia.acm.services.notification.model.EmailNotificationDto;
import com.armedia.acm.services.notification.model.Notification;
import org.springframework.security.core.Authentication;

/**
 * @author riste.tutureski
 *
 */
public interface NotificationSender {

	public Notification send(Notification notification);
	public String makeNote(EmailNotificationDto emailNotificationDto, Authentication authentication);
	
}
