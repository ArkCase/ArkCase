/**
 * 
 */
package com.armedia.acm.services.notification.model;

/**
 * @author riste.tutureski
 *
 */
public interface NotificationRule {

	public boolean isGlobalRule();
	public String getJpaQuery();
	
}
