package com.armedia.acm.services.dataaccess.service;

import com.armedia.acm.services.dataaccess.service.impl.AcmPrivilegeService;
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
                if (AcmPrivilegeService.ACCESS_LEVEL_READ.equals(priv.getObjectAction()) && level.equals(priv.getAccessType()) )
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
        for ( AcmParticipant ap : in.getParticipants() )
        {
            if ( AcmPrivilegeService.DEFAULT_ACCESSOR.equals(ap.getParticipantLdapId()) )
            {
                for ( AcmParticipantPrivilege priv : ap.getPrivileges() )
                {
                    if ( AcmPrivilegeService.ACCESS_LEVEL_READ.equals(priv.getObjectAction()) &&
                            AcmPrivilegeService.ACCESS_GRANT.equals(priv.getAccessType()) )
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
