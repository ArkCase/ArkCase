package com.armedia.acm.plugins.casefile.web.api;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.GetCaseByNumberService;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

/**
 * @author sasko.tanaskoski
 *
 */

@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class GetCaseByNumberAPIController
{

    private final Logger log = LogManager.getLogger(getClass());

    private GetCaseByNumberService getCaseByNumberService;

    private ArkPermissionEvaluator arkPermissionEvaluator;

    @RequestMapping(value = "/bynumber", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CaseFile getCaseFileByNumber(@RequestParam(value = "caseNumber", required = true) String caseNumber, Authentication auth)
            throws AcmAccessControlException
    {
        CaseFile caseFile = getCaseByNumberService.getCaseByNumber(caseNumber);
        if (caseFile != null && !getArkPermissionEvaluator().hasPermission(auth, caseFile.getId(), "CASE_FILE", "read"))
        {
            throw new AcmAccessControlException(Arrays.asList(""),
                    "The user {" + auth.getName() + "} is not allowed to read case file with id=" + caseFile.getId());
        }
        return caseFile;
    }

    /**
     * @return the getCaseByNumberService
     */
    public GetCaseByNumberService getGetCaseByNumberService()
    {
        return getCaseByNumberService;
    }

    /**
     * @param getCaseByNumberService
     *            the getCaseByNumberService to set
     */
    public void setGetCaseByNumberService(GetCaseByNumberService getCaseByNumberService)
    {
        this.getCaseByNumberService = getCaseByNumberService;
    }

    /**
     * @return the arkPermissionEvaluator
     */
    public ArkPermissionEvaluator getArkPermissionEvaluator()
    {
        return arkPermissionEvaluator;
    }

    /**
     * @param arkPermissionEvaluator
     *            the arkPermissionEvaluator to set
     */
    public void setArkPermissionEvaluator(ArkPermissionEvaluator arkPermissionEvaluator)
    {
        this.arkPermissionEvaluator = arkPermissionEvaluator;
    }

}
