package com.armedia.acm.services.dataaccess.model.test;

import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by armdev on 2/16/15.
 */
public class DataAccessAssignedObject implements AcmAssignedObject
{
    private String status;
    private List<AcmParticipant> participants = new ArrayList<>();
    private Long id;

    public DataAccessAssignedObject()
    {
        id = new Random().nextLong();
    }

    @Override
    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public List<AcmParticipant> getParticipants()
    {
        return participants;
    }

    public void setParticipants(List<AcmParticipant> participants)
    {
        this.participants = participants;
    }

    @Override
    public String getObjectType()
    {
        return "DATA-ACCESS-TEST";
    }

    @Override
    public Long getId()
    {
        return id;
    }
}
