/**
 * 
 */
package com.armedia.acm.services.notification.model;

/**
 * @author riste.tutureski
 *
 */
public class AssignmentRule implements NotificationRule {

	private boolean globalRule;
	private String jpaQuery;
	
	@Override
	public boolean isGlobalRule() {
		return globalRule;
	}
	
	public void setGlobalRule(boolean globalRule) {
		this.globalRule = globalRule;
	}
	
	@Override
	public String getJpaQuery() {
		return jpaQuery;
	}
	
	public void setJpaQuery(String jpaQuery) {
		this.jpaQuery = jpaQuery;
	}
	
	
	
}
