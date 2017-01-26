package com.armedia.acm.services.note.model;

import com.armedia.acm.core.model.AcmEvent;

public class ApplicationNoteEvent extends AcmEvent
{

    private static final long serialVersionUID = -320828483774515322L;

    public ApplicationNoteEvent(Note source, String noteEvent, boolean succeeded, String ipAddress)
    {
        super(source);

        setObjectId(source.getId());
        setEventDate(source.getCreated());
        setUserId(source.getModifier());
        setEventType(String.format("%s.%s", NoteConstants.EVENT_TYPE, noteEvent));
        setSucceeded(succeeded);
        setIpAddress(ipAddress);
        setObjectType(NoteConstants.OBJECT_TYPE);
        setParentObjectId(source.getParentId());
        setParentObjectType(source.getParentType());
    }
}


