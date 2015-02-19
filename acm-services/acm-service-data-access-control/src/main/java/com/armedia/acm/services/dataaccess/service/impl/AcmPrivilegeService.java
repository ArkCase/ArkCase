package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.services.dataaccess.model.DataAccessControlConstants;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantPrivilege;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 1/6/15.
 */
public class AcmPrivilegeService
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Grant privileges to object participants based on an access specification.  The access specification is
     * configured in the drools-access-control-rules.xlsx spreadsheet.  It must conform to the following pattern:
     * <p/>
     *   <pre>[grant|deny|mandatory deny] [access level] to [participant type][, participant type...]</pre>
     * <p/>
     * Example: grant read to assignee
     * <br/>
     * Example: deny read to *
     * <br/>
     * Example: grant save to assignee, co-owner
     * <p/>
     * "*" is a special participant type; the access granted to "*" is the access granted to anyone who is not a
     * participant on this object.
     *
     * @param obj
     * @param accessSpec Must follow the pattern '[grant|deny|mandatory deny] [access level] to [participant type][, participant type...]'
     */
    public void setPrivileges(AcmAssignedObject obj, String accessSpec)
    {
        log.debug("Set privilege '" + accessSpec + "'");

        String[] parts = accessSpec.split(" ");
        // grant, deny, mandatory deny

        int idx = 0;
        String mode = parts[idx];
        if (DataAccessControlConstants.ACCESS_MANDATORY.equals(mode))
        {
            idx++;
            mode += " " + parts[idx];
        }

        idx++;
        String action = parts[idx];
        idx++;
        while (!parts[idx].equals(DataAccessControlConstants.ACCESS_LEVEL_PARTICIPANT_TYPE_SEPARATOR) && idx < parts.length)
        {
            action += " " + parts[idx];
            idx++;
        }

        // skip the word "to"
        idx++;
        String participantType = parts[idx];
        idx++;

        while (idx < parts.length)
        {
            participantType += " " + parts[idx];
            idx++;
        }

        List<String> participantTypes = participantTypesToList(participantType);

        // now we have the desired access, so we can grant it to every participant of the given participant type
        for (AcmParticipant ap : obj.getParticipants())
        {
            log.trace("checking type '" + ap.getParticipantType() + "', user '" + ap.getParticipantLdapId() + "'");
            if ( participantTypes.contains((ap.getParticipantType())) )
            {
                ap.setModified(new Date());
                log.trace("participant matches, checking privileges");
                boolean found = false;
                for (AcmParticipantPrivilege priv : ap.getPrivileges())
                {
                    log.trace("object action: '" + priv.getObjectAction() + "', rule action: '" + action + "'");
                    if (action.equals(priv.getObjectAction()))
                    {
                        found = true;
                        priv.setAccessType(mode);
                        priv.setAccessReason(DataAccessControlConstants.ACCESS_REASON_POLICY);
                        log.trace("updated existing privilege");
                        break;
                    }
                }

                if (!found)
                {
                    AcmParticipantPrivilege priv = new AcmParticipantPrivilege();
                    priv.setAccessType(mode);
                    priv.setAccessReason(DataAccessControlConstants.ACCESS_REASON_POLICY);
                    priv.setObjectAction(action);
                    ap.getPrivileges().add(priv);

                    log.trace("added privilege '" + action + "' to '" + ap.getParticipantLdapId() + "'");
                }
            }
        }

    }

    private List<String> participantTypesToList(String participantType)
    {
        List<String> participantTypes = new ArrayList<>();
        String[] types = participantType.split(",");
        for ( String type : types )
        {
            participantTypes.add(type.trim().toLowerCase());
        }

        return participantTypes;
    }
}
