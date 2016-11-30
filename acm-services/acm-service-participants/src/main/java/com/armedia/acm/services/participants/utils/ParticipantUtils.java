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
		return participants.stream().filter(p -> ASSIGNEE.equals(p.getParticipantType())).findFirst().map(AcmParticipant::getParticipantLdapId).orElse(null);
	}

	public static String getOwningGroupIdFromParticipants(List<AcmParticipant> participants)
	{
		return participants.stream().filter(p -> OWNINGGROUP.equals(p.getParticipantType())).findFirst().map(AcmParticipant::getParticipantLdapId).orElse(null);
	}
}
