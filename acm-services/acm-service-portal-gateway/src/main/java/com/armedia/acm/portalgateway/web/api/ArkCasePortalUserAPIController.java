package com.armedia.acm.portalgateway.web.api;


import com.armedia.acm.portalgateway.model.PortalUserConfig;
import com.armedia.acm.portalgateway.service.PortalUserConfigurationService;
import com.armedia.acm.services.users.web.api.SecureLdapController;
import com.armedia.acm.spring.SpringContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(value = {"/api/v1/service/portalgateway", "/api/latest/service/portalgateway"})
public class ArkCasePortalUserAPIController {

    private transient final Logger log = LogManager.getLogger(getClass());

    private PortalUserConfigurationService portalUserConfigurationService;

    @RequestMapping(value= "/config", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PortalUserConfig getPortalUserConfiguration(Authentication auth){
        log.debug("User [{}] is getting a portal configuration", auth.getName());
        return getPortalUserConfigurationService().getPortalUserConfiguration();
    }

    public PortalUserConfigurationService getPortalUserConfigurationService()
    {
        return portalUserConfigurationService;
    }

    public void setPortalUserConfigurationService(PortalUserConfigurationService portalUserConfigurationService)
    {
        this.portalUserConfigurationService = portalUserConfigurationService;
    }
}
