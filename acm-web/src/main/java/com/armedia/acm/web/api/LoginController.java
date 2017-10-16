package com.armedia.acm.web.api;

import com.armedia.acm.web.api.service.ApplicationMetaInfoService;
import com.armedia.acm.web.api.service.LoginWarningMessageService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by dragan.simonovski on 05/11/2016.
 */
@Controller
public class LoginController
{
    LoginWarningMessageService loginWarningMessageService;
    ApplicationMetaInfoService applicationMetaInfoService;

    @RequestMapping(value = {"/login", "/login.html"}, method = RequestMethod.GET)
    public String getLogin(Model model)
    {

        model.addAttribute("warningEnabled", loginWarningMessageService.isEnabled());
        model.addAttribute("warningMessage", loginWarningMessageService.getMessage());
        model.addAttribute("version", applicationMetaInfoService.getVersion());

        return "login";
    }

    /**
     * Retrieve login warning configuration
     *
     * @return login warning configuration
     */
    @RequestMapping(value = "/warning", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getWarning()
    {
        return loginWarningMessageService.getWarning();
    }

    public void setLoginWarningMessageService(LoginWarningMessageService loginWarningMessageService)
    {
        this.loginWarningMessageService = loginWarningMessageService;
    }

    public void setApplicationMetaInfoService(ApplicationMetaInfoService applicationMetaInfoService)
    {
        this.applicationMetaInfoService = applicationMetaInfoService;
    }
}
