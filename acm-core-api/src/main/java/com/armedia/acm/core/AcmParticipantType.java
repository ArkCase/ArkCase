package com.armedia.acm.core;

/*-
 * #%L
 * ACM Core API
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

import com.armedia.acm.core.enums.AcmParticipantTypes;

import java.io.Serializable;

/**
 * Created by armdev on 7/7/14.
 */
public class AcmParticipantType implements Serializable
{
    private static final long serialVersionUID = 5828137798364937588L;

    private String name;
    private String description;
    private AcmParticipantTypes type;
    private boolean requiredOnACL;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public AcmParticipantTypes getType()
    {
        return type;
    }

    public void setType(AcmParticipantTypes type)
    {
        this.type = type;
    }

    public boolean isRequiredOnACL()
    {
        return requiredOnACL;
    }

    public void setRequiredOnACL(boolean requiredOnACL)
    {
        this.requiredOnACL = requiredOnACL;
    }
}
