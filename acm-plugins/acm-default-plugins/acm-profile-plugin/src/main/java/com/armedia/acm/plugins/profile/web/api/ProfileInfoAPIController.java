package com.armedia.acm.plugins.profile.web.api;

import com.armedia.acm.plugins.profile.model.ProfileDTO;
import com.armedia.acm.plugins.profile.service.UserOrgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/api/v1/plugin/profile", "/api/latest/plugin/profile"})
public class ProfileInfoAPIController
{
    private UserOrgService userOrgService;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/get/{userId:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProfileDTO getProfileInfo(@PathVariable("userId") String userId, Authentication authentication)
    {
        log.info("Finding Profile info for user [{}]", userId);
        return userOrgService.getProfileInfo(userId, authentication);
    }

    @RequestMapping(value = "/userOrgInfo/set", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProfileDTO saveUserOrgInfo(@RequestBody ProfileDTO profile, Authentication auth)
    {
        return userOrgService.saveUserOrgInfo(profile, auth);
    }

    public UserOrgService getUserOrgService()
    {
        return userOrgService;
    }

    public void setUserOrgService(UserOrgService userOrgService)
    {
        this.userOrgService = userOrgService;
    }
}
