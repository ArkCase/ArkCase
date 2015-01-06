package com.armedia.acm.services.participants.model;

/**
 * Created by armdev on 1/5/15.
 */
public class AcmParticipantPrivilege
{
    private String objectAction;
    private String accessType;
    private String accessReason;
    private AcmParticipant participant;

    public String getObjectAction()
    {
        return objectAction;
    }

    public void setObjectAction(String objectAction)
    {
        this.objectAction = objectAction;
    }

    public String getAccessType()
    {
        return accessType;
    }

    public void setAccessType(String accessType)
    {
        this.accessType = accessType;
    }

    public String getAccessReason()
    {
        return accessReason;
    }

    public void setAccessReason(String accessReason)
    {
        this.accessReason = accessReason;
    }

    public AcmParticipant getParticipant()
    {
        return participant;
    }

    public void setParticipant(AcmParticipant participant)
    {
        this.participant = participant;
    }
}
