/**
 * 
 */
package com.armedia.acm.services.notification.service;

import java.util.Date;

import com.armedia.acm.services.notification.model.AssignmentRule;
import com.armedia.acm.services.notification.model.Notification;

/**
 * @author riste.tutureski
 *
 */
public interface NotificationService {

	public void run();
	public void runRule(Date lastRun, AssignmentRule rule);
	public Notification send(Notification notification);
	
}
