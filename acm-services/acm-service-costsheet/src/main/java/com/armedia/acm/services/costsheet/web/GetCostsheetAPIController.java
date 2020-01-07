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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
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
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/costsheet", "/api/latest/service/costsheet" })
public class GetCostsheetAPIController
{

    private Logger LOG = LogManager.getLogger(getClass());
    private CostsheetService costsheetService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmCostsheet getCostsheet(@PathVariable("id") Long id,
            Authentication auth) throws AcmObjectNotFoundException
    {
        if (LOG.isInfoEnabled())
        {
            LOG.info("Taking costsheet id=" + id);
        }

        AcmCostsheet costsheet = getCostsheetService().get(id);

        if (costsheet == null)
        {
            throw new AcmObjectNotFoundException(CostsheetConstants.OBJECT_TYPE, id, "Could not retrieve Costsheet.", new Throwable());
        }

        return costsheet;
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
