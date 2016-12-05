package com.armedia.acm.frevvo.config;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.users.dao.ldap.UserActionDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;

public interface FrevvoFormService {

	Object init();
	Object get(String action);

	@Transactional
	boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception;
	
	Map<String, Object> getProperties();
	void setProperties(Map<String, Object> properties);
	
	HttpServletRequest getRequest();
	void setRequest(HttpServletRequest request);
	

	Authentication getAuthentication();
	void setAuthentication(Authentication authentication);
	
	AuthenticationTokenService getAuthenticationTokenService();
	void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService);
	
	UserDao getUserDao();
	void setUserDao(UserDao userDao);

	UserActionDao getUserActionDao();
	void setUserActionDao(UserActionDao userActionDao);
	
	EcmFileService getEcmFileService();
	void setEcmFileService(EcmFileService ecmFileService);
	
	String getServletContextPath();
    void setServletContextPath(String servletContextPath);

    String getFormName();
	default void setFormName(String formName) {

	}
	Class<?> getFormClass();

	String getUserIpAddress();

	void setUserIpAddress(String userIpAddress);
	
	JSONObject createResponse(Object object);

	void updateXML(AcmContainerEntity entity, Authentication auth, Class<?> c);

	String findCmisFolderId(Long folderId, AcmContainer container, String objectType, Long objectId);

}
