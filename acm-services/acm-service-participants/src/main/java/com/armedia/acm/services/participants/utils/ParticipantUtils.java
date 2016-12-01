/**
 *
 */
package com.armedia.acm.services.participants.utils;

import java.util.List;
import java.util.stream.Collectors;

import com.armedia.acm.services.participants.model.AcmParticipant;

/**
 * @author riste.tutureski
 */
public class ParticipantUtils
{

	private static final String ASSIGNEE = "assignee";

	public static String getAssigneeIdFromParticipants(List<AcmParticipant> participants)
	{
		if (participants != null)
		{
			for (AcmParticipant participant : participants)
			{
				if (ASSIGNEE.equals(participant.getParticipantType()))
				{
					return participant.getParticipantLdapId();
				}
			}
		}

		return null;
	}

    public static String createParticipantsListJson(List<AcmParticipant> participants)
    {
        return participants.stream()
                .map(p -> String.format("{\"ldapId\":\"%s\", \"type\":\"%s\"}", p.getParticipantLdapId(), p.getParticipantType()))
                .collect(Collectors.joining(",", "[", "]"));
    }

}
