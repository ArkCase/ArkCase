package com.armedia.acm.services.dataaccess.service;

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

import com.armedia.acm.services.dataaccess.model.DataAccessControlConstants;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantPrivilege;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 1/14/15.
 */
public class ParticipantAccessChecker
{
    public List<String> getDenied(AcmAssignedObject in)
    {
        return getReadersWithLevel("mandatory deny", in);
    }

    public List<String> getReaders(AcmAssignedObject in)
    {
        return getReadersWithLevel("grant", in);
    }

    private List<String> getReadersWithLevel(String level, AcmAssignedObject in)
    {
        List<String> readers = new ArrayList<>();
        for (AcmParticipant ap : in.getParticipants())
        {
            for (AcmParticipantPrivilege priv : ap.getPrivileges())
            {
                if (DataAccessControlConstants.ACCESS_LEVEL_READ.equals(priv.getObjectAction()) && level.equals(priv.getAccessType()))
                {
                    readers.add(ap.getParticipantLdapId());
                    break;
                }
            }
        }

        return readers;
    }

    public boolean defaultUserHasRead(AcmAssignedObject in)
    {
        boolean defaultAccessorFound = false;

        for (AcmParticipant ap : in.getParticipants())
        {
            if (DataAccessControlConstants.DEFAULT_ACCESSOR.equals(ap.getParticipantLdapId()))
            {
                defaultAccessorFound = true;
                for (AcmParticipantPrivilege priv : ap.getPrivileges())
                {
                    if (DataAccessControlConstants.ACCESS_LEVEL_READ.equals(priv.getObjectAction()) &&
                            DataAccessControlConstants.ACCESS_GRANT.equals(priv.getAccessType()))
                    {
                        return true;
                    }
                }
            }
        }

        // if there was no default accessor entry at all, then we return true (global read by default).
        // if there was a default accessor entry, but we got here, then we return false.
        return !defaultAccessorFound;
    }

}
