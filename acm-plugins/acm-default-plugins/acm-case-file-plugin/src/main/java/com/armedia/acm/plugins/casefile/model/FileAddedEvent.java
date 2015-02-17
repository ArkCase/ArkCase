package com.armedia.acm.plugins.casefile.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 11.02.2015.
 */
public class FileAddedEvent extends AcmEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.casefile.file.added";

    public FileAddedEvent( CaseFile source ) {

        super(source);

        setObjectId(source.getId());
        setObjectType(source.getObjectType());
        setEventDate(new Date());
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
