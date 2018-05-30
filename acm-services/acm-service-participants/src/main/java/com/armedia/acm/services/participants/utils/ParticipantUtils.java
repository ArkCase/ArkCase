package com.armedia.acm.services.participants.utils;

/*-
 * #%L
 * ACM Service: Participants
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

import com.armedia.acm.services.participants.model.AcmParticipant;

import java.util.List;
import java.util.stream.Collectors;

public class ParticipantUtils
{

    private static final String ASSIGNEE = "assignee";
    private static final String OWNER = "owner";

    private static final String OWNINGGROUP = "owning group";

    public static String createParticipantsListJson(List<AcmParticipant> participants)
    {
        return participants.stream()
                .map(p -> String.format("{\"ldapId\":\"%s\", \"type\":\"%s\"}", p.getParticipantLdapId(), p.getParticipantType()))
                .collect(Collectors.joining(",", "[", "]"));
    }

    public static String getAssigneeIdFromParticipants(List<AcmParticipant> participants)
    {
        return participants.stream().filter(p -> ASSIGNEE.equals(p.getParticipantType())).findFirst()
                .map(AcmParticipant::getParticipantLdapId).orElse(null);
    }

    public static String getOwnerIdFromParticipants(List<AcmParticipant> participants)
    {
        return participants.stream().filter(p -> OWNER.equals(p.getParticipantType())).findFirst()
                .map(AcmParticipant::getParticipantLdapId).orElse(null);
    }

    public static String getOwningGroupIdFromParticipants(List<AcmParticipant> participants)
    {
        return participants.stream().filter(p -> OWNINGGROUP.equals(p.getParticipantType())).findFirst()
                .map(AcmParticipant::getParticipantLdapId).orElse(null);
    }
}
