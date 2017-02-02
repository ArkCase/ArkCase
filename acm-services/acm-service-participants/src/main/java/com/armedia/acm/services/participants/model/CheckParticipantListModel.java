package com.armedia.acm.services.participants.model;


import java.util.ArrayList;
import java.util.List;

public class CheckParticipantListModel
{
    private List<String> errorsList = new ArrayList<>();
    private List<AcmParticipant> participantList;
    private String objectType;

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
}
