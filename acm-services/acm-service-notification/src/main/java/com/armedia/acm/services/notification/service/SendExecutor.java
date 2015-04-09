/**
 * 
 */
package com.armedia.acm.services.notification.service;

import java.util.Map;

import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.spring.SpringContextHolder;

/**
 * @author riste.tutureski
 *
 */
public class SendExecutor implements Executor {

	private SpringContextHolder springContextHolder;

	@Override
	public Notification execute(Notification notification) 
	{
		// Get all registered senders
		Map<String, NotificationSender> senders = getSpringContextHolder().getAllBeansOfType(NotificationSender.class);
		
		if (senders != null)
		{
			for (NotificationSender sender : senders.values())
			{
				// Send notification
				notification = sender.send(notification);
			}
		}
				
		return notification;
	}

	public SpringContextHolder getSpringContextHolder() {
		return springContextHolder;
	}

	public void setSpringContextHolder(SpringContextHolder springContextHolder) {
		this.springContextHolder = springContextHolder;
	}
}
