/**
 * 
 */
package com.armedia.acm.services.notification.model;

/**
 * @author riste.tutureski
 *
 */
public class AssignmentRule {

	private boolean globalRule;
	private String jpaQuery;
	
	public boolean isGlobalRule() {
		return globalRule;
	}
	
	public void setGlobalRule(boolean globalRule) {
		this.globalRule = globalRule;
	}
	
	public String getJpaQuery() {
		return jpaQuery;
	}
	
	public void setJpaQuery(String jpaQuery) {
		this.jpaQuery = jpaQuery;
	}
	
	
	
}
