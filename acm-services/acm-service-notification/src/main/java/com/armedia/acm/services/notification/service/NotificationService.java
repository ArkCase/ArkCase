package com.armedia.acm.services.notification.service;

import java.util.Date;

import com.armedia.acm.services.notification.model.NotificationRule;


public interface NotificationService {

	void run();
	void runRule(Date lastRun, NotificationRule rule);
	
}
