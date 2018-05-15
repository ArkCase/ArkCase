package com.armedia.acm.services.dataupdate.model;

/*-
 * #%L
 * ACM Service: Data Update Service
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

import com.armedia.acm.services.users.model.AcmUser;

import java.util.Objects;

public class AcmUserUpdateHolder
{
    private String oldId;

    private String newId;

    private AcmUser newUser;

    public AcmUserUpdateHolder(String oldId, String newId, AcmUser newUser)
    {
        this.oldId = oldId;
        this.newId = newId;
        this.newUser = newUser;
    }

    public String getOldId()
    {
        return oldId;
    }

    public String getNewId()
    {
        return newId;
    }

    public AcmUser getNewUser()
    {
        return newUser;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AcmUserUpdateHolder holder = (AcmUserUpdateHolder) o;
        return Objects.equals(oldId, holder.oldId) &&
                Objects.equals(newId, holder.newId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(oldId, newId);
    }
}
