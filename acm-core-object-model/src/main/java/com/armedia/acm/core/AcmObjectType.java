package com.armedia.acm.core;

import java.util.List;

/**
 * Created by armdev on 7/7/14.
 */
public class AcmObjectType
{
    private String name;
    private String description;
    private List<AcmObjectState> states;
    private List<AcmParticipantType> participantTypes;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<AcmObjectState> getStates()
    {
        return states;
    }

    public void setStates(List<AcmObjectState> states)
    {
        this.states = states;
    }

    public List<AcmParticipantType> getParticipantTypes()
    {
        return participantTypes;
    }

    public void setParticipantTypes(List<AcmParticipantType> participantTypes)
    {
        this.participantTypes = participantTypes;
    }
}
