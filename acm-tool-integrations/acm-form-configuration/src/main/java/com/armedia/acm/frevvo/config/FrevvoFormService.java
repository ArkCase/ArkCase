
/**
 * 
 */
package com.armedia.acm.frevvo.config;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.users.dao.ldap.UserDao;

/**
 * @author riste.tutureski
 *
 */
public interface FrevvoFormService {

	public Object init();
	public Object get(String action);
	public boolean save(String xml, MultiValueMap<String, MultipartFile> attachemnts) throws Exception;
	public void setProperties(Map<String, Object> properties);
	public void setRequest(HttpServletRequest request);
	public void setAuthentication(Authentication authentication);
	public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService);
	public void setUserDao(UserDao userDao);
	
}
