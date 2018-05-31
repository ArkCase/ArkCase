package com.armedia.acm.frevvo.config;

/*-
 * #%L
 * ACM Service: Form Configuration
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.users.dao.UserActionDao;
import com.armedia.acm.services.users.dao.UserDao;

import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;

public interface FrevvoFormService
{

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

    default void setFormName(String formName)
    {

    }

    Class<?> getFormClass();

    String getUserIpAddress();

    void setUserIpAddress(String userIpAddress);

    JSONObject createResponse(Object object);

    void updateXML(AcmContainerEntity entity, Authentication auth, Class<?> c);

    String findCmisFolderId(Long folderId, AcmContainer container, String objectType, Long objectId);

}
