package com.armedia.acm.services.participants.model;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckParticipantListModel
{
    private List<String> errorsList = new ArrayList<>();
    private List<AcmParticipant> participantList;
    private String objectType;
    private Map<String, List<String>> participants;

    public List<String> getErrorsList()
    {
        return errorsList;
    }

    public void setErrorsList(List<String> errorsList)
    {
        this.errorsList = errorsList;
    }

    public List<AcmParticipant> getParticipantList()
    {
        return participantList;
    }

    public void setParticipantList(List<AcmParticipant> participantList)
    {
        this.participantList = participantList;

        if (this.participantList != null)
        {
            this.participantList.stream().forEach(item -> addToParticipantListByType(item));
        }
    }

    private void addToParticipantListByType(AcmParticipant participant)
    {
        if (participant != null)
        {
            if (this.participants == null)
            {
                this.participants = new HashMap<>();
            }

            List<String> participants = new ArrayList<>();
            String type = participant.getParticipantType();
            String id = participant.getParticipantLdapId();

            if (this.participants.containsKey(type))
            {
                participants = this.participants.get(type);

            }

            participants.add(id);

            this.participants.put(type, participants);
        }
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public void addErrorMessage(String reason)
    {
        errorsList.add(reason);
    }

    public Map<String, List<String>> getParticipants()
    {
        return participants;
    }
}
