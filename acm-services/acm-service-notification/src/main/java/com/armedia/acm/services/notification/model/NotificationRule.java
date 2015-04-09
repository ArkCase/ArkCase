/**
 * 
 */
package com.armedia.acm.services.notification.model;

import java.util.Map;

import com.armedia.acm.services.notification.service.Executor;

/**
 * @author riste.tutureski
 *
 */
public interface NotificationRule {

	public boolean isGlobalRule();
	public boolean isCreate();
	public Executor getExecutor();
	public Map<String, Object> getJpaProperties();
	public String getJpaQuery();
	
}
