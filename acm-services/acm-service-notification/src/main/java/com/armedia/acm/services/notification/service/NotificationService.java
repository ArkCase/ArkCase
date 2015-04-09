/**
 * 
 */
package com.armedia.acm.services.notification.service;

import java.util.Date;

import com.armedia.acm.services.notification.model.NotificationRule;

/**
 * @author riste.tutureski
 *
 */
public interface NotificationService {

	public void run();
	public void runRule(Date lastRun, NotificationRule rule);
	
}
