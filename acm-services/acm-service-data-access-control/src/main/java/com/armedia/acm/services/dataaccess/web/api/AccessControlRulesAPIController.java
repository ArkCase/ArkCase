package com.armedia.acm.services.dataaccess.web.api;

import com.armedia.acm.services.dataaccess.model.AccessControlRules;
import com.armedia.acm.services.dataaccess.service.AccessControlRuleChecker;
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
@RequestMapping({"/api/v1/service/dataaccess/rules", "/api/latest/service/dataaccess/rules"})
public class AccessControlRulesAPIController
{
    /**
     * Access Control Service reference.
     */
    AccessControlRuleChecker accessControlRuleChecker;

    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Retrieve AC rules configuration
     *
     * @param authentication authentication token
     * @return AC rules configuration
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AccessControlRules getConfiguration(Authentication authentication)
    {
        log.debug("User [{}] is requesting the AC rules", authentication.getName());
        return accessControlRuleChecker.getAccessControlRules();
    }

    public AccessControlRuleChecker getAccessControlRuleChecker()
    {
        return accessControlRuleChecker;
    }

    public void setAccessControlRuleChecker(AccessControlRuleChecker accessControlRuleChecker)
    {
        this.accessControlRuleChecker = accessControlRuleChecker;
    }
}
