package com.armedia.acm.plugins.complaint.web.api;

/*-
 * #%L
 * ACM Default Plugin: Complaint
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

import com.armedia.acm.data.exceptions.AcmAccessControlException;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.GetComplaintByNumberService;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

/**
 * @author ivana.shekerova on 04/23/2019.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/complaint", "/api/latest/plugin/complaint" })
public class FindComplaintByNumberAPIController
{

    private final Logger log = LogManager.getLogger(getClass());

    private GetComplaintByNumberService getComplaintByNumberService;

    private ArkPermissionEvaluator arkPermissionEvaluator;

    @RequestMapping(value = "/bynumber", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Complaint getComplaintByNumber(@RequestParam(value = "complaintNumber", required = true) String complaintNumber,
            Authentication auth)
            throws AcmAccessControlException
    {
        Complaint complaint = getComplaintByNumberService.getComplaintByNumber(complaintNumber);
        if (complaint != null && !getArkPermissionEvaluator().hasPermission(auth, complaint.getId(), "COMPLAINT", "read"))
        {
            throw new AcmAccessControlException(Arrays.asList(""),
                    "The user {" + auth.getName() + "} is not allowed to read complaint with id=" + complaint.getId());
        }
        return complaint;
    }

    public GetComplaintByNumberService getGetComplaintByNumberService()
    {
        return getComplaintByNumberService;
    }

    public void setGetComplaintByNumberService(GetComplaintByNumberService getComplaintByNumberService)
    {
        this.getComplaintByNumberService = getComplaintByNumberService;
    }

    public ArkPermissionEvaluator getArkPermissionEvaluator()
    {
        return arkPermissionEvaluator;
    }

    public void setArkPermissionEvaluator(ArkPermissionEvaluator arkPermissionEvaluator)
    {
        this.arkPermissionEvaluator = arkPermissionEvaluator;
    }
}
