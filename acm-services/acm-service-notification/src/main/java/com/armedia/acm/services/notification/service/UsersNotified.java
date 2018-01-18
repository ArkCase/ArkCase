/**
 * 
 */
package com.armedia.acm.services.notification.service;

import com.armedia.acm.services.notification.model.Notification;

import java.util.List;

/**
 * @author ncuculova
 *
 */
public interface UsersNotified
{
    List<Notification> getNotifications(Object[] notification, Long parentObjectId, String parentObjectType);
}
