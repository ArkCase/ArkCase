/**
 * 
 */
package com.armedia.acm.service.usernotification.service;

import java.util.Date;

import com.armedia.acm.service.usernotification.model.AssignmentRule;

/**
 * @author riste.tutureski
 *
 */
public interface UserNotificationService {

	public void run();
	public void runRule(Date lastRun, AssignmentRule rule);
	
}
