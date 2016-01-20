package com.armedia.acm.plugins.dashboard.model.module;

/**
 * Created by marjan.stefanoski on 19.01.2016.
 */
public class ModuleCreatedEvent extends ModulePersistenceEvent
{

    public ModuleCreatedEvent(Module source, String userId)
    {
        super(source, userId);
    }

    @Override
    public String getEventType()
    {
        return ModuleConstants.EVENT_TYPE_MODULE_NAME_CREATED;
    }
}
