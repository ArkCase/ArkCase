package com.armedia.acm.services.costsheet.pipeline;

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

import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import org.springframework.security.core.Authentication;

/**
 * Store all the case file saving-related references in this context.
 * Created by Petar Ilin <marjan.trifunov@armedia.com> on 27.02.2018.
 */
public class CostsheetPipelineContext extends AbstractPipelineContext
{
    /**
     * Spring authentication token.
     */
    private Authentication authentication;

    /**
     * Flag showing whether new costsheet is created.
     */
    private boolean newCostsheet;

    /**
     * IP Address.
     */
    private String ipAddress;

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public boolean isNewCostsheet()
    {
        return newCostsheet;
    }

    public void setNewCostsheet(boolean newCostsheet)
    {
        this.newCostsheet = newCostsheet;
    }
}
