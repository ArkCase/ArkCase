/**
 * 
 */
package com.armedia.acm.services.notification.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;

/**
 * @author riste.tutureski
 *
 */
public class PurgeExecutor implements Executor {

	private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
	
	@Override
	public Notification execute(Notification notification) 
	{
		getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);
		
		if (notification != null)
		{
			notification.setStatus(NotificationConstants.STATUS_DELETE);
		}
		
		return notification;
	}

	public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter() {
		return auditPropertyEntityAdapter;
	}

	public void setAuditPropertyEntityAdapter(
			AuditPropertyEntityAdapter auditPropertyEntityAdapter) {
		this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
	}

}
