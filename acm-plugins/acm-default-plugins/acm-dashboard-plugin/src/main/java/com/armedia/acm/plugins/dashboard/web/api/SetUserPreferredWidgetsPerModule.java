package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.dashboard.model.userPreference.PreferredWidgetsDto;
import com.armedia.acm.plugins.dashboard.service.UserPreferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by marjan.stefanoski on 14.01.2016.
 */
@Controller
@RequestMapping({"/api/v1/plugin/dashboard/widgets", "/api/latest/plugin/dashboard/widgets"})
public class SetUserPreferredWidgetsPerModule
{

    private UserPreferenceService userPreferenceService;
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/preferred", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public PreferredWidgetsDto setPreferredWidgets(
            @RequestBody PreferredWidgetsDto in,
            Authentication authentication,
            HttpSession session
    ) throws AcmObjectNotFoundException
    {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        String userId = authentication.getName();

        log.info("Setting widgets for user  based on the user preference for user: [{}]", userId);
        return userPreferenceService.updateUserPreferenceWidgets(userId, in, ipAddress);

    }

    public UserPreferenceService getUserPreferenceService()
    {
        return userPreferenceService;
    }

    public void setUserPreferenceService(UserPreferenceService userPreferenceService)
    {
        this.userPreferenceService = userPreferenceService;
    }
}
