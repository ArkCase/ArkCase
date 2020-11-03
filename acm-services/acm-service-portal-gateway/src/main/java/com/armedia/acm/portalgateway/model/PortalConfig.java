package com.armedia.acm.portalgateway.model;

/*-
 * #%L
 * ACM Service: Portal Gateway Service
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by ana.serafimoska
 */
@JsonSerialize(as = PortalConfig.class)
public class PortalConfig
{
    @JsonProperty("portal.authenticatedMode")
    @Value("${portal.authenticatedMode}")
    private Boolean authenticatedMode;

    public Boolean getAuthenticatedMode()
    {
        return authenticatedMode;
    }

    public void setAuthenticatedMode(Boolean authenticatedMode)
    {
        this.authenticatedMode = authenticatedMode;
    }
}
