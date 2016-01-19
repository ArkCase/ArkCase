package com.armedia.acm.plugins.dashboard.model.userPreference;

/**
 * Created by marjan.stefanoski on 19.01.2016.
 */
public class UserPreferenceCreatedEvent extends UserPreferencePersistenceEvent
{
    public UserPreferenceCreatedEvent(UserPreference source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return UserPreferenceConstants.EVENT_TYPE_USER_PREFERENCE_CREATED;
    }
}
