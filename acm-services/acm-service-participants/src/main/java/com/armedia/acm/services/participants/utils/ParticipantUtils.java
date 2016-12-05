package com.armedia.acm.services.participants.utils;

import com.armedia.acm.services.participants.model.AcmParticipant;

import java.util.List;
import java.util.stream.Collectors;


public class ParticipantUtils
{

    private static final String ASSIGNEE = "assignee";

    private static final String OWNINGGROUP = "owning group";

    public static String createParticipantsListJson(List<AcmParticipant> participants)
    {
        return participants.stream()
                .map(p -> String.format("{\"ldapId\":\"%s\", \"type\":\"%s\"}", p.getParticipantLdapId(), p.getParticipantType()))
                .collect(Collectors.joining(",", "[", "]"));
    }

    public static String getAssigneeIdFromParticipants(List<AcmParticipant> participants)
    {
        return participants.stream().filter(p -> ASSIGNEE.equals(p.getParticipantType())).findFirst().map(AcmParticipant::getParticipantLdapId).orElse(null);
    }

    public static String getOwningGroupIdFromParticipants(List<AcmParticipant> participants)
    {
        return participants.stream().filter(p -> OWNINGGROUP.equals(p.getParticipantType())).findFirst().map(AcmParticipant::getParticipantLdapId).orElse(null);
    }
}
