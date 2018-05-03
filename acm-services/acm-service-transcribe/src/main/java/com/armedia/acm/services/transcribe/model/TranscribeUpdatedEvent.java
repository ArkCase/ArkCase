package com.armedia.acm.services.transcribe.model;

import java.util.Date;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 04/02/2018
 */
public class TranscribeUpdatedEvent extends TranscribeEvent
{
    public TranscribeUpdatedEvent(Transcribe source)
    {
        super(source);
        setEventDate(new Date());
        setObjectId(source.getId());
        setObjectType(source.getObjectType());
        setParentObjectId(source.getMediaEcmFileVersion().getId());
        setParentObjectType(source.getMediaEcmFileVersion().getObjectType());
        setParentObjectName(source.getMediaEcmFileVersion().getFile().getFileName());
    }

    @Override
    public String getEventType()
    {
        return TranscribeConstants.TRANSCRIBE_UPDATED_EVENT;
    }
}
