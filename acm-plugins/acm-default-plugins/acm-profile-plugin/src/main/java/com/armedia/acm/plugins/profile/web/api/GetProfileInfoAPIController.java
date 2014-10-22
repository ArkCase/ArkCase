package com.armedia.acm.plugins.profile.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.profile.exception.AcmProfileException;
import com.armedia.acm.plugins.profile.model.ProfileDTO;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */

@Controller
@RequestMapping({"/api/v1/plugin/profile", "/api/latest/plugin/profile"})
public class GetProfileInfoAPIController {
    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProfileDTO getProfileInfo(
            Authentication authentication,
            HttpSession session
    ) throws AcmProfileException, AcmObjectNotFoundException {
        return null;
    }
}
