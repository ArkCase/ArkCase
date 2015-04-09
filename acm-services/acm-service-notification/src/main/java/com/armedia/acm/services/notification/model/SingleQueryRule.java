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
public class SingleQueryRule implements NotificationRule {

	private boolean globalRule;
	private boolean create;
	private Executor executor;
	private Map<String, Object> jpaProperties;
	private String jpaQuery;
	
	@Override
	public boolean isGlobalRule() 
	{
		return globalRule;
	}

	public void setGlobalRule(boolean globalRule) {
		this.globalRule = globalRule;
	}

	@Override	
	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	@Override
	public Executor getExecutor() 
	{
		return executor;
	}
		
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	@Override
	public Map<String, Object> getJpaProperties() {
		return jpaProperties;
	}

	public void setJpaProperties(Map<String, Object> jpaProperties) {
		this.jpaProperties = jpaProperties;
	}

	@Override
	public String getJpaQuery() 
	{
		return jpaQuery;
	}

	public void setJpaQuery(String jpaQuery) {
		this.jpaQuery = jpaQuery;
	}

}
