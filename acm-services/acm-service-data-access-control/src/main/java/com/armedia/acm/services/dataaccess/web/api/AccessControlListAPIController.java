package com.armedia.acm.services.dataaccess.web.api;

import com.armedia.acm.services.dataaccess.model.AccessControlList;
import com.armedia.acm.services.dataaccess.service.AccessControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Data Access Control servlet.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 05.11.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/dataaccess/acl", "/api/latest/service/dataaccess/acl"})
public class AccessControlListAPIController
{
    /**
     * Access Control Service reference.
     */
    AccessControlService accessControlService;

    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Retrieve ACL configuraion
     *
     * @param authentication authentication token
     * @return ACL configuration
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AccessControlList getConfiguration(Authentication authentication)
    {
        log.debug("User [{}] is requesting the ACL", authentication.getName());
        return accessControlService.getAccessControlList();
    }

    public AccessControlService getAccessControlService()
    {
        return accessControlService;
    }

    public void setAccessControlService(AccessControlService accessControlService)
    {
        this.accessControlService = accessControlService;
    }
}
