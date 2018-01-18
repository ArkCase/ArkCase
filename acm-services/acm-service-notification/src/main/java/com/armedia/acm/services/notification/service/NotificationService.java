package com.armedia.acm.services.notification.service;

import com.armedia.acm.services.notification.model.NotificationRule;

import java.util.Date;

public interface NotificationService
{

    void run();

    void runRule(Date lastRun, NotificationRule rule);

}
