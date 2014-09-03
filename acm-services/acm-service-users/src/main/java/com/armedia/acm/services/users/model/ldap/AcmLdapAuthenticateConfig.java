package com.armedia.acm.services.users.model.ldap;

public class AcmLdapAuthenticateConfig extends AcmLdapConfig 
{
	private String searchBase;

	public String getSearchBase() {
		return searchBase;
	}

	public void setSearchBase(String searchBase) {
		this.searchBase = searchBase;
	}
	
}
