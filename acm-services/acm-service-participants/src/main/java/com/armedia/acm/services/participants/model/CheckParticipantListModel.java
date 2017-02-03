package com.armedia.acm.services.participants.model;


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
