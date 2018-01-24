/**
 * 
 */
package com.armedia.acm.services.notification.service;

import com.armedia.acm.services.notification.model.Notification;

/**
 * @author riste.tutureski
 *
 */
public interface Executor
{

    Notification execute(Notification notification);

}
