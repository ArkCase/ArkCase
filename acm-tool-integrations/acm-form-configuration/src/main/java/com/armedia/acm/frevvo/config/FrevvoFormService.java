
/**
 * 
 */
package com.armedia.acm.frevvo.config;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.users.dao.ldap.UserActionDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;

/**
 * @author riste.tutureski
 *
 */
public interface FrevvoFormService {

	public Object init();
	public Object get(String action);
	public boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception;
	
	public Map<String, Object> getProperties();
	public void setProperties(Map<String, Object> properties);
	
	public HttpServletRequest getRequest();
	public void setRequest(HttpServletRequest request);
	
	public Authentication getAuthentication();
	public void setAuthentication(Authentication authentication);
	
	public AuthenticationTokenService getAuthenticationTokenService();
	public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService);
	
	public UserDao getUserDao();
	public void setUserDao(UserDao userDao);

	public UserActionDao getUserActionDao();
	public void setUserActionDao(UserActionDao userActionDao);
	
	public EcmFileService getEcmFileService();
	public void setEcmFileService(EcmFileService ecmFileService);
	
	public String getServletContextPath();
    public void setServletContextPath(String servletContextPath);

    String getFormName();

	void setUserIpAddress(String userIpAddress);
	String getUserIpAddress();
	
	public JSONObject createResponse(Object object);
	
}
