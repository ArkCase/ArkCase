package com.armedia.acm.services.users.service.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;

import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.model.ldap.AcmLdapAuthenticateConfig;

/**
 * Authenticates a user id and password against LDAP directory.  To support multiple LDAP configurations, create multiple Spring 
 * beans, each with its own LdapAuthenticateService.
 */
public class LdapAuthenticateService {
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private SpringLdapDao ldapDao;
	private AcmLdapAuthenticateConfig ldapAuthenticateConfig;
	
	/*
	 * Authenticates user against LDAP
	 */
	public Boolean authenticate(String userName, String password) {
		boolean debug = log.isDebugEnabled();

        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapAuthenticateConfig());
        
        String userIdAttributeName = getLdapAuthenticateConfig().getUserIdAttributeName();
        String searchBase = getLdapAuthenticateConfig().getSearchBase();
                
        String filter = "(" + userIdAttributeName + "=" + userName + ")";
        boolean authenticated = template.authenticate(searchBase, filter, password);
        
		if (debug) {
			log.debug("searchBase[" + searchBase + "], filter[" + filter + "], authenticated[" + authenticated + "]");
		}
        
        return authenticated;
	}
		
	public SpringLdapDao getLdapDao() {
		return ldapDao;
	}
	public void setLdapDao(SpringLdapDao ldapDao) {
		this.ldapDao = ldapDao;
	}

	public AcmLdapAuthenticateConfig getLdapAuthenticateConfig() {
		return ldapAuthenticateConfig;
	}

	public void setLdapAuthenticateConfig(
			AcmLdapAuthenticateConfig ldapAuthenticateConfig) {
		this.ldapAuthenticateConfig = ldapAuthenticateConfig;
	}
	
	
}
