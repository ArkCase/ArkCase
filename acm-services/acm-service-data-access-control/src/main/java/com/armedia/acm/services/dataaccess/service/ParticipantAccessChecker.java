package com.armedia.acm.services.dataaccess.service;

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
        for ( AcmParticipant ap : in.getParticipants() )
        {
            for ( AcmParticipantPrivilege priv : ap.getPrivileges() )
            {
                if (DataAccessControlConstants.ACCESS_LEVEL_READ.equals(priv.getObjectAction()) && level.equals(priv.getAccessType()) )
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

        for ( AcmParticipant ap : in.getParticipants() )
        {
            if ( DataAccessControlConstants.DEFAULT_ACCESSOR.equals(ap.getParticipantLdapId()) )
            {
                defaultAccessorFound = true;
                for ( AcmParticipantPrivilege priv : ap.getPrivileges() )
                {
                    if ( DataAccessControlConstants.ACCESS_LEVEL_READ.equals(priv.getObjectAction()) &&
                            DataAccessControlConstants.ACCESS_GRANT.equals(priv.getAccessType()) )
                    {
                        return true;
                    }
                }
            }
        }

        // if there was no default accessor entry at all, then we return true (global read by default).
        // if there was a default accessor entry, but we got here, then we return false.
        return ! defaultAccessorFound;
    }

}
