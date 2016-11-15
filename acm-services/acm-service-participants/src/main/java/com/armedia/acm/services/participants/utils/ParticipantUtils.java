/**
 * 
 */
package com.armedia.acm.services.participants.utils;

import com.armedia.acm.services.participants.model.AcmParticipant;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class ParticipantUtils {

	private static final String ASSIGNEE = "assignee";

	private static final String OWNINGGROUP = "owning group";

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

	public static String getGroupIdFromParticipants(List<AcmParticipant> participants)
	{
		if (participants != null)
		{
			for (AcmParticipant participant : participants)
			{
				if (OWNINGGROUP.equals(participant.getParticipantType()))
				{
					return participant.getParticipantLdapId();
				}
			}
		}
		return null;
	}
}
