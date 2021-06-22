package com.armedia.acm.auth.oidc;

/*-
 * #%L
 * ACM Service: User Login and Authentication
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.auth.AcmAuthenticationMapper;
import com.armedia.acm.auth.AcmLoginSuccessHandler;
import com.armedia.acm.auth.AcmLoginSuccessOperations;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.OAuth2ClientRegistrationConfig;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.spring.SpringContextHolder;
import com.armedia.acm.web.model.LoginConfig;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@Component
@Profile(value = {"oidc", "externalOidc"})
public class AcmOAuth2LoginSuccessHandler extends AcmLoginSuccessHandler
{
    private final AcmAuthenticationMapper acmAuthenticationMapper;
    private final UserDao userDao;
    private final OAuth2ClientRegistrationConfig oAuth2ClientRegistrationConfig;
    private final SpringContextHolder springContextHolder;

    private static final Logger logger = LogManager.getLogger(AcmOAuth2LoginSuccessHandler.class);

    public AcmOAuth2LoginSuccessHandler(AcmAuthenticationMapper acmAuthenticationMapper,
            AcmLoginSuccessOperations loginSuccessOperations,
            SessionRegistry sessionRegistry,
            UserDao userDao,
            OAuth2ClientRegistrationConfig oAuth2ClientRegistrationConfig,
            @Qualifier("concurrentSessionControlAuthenticationStrategy") SessionAuthenticationStrategy sessionAuthenticationStrategy,
            LoginConfig loginConfig,
            SpringContextHolder springContextHolder)
    {
        this.acmAuthenticationMapper = acmAuthenticationMapper;
        this.userDao = userDao;
        this.oAuth2ClientRegistrationConfig = oAuth2ClientRegistrationConfig;
        this.springContextHolder = springContextHolder;

        setLoginSuccessOperations(loginSuccessOperations);
        setSessionRegistry(sessionRegistry);
        setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        setLoginConfig(loginConfig);
        setDefaultTargetUrl(loginConfig.getDefaultTargetUrl());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException
    {
        OAuth2AuthenticationToken oAuth2LoginAuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oAuth2LoginAuthenticationToken.getPrincipal();

        String username = (String) oAuth2User.getAttributes().get(oAuth2ClientRegistrationConfig.getUsernameAttribute());
        if (username == null)
        {
            throw new UsernameNotFoundException(String.format("Attribute %s is not part of the OAuth2 token payload",
                    oAuth2ClientRegistrationConfig.getUsernameAttribute()));
        }
        logger.debug("User [{}] has successfully authenticated", username);

        AcmLdapSyncConfig ldapSyncConfig = springContextHolder.getBeanByName(String.format("%s_sync",
                oAuth2ClientRegistrationConfig.getUsersDirectory()), AcmLdapSyncConfig.class);

        String acmPrincipal = MapperUtils.buildPrincipalName(StringUtils.substringBeforeLast(username, "@"),
                ldapSyncConfig.getUserPrefix(), ldapSyncConfig.getUserDomain());

        if (!acmPrincipal.equals(username))
        {
            logger.info("Authenticated principal with configured prefix [{}] and domain [{}] is [{}]", ldapSyncConfig.getUserPrefix(),
                    ldapSyncConfig.getUserDomain(), acmPrincipal);
        }

        AcmUser acmUser = userDao.findByUserId(acmPrincipal);
        if (acmUser == null)
        {
            throw new UsernameNotFoundException(String.format("User with email address %s not found in system", username));
        }

        Set<AcmGroup> acmUserAuthorities = acmUser.getLdapGroups();
        String[] authoritiesNames = acmUserAuthorities.stream()
                .map(AcmGroup::getName)
                .toArray(String[]::new);
        AcmOAuth2User acmOAuth2User = new AcmOAuth2User(oAuth2User, acmUser.getUserId(), authoritiesNames);
        OAuth2AuthenticationToken updatedToken = new OAuth2AuthenticationToken(acmOAuth2User, acmOAuth2User.getAuthorities(),
                oAuth2LoginAuthenticationToken.getAuthorizedClientRegistrationId());

        AcmAuthentication acmAuthentication = acmAuthenticationMapper.getAcmAuthentication(updatedToken);
        SecurityContextHolder.getContext().setAuthentication(acmAuthentication);

        super.onAuthenticationSuccess(request, response, acmAuthentication);
    }
}
