package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.core.model.AcmEvent;
import org.springframework.security.core.Authentication;
import sun.text.normalizer.ICUBinary;

import java.util.Date;

public class FileMadePublicEvent extends AcmEvent
{
    private static final long serialVersionUID = 7492500722858546075L;

    public FileMadePublicEvent(EcmFile source, Authentication auth)
    {
        super(source);
        setEventDate(new Date());
        setObjectId(source.getId());
        setUserId(auth.getName());
        setObjectType(EcmFileConstants.OBJECT_FILE_TYPE);
        setParentObjectType(source.getContainer().getContainerObjectType());
        setParentObjectId(source.getContainer().getContainerObjectId());
    }
}
