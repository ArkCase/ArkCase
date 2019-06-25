/**
 *
 */
package com.armedia.acm.services.costsheet.web;

/*-
 * #%L
 * ACM Service: Costsheet
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

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.costsheet.service.CostsheetService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author riste.tutureski
 */
@Controller
@RequestMapping({ "/api/v1/service/costsheet", "/api/latest/service/costsheet" })
public class GetCostsheetsForUserAPIController
{

    private Logger LOG = LogManager.getLogger(getClass());
    private CostsheetService costsheetService;

    @RequestMapping(value = "/user/{userId:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getCostsheetsForUser(@PathVariable("userId") String userId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            @RequestParam(value = "searchQuery", required = false, defaultValue = "*") String searchQuery,
            Authentication auth)
            throws AcmListObjectsFailedException
    {
        if (LOG.isInfoEnabled())
        {
            LOG.info("Querying all costsheets for user=" + userId);
        }

        String jsonResponse = getCostsheetService().getObjectsFromSolr(CostsheetConstants.OBJECT_TYPE, auth, startRow, maxRows, sort,
                searchQuery, userId);

        if (jsonResponse == null)
        {
            throw new AcmListObjectsFailedException(CostsheetConstants.OBJECT_TYPE, "Could not retrieve list of Costsheets.",
                    new Throwable());
        }

        return jsonResponse;
    }

    public CostsheetService getCostsheetService()
    {
        return costsheetService;
    }

    public void setCostsheetService(CostsheetService costsheetService)
    {
        this.costsheetService = costsheetService;
    }
}
