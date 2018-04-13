package com.armedia.acm.services.transcribe.model;

import com.armedia.acm.core.model.AcmEvent;
import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 04/02/2018
 */
public class TranscribeEvent extends AcmEvent
{
    public TranscribeEvent(Transcribe source)
    {
        super(source);
        setEventDate(new Date());
        setObjectId(source.getId());
        setObjectType(source.getObjectType());
    }

    @Override
    public Transcribe getSource()
    {
        return (Transcribe) super.getSource();
    }
}
