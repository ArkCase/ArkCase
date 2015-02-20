/**
 * 
 */
package com.armedia.acm.services.notification.service;

import com.armedia.acm.services.notification.model.Notification;

/**
 * @author riste.tutureski
 *
 */
public interface NotificationSender {

	public Notification send(Notification notification);
	
}
