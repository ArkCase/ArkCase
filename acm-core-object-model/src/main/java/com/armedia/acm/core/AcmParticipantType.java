package com.armedia.acm.core;

import com.armedia.acm.core.enums.AcmParticipantTypes;

/**
 * Created by armdev on 7/7/14.
 */
public class AcmParticipantType
{
    private String name;
    private String description;
    private AcmParticipantTypes type;
    private boolean requiredOnACL;

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

    public AcmParticipantTypes getType()
    {
        return type;
    }

    public void setType(AcmParticipantTypes type)
    {
        this.type = type;
    }

    public boolean isRequiredOnACL()
    {
        return requiredOnACL;
    }

    public void setRequiredOnACL(boolean requiredOnACL)
    {
        this.requiredOnACL = requiredOnACL;
    }
}
