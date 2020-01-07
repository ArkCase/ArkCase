package com.armedia.acm.services.dataaccess.web.api;

/*-
 * #%L
 * ACM Service: Data Access Control
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.services.dataaccess.model.AccessControlRules;
import com.armedia.acm.services.dataaccess.service.AccessControlRuleChecker;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
@RequestMapping({ "/api/v1/service/dataaccess/rules", "/api/latest/service/dataaccess/rules" })
public class AccessControlRulesAPIController
{
    /**
     * Access Control Service reference.
     */
    AccessControlRuleChecker accessControlRuleChecker;

    /**
     * Logger instance.
     */
    private Logger log = LogManager.getLogger(getClass());

    /**
     * Retrieve AC rules configuration
     *
     * @param authentication
     *            authentication token
     * @return AC rules configuration
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
