package com.armedia.acm.plugins.consultation.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class FileAddedEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.consultation.file.added";

    public FileAddedEvent(Consultation source, String ipAddress)
    {

        super(source);

        setObjectId(source.getId());
        setObjectType(source.getObjectType());
        setEventDate(new Date());
        setIpAddress(ipAddress);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
