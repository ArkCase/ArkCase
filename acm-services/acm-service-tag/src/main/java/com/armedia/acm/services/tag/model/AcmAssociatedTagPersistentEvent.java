package com.armedia.acm.services.tag.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class AcmAssociatedTagPersistentEvent  extends AcmEvent
{
    private static final String OBJECT_TYPE = "ASSOCIATE_TAG";

    public AcmAssociatedTagPersistentEvent(AcmAssociatedTag source,String userId) {

        super(source);
        setObjectId(source.getId());
        setEventDate(new Date());
        setUserId(userId);
        setObjectType(OBJECT_TYPE);
        setParentId(source.getParentId());
        setParentType(source.getParentType());
    }

    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }
}
