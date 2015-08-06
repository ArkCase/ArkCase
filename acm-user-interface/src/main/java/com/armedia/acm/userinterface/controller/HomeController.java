package com.armedia.acm.userinterface.controller;

import com.armedia.acm.userinterface.connector.ArkCaseAuthenticator;
import com.armedia.acm.userinterface.model.UserInterfaceConstants;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.ConnectorSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by riste.tutureski on 7/30/2015.
 */
@Controller
public class HomeController {

    private ConnectorService connectorService;
    private ArkCaseAuthenticator arkCaseAuthenticator;

    @RequestMapping(value = "/")
    public String home() {
        return "redirect:home";
    }

    @RequestMapping(value="/home")
    public String home(Model model, Authentication auth) {

        model.addAttribute("ticket", ((ConnectorSession) auth.getPrincipal()).getParameter(UserInterfaceConstants.ACM_TICKET));
        return "home";
    }

    @RequestMapping(value="/login")
    public String login(Authentication auth) {

        return "login";
    }

    @RequestMapping(value="/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        return "redirect:home";
    }

    public ConnectorService getConnectorService() {
        return connectorService;
    }

    public void setConnectorService(ConnectorService connectorService) {
        this.connectorService = connectorService;
    }

    public ArkCaseAuthenticator getArkCaseAuthenticator() {
        return arkCaseAuthenticator;
    }

    public void setArkCaseAuthenticator(ArkCaseAuthenticator arkCaseAuthenticator) {
        this.arkCaseAuthenticator = arkCaseAuthenticator;
    }
}
