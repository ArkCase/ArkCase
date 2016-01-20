package com.armedia.acm.plugins.dashboard.model.module;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 19.01.2016.
 */
public class ModulePersistenceEvent extends AcmEvent
{
    public ModulePersistenceEvent(Module source, String userId)
    {
        super(source);
        setObjectId(source.getModuleId());
        setEventDate(new Date());
        setUserId(userId);
    }

    @Override
    public String getObjectType()
    {
        return ModuleConstants.OBJECT_TYPE;
    }
}
