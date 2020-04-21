package com.armedia.acm.services.exemption.model;

import java.util.Date;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.plugins.ecm.model.EcmFile;

/**
 * Created by ana.serafimoska
 */
public class DocumentRedactionEvent extends AcmEvent
{

    private static final long serialVersionUID = -2378737634221219733L;

    public DocumentRedactionEvent(EcmFile source)
    {
        super(source);
        setEventDate(new Date());
        setObjectId(source.getId());
        setObjectType(source.getParentObjectType());
    }

    @Override
    public EcmFile getSource()
    {
        return (EcmFile) super.getSource();
    }
}
