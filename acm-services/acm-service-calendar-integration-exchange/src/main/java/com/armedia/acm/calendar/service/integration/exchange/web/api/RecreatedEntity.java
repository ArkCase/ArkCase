package com.armedia.acm.calendar.service.integration.exchange.web.api;

/*-
 * #%L
 * ACM Service: Exchange Integration Calendar Service
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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 31, 2017
 *
 */

@JsonIdentityInfo(generator = JSOGGenerator.class)
public class RecreatedEntity
{

    private String objectType;

    private Long objectId;

    /**
     * @return the objectType
     */
    public String getObjectType()
    {
        return objectType;
    }

    /**
     * @param objectType
     *            the objectType to set
     */
    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    /**
     * @return the objectId
     */
    public Long getObjectId()
    {
        return objectId;
    }

    /**
     * @param objectId
     *            the objectId to set
     */
    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

}
