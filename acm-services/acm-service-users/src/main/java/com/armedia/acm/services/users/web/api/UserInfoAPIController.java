package com.armedia.acm.services.users.web.api;

import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserInfoDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Logger log = LoggerFactory.getLogger(getClass());
    private UserDao userDao;

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
        retval.setNotificationMessage(notificationMessage);
        retval.setLangCode(user.getLang());
        return retval;
    }

    @RequestMapping(value = "/lang/{lang}", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String updateLang(@PathVariable("lang") String lang, Authentication authentication)
    {
        // TODO validate lang supported
        log.debug("Setting '{}' language for user [{}]", lang, authentication.getName());

        AcmUser acmUser = userDao.findByUserId(authentication.getName());
        acmUser.setLang(lang);
        userDao.save(acmUser);

        return lang;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}
