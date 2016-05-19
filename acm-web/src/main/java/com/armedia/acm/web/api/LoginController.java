package com.armedia.acm.web.api;

import com.armedia.acm.web.api.service.LoginWarningMessageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by dragan.simonovski on 05/11/2016.
 */
@Controller
public class LoginController
{
    LoginWarningMessageService loginWarningMessageService;

    @RequestMapping(value = {"/login", "/login.html"}, method = RequestMethod.GET)
    public String getLogin(Model model)
    {

        model.addAttribute("warningEnabled", loginWarningMessageService.isEnabled());
        model.addAttribute("warningMessage", loginWarningMessageService.getMessage());

        return "login";
    }

    public void setLoginWarningMessageService(LoginWarningMessageService loginWarningMessageService)
    {
        this.loginWarningMessageService = loginWarningMessageService;
    }
}
