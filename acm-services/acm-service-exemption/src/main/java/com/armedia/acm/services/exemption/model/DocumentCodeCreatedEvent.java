package com.armedia.acm.services.exemption.model;

import com.armedia.acm.plugins.ecm.model.EcmFile;

/**
 * Created by ana.serafimoska
 */
public class DocumentCodeCreatedEvent extends DocumentRedactionEvent
{
    public DocumentCodeCreatedEvent(EcmFile source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return ExemptionConstants.EXEMPTION_CODE_CREATED_EVENT;
    }
}
