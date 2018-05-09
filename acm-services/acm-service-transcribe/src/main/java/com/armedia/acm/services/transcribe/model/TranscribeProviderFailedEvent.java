package com.armedia.acm.services.transcribe.model;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 04/02/2018
 */
public class TranscribeProviderFailedEvent extends TranscribeEvent
{
    public TranscribeProviderFailedEvent(Transcribe source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return TranscribeConstants.TRANSCRIBE_PROVIDER_FAILED_EVENT;
    }

}
