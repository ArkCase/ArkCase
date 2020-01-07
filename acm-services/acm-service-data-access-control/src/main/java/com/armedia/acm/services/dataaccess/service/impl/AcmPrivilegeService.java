package com.armedia.acm.services.dataaccess.service.impl;

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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 1/6/15.
 */
public class AcmPrivilegeService
{
    private transient final Logger log = LogManager.getLogger(getClass());

    /**
     * Grant privileges to object participants based on an access specification. The access specification is
     * configured in the drools-access-control-rules.xlsx spreadsheet. It must conform to the following pattern:
     * <p/>
     * 
     * <pre>
     * [grant|deny|mandatory deny] [access level] to [participant type][, participant type...]
     * </pre>
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
     * @param accessSpec
     *            Must follow the pattern '[grant|deny|mandatory deny] [access level] to [participant type][,
     *            participant type...]'
     */
    public void setPrivileges(AcmAssignedObject obj, String accessSpec)
    {
        log.trace("Set privilege '{}' to object: {}[{}]", accessSpec, obj.getObjectType(), obj.getId());

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
            log.trace("checking type '{}', user '{}'", ap.getParticipantType(), ap.getParticipantLdapId());

            if (participantTypes.contains((ap.getParticipantType().toLowerCase())))
            {
                boolean found = false;
                for (AcmParticipantPrivilege priv : ap.getPrivileges())
                {
                    log.trace("object action: '{}', rule action: '{}'", priv.getObjectAction(), action);
                    if (action.equals(priv.getObjectAction()))
                    {
                        found = true;
                        if (!mode.equals(priv.getAccessType())
                                || !DataAccessControlConstants.ACCESS_REASON_POLICY.equals(priv.getAccessReason()))
                        {
                            priv.setAccessType(mode);
                            priv.setAccessReason(DataAccessControlConstants.ACCESS_REASON_POLICY);
                            log.debug("updated existing privilege [{} '{}' to '{}'='{}']", mode, action, ap.getParticipantType(),
                                    ap.getParticipantLdapId());
                            break;
                        }
                    }
                }

                if (!found)
                {
                    AcmParticipantPrivilege priv = new AcmParticipantPrivilege();
                    priv.setAccessType(mode);
                    priv.setAccessReason(DataAccessControlConstants.ACCESS_REASON_POLICY);
                    priv.setObjectAction(action);
                    ap.getPrivileges().add(priv);

                    log.debug("added privilege '{}' to '{}'", action, ap.getParticipantLdapId());
                }
            }
        }

    }

    private List<String> participantTypesToList(String participantType)
    {
        List<String> participantTypes = new ArrayList<>();
        String[] types = participantType.split(",");
        for (String type : types)
        {
            participantTypes.add(type.trim().toLowerCase());
        }

        return participantTypes;
    }
}
