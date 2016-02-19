/**
 * 
 */
package com.armedia.acm.services.notification.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.armedia.acm.services.notification.model.EmailNotificationDto;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;

/**
 * @author riste.tutureski
 *
 */
public interface NotificationSender
{

    /**
     * Sends the notification to user's email. If successful, sets the notification state to {@link NotificationConstants#STATE_SENT},
     * otherwise it sets it to {@link NotificationConstants#STATE_NOT_SENT}
     * 
     * @param notification
     *            the notification to send
     * @return the notification with state set
     */
    public Notification send(Notification notification);

    public List<Notification> sendEmailNotificationWithLinks(List<EmailNotificationDto> in, Authentication authentication);

}
