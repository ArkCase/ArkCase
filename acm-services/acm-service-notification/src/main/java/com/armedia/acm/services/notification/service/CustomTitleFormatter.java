package com.armedia.acm.services.notification.service;

import com.armedia.acm.services.notification.model.Notification;

/**
 * @author ncuculova
 *
 */
public interface CustomTitleFormatter
{
	String format(Notification notification);
}
