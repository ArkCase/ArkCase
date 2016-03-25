package com.armedia.acm.services.note.model;

import com.armedia.acm.core.model.AcmEvent;

public class ApplicationNoteEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.app.note";
    private static final long serialVersionUID = -320828483774515322L;

    public ApplicationNoteEvent(Note source, String noteEvent, boolean succeeded, String ipAddress)
    {
        super(source);

        setObjectId(source.getId());
        setEventDate(source.getCreated());
        setUserId(source.getCreator());
        setEventType(String.format("%s.%s", EVENT_TYPE, noteEvent));
        setSucceeded(succeeded);
        setIpAddress(ipAddress);
        setObjectType("NOTIFICATION");
        setParentObjectId(source.getParentId());
        setParentObjectType(source.getParentType());
    }
}


