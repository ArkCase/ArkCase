package com.armedia.acm.services.users.web.api;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.core.AcmSpringActiveProfile;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserInfoDTO;
import com.armedia.acm.services.users.service.AcmUserService;
import com.google.json.JsonSanitizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = {
        "/api/v1/users",
        "/api/latest/users" })
public class UserInfoAPIController
{
    private Logger log = LogManager.getLogger(getClass());
    private UserDao userDao;
    private AcmUserService acmUserService;
    private AcmSpringActiveProfile acmSpringActiveProfile;

    @RequestMapping(method = RequestMethod.GET, value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AcmUserInfoDTO info(Authentication auth, HttpSession session)
    {
        log.debug("Getting info for user {}", auth.getName());

        List<String> authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        AcmUser user = (AcmUser) session.getAttribute("acm_user");

        Map<String, Boolean> privilegeMap = (Map<String, Boolean>) session.getAttribute("acm_privileges");
        Set<String> privileges = privilegeMap.keySet();

        String notificationMessage = (String) session.getAttribute("acm_user_message");

        AcmUserInfoDTO retval = new AcmUserInfoDTO();
        retval.setUserId(auth.getName());
        retval.setFullName(user.getFullName());
        retval.setAuthorities(authorities);
        retval.setPrivileges(privileges);
        retval.setFirstName(user.getFirstName());
        retval.setLastName(user.getLastName());
        retval.setMail(user.getMail());
        retval.setDirectoryName(user.getUserDirectoryName());
        retval.setCountry(user.getCountry());
        retval.setCountryAbbreviation(user.getCountryAbbreviation());
        retval.setDepartment(user.getDepartment());
        retval.setCompany(user.getCompany());
        retval.setTitle(user.getTitle());
        if (getAcmSpringActiveProfile() != null && !getAcmSpringActiveProfile().isSSOEnabledEnvironment())
        {
            retval.setNotificationMessage(notificationMessage);
        }
        retval.setLangCode(user.getLang());
        return retval;
    }

    @RequestMapping(value = "/lang/{lang}", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String updateLang(@PathVariable("lang") String lang, Authentication authentication, HttpSession session)
    {
        // TODO validate lang supported
        log.debug("Setting '{}' language for user [{}]", lang, authentication.getName());

        AcmUser acmUser = userDao.findByUserId(authentication.getName());
        acmUser.setLang(lang);
        userDao.save(acmUser);

        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        user.setLang(lang);
        session.setAttribute("acm_user", user);

        String wellFormedJson = JsonSanitizer.sanitize(lang);
        return wellFormedJson;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/userPrivileges", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Set<String> getUserPrivileges(Authentication authentication)
    {

        return getAcmUserService().getUserPrivileges(authentication.getName());
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public AcmUserService getAcmUserService()
    {
        return acmUserService;
    }

    public void setAcmUserService(AcmUserService acmUserService)
    {
        this.acmUserService = acmUserService;
    }

    public AcmSpringActiveProfile getAcmSpringActiveProfile()
    {
        return acmSpringActiveProfile;
    }

    public void setAcmSpringActiveProfile(AcmSpringActiveProfile acmSpringActiveProfile)
    {
        this.acmSpringActiveProfile = acmSpringActiveProfile;
    }
}
