package com.armedia.acm.services.participants.model;


import java.util.List;

public class CheckParticipantListModel
{
    private List<String> errors;
    private List<AcmParticipant> participantList;
    private String objectType;

    public List<String> getErrors()
    {
        return errors;
    }

    public void setErrors(List<String> errors)
    {
        this.errors = errors;
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
}
