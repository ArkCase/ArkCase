/**
 * 
 */
package com.armedia.acm.frevvo.config;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
	public static final String TIMEZONE = "frevvo.timezone";
	public static final String LOCALE = "frevvo.locale";

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
		String timezone = (String) properties.get(TIMEZONE);
		String locale = getLocale();
		
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
		
		if (timezone != null) {
			uri = uri.replace("{frevvo_timezone}", timezone);
		}
		
		if (locale != null) {
			uri = uri.replace("{frevvo_locale}", locale);
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
	
	private String getLocale()
	{
		String retval = "";
		
		String defaultLocale = (String) properties.get(LOCALE);
		String overwriteLocale = (String) properties.get(LOCALE + ".overwrite");
		
		if (overwriteLocale != null && "true".equalsIgnoreCase(overwriteLocale))
		{
			HttpServletRequest request = getCurrentRequest();
			
			if (request != null)
			{
				Locale locale = request.getLocale();
				if (locale != null && !"".equals((locale.toString())))
				{
					String language = locale.getLanguage() == null ? "" : locale.getLanguage();
					String country = locale.getCountry() == null ? "" : locale.getCountry();
					
					retval = language + "_" + country;		
				}
			}
		}

		if ("".equals(retval) && defaultLocale != null)
		{
			retval = defaultLocale;
		}
		
		return retval;
	}
	
	private HttpServletRequest getCurrentRequest()
	{
		HttpServletRequest request = null;
		
		RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
		
		if (attrs instanceof ServletRequestAttributes)
		{
			request = ((ServletRequestAttributes) attrs).getRequest();
		}
		
		return request;
	}

}
