/**
 * 
 */
package com.armedia.acm.services.notification.service;

import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.spring.SpringContextHolder;

import java.util.Map;

/**
 * @author riste.tutureski
 *
 */
public class SendExecutor implements Executor
{

    private SpringContextHolder springContextHolder;

    @Override
    public Notification execute(Notification notification)
    {

        // Get all registered senders
        Map<String, NotificationSenderFactory> senderFactoryList = getSpringContextHolder()
                .getAllBeansOfType(NotificationSenderFactory.class);

        if (senderFactoryList != null)
        {
            for (NotificationSenderFactory senderFactory : senderFactoryList.values())
            {
                // Send notification
                notification = senderFactory.getNotificationSender().send(notification);
            }
        }

        return notification;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

}
