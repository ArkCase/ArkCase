/**
 * 
 */
package com.armedia.acm.frevvo.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.armedia.acm.form.config.FormUrl;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;

/**
 * @author riste.tutureski
 *
 */
public class FrevvoFormUrl implements FormUrl {

	private Logger LOG = LoggerFactory.getLogger(FrevvoFormUrl.class);
	
	public static final String SERVICE = "frevvo.service.baseUrl";
	public static final String REDIRECT = "frevvo.browser.redirect.baseUrl";
	public static final String HOST = "frevvo.host";
	public static final String PORT = "frevvo.port";
	public static final String URI = "frevvo.uri";

	private Map<String, Object> properties;
	private AuthenticationTokenService authenticationTokenService;
	
	public String getBaseUrl() {
		String url = "";
		
		String host = (String) properties.get(HOST);
		String port = (String) properties.get(PORT);
		
		if (host != null) {
			url = url + host;
		}else {
			LOG.error("Frevvo Host is not defined.");
		}
		
		if (port != null && !"".equals(port)) {
			url = url + ":" + port;
		}else {
			LOG.warn("Frevvo port number is not defined. Maybe is not needed.");
		}
		
		LOG.info("Form Base Url: " + url);
		
		return url;
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	/**
	 * @return the authenticationTokenService
	 */
	public AuthenticationTokenService getAuthenticationTokenService() {
		return authenticationTokenService;
	}

	/**
	 * @param authenticationTokenService the authenticationTokenService to set
	 */
	public void setAuthenticationTokenService(
			AuthenticationTokenService authenticationTokenService) {
		this.authenticationTokenService = authenticationTokenService;
	}

	@Override
	public String getNewFormUrl(String formName) {
		String uri = (String) properties.get(URI);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (uri == null) {
			uri = "";
			LOG.error("Frevvo URI is not defined.");
		}
		
		String tenant = (String) properties.get(formName + ".tenant");
		String user = (String) properties.get(formName + ".user");
		String applicationId = (String) properties.get(formName + ".application.id");
		String type = (String) properties.get(formName + ".type");
		String id = (String) properties.get(formName + ".id");
		String mode = (String) properties.get(formName + ".mode");
		String token = this.authenticationTokenService.getTokenForAuthentication(authentication);
		String service = (String) properties.get(SERVICE);
		String redirect = (String) properties.get(REDIRECT);
		
		if (tenant != null) {
			uri = uri.replace("{tenant}", tenant);			
		}
		
		if (user != null) {
			uri = uri.replace("{user}", user);			
		}
		
		if (applicationId != null) {
			uri = uri.replace("{application}", applicationId);
		}

		if (type != null) {
			uri = uri.replace("{type}", type);
		}

		if (id != null) {
			uri = uri.replace("{id}", id);			
		}
		
		if (mode != null) {
			uri = uri.replace("{mode}", mode);			
		}
		
		if (token != null) {
			uri = uri.replace("{acm_ticket}", token);
		}
		
		if (service != null) {
			uri = uri.replace("{frevvo_service_baseUrl}", service);
		}
		
		if (redirect != null) {
			uri = uri.replace("{frevvo_browser_redirect_baseUrl}", redirect);
		}
		
		String url = getBaseUrl() + uri; 
		
		LOG.info("Form Url: " + url);
		
		return url;
	}

	@Override
	public String getPdfRenditionUrl(String formName, String docId) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public String enableFrevvoFormEngine(String formName) {
        String enableFrevvoFormEngine = (String) properties.get(formName + ".enable.frevvo.form.engine");
        return enableFrevvoFormEngine;
    }
	
	

}
