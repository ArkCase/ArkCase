package com.armedia.acm.services.transcribe.model;

import java.util.Date;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 04/02/2018
 */
public class TranscribeCreatedEvent extends TranscribeEvent
{
    public TranscribeCreatedEvent(Transcribe source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return TranscribeConstants.TRANSCRIBE_CREATED_EVENT;
    }
}
