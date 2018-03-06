package com.armedia.acm.auth.okta.web.api;

import com.armedia.acm.auth.okta.model.AuthProfile;
import com.armedia.acm.auth.okta.model.OktaAPIConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by joseph.mcgrady on 3/6/2018.
 */
@Controller
@RequestMapping({"/api/v1/plugin/okta/authprofile", "/api/latest/plugin/okta/authprofile"})
public class GetAuthProfileAPIController
{
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AuthProfile retrieveAuthProfile()
    {
        String profileName = System.getProperty(OktaAPIConstants.SPRING_PROFILES_ACTIVE);
        if (StringUtils.isEmpty(profileName))
        {
            profileName = OktaAPIConstants.AUTH_DEFAULT_PROFILE;
        }

        AuthProfile authProfile = new AuthProfile();
        authProfile.setName(profileName);
        return authProfile;
    }
}