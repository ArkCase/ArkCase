package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantPrivilege;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by armdev on 1/6/15.
 */
public class AcmPrivilegeService
{

    public static final String ACCESS_MANDATORY = "mandatory";
    public static final String ACCESS_GRANT = "grant";
    public static final String ACCESS_DENY = "deny";
    public static final String ACCESS_LEVEL_PARTICIPANT_TYPE_SEPARATOR = "to";
    public static final String ACCESS_REASON_POLICY = "policy";
    public static final String DEFAULT_ACCESSOR = "*";
    public static final String ACCESS_LEVEL_READ = "read";

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Grant privileges to object participants based on an access specification.  The access specification is
     * configured in the drools-access-control-rules.xlsx spreadsheet.  It must conform to the following pattern:
     * <p/>
     *   <pre>[grant|deny|mandatory deny] [access level] to [participant type]</pre>
     * <p/>
     * Example: grant read to assignee
     * <br/>
     * Example: deny read to *
     * <p/>
     * "*" is a special participant type; the access granted to "*" is the access granted to anyone who is not a
     * participant on this object.
     *
     * NOTE This code is used from Drools rules engine via a rules spreadsheet. Don't believe IDEA when it says
     * this method is not used.
     * @param obj
     * @param accessSpec Must follow the pattern '[grant|deny|mandatory deny] [access level] to [participant type]'
     */
    public void setPrivileges(AcmAssignedObject obj, String accessSpec)
    {
        log.debug("Set privilege '" + accessSpec + "'");

        String[] parts = accessSpec.split(" ");
        // grant, deny, mandatory deny

        int idx = 0;
        String mode = parts[idx];
        if (ACCESS_MANDATORY.equals(mode))
        {
            idx++;
            mode += " " + parts[idx];
        }

        idx++;
        String action = parts[idx];
        idx++;
        while (!parts[idx].equals(ACCESS_LEVEL_PARTICIPANT_TYPE_SEPARATOR) && idx < parts.length)
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

        // now we have the desired access, so we can grant it to every participant of the given participant type
        for (AcmParticipant ap : obj.getParticipants())
        {
            if (participantType.equals(ap.getParticipantType()))
            {
                boolean found = false;
                for (AcmParticipantPrivilege priv : ap.getPrivileges())
                {
                    if (action.equals(priv.getObjectAction()))
                    {
                        found = true;
                        priv.setAccessType(mode);
                        priv.setAccessReason(ACCESS_REASON_POLICY);
                        break;
                    }
                }

                if (!found)
                {
                    AcmParticipantPrivilege priv = new AcmParticipantPrivilege();
                    priv.setAccessType(mode);
                    priv.setAccessReason(ACCESS_REASON_POLICY);
                    priv.setObjectAction(action);
                    ap.getPrivileges().add(priv);
                }
            }
        }

    }
}
