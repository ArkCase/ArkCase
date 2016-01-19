package com.armedia.acm.plugins.dashboard.model.userPreference;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 19.01.2016.
 */
public class UserPreferencePersistenceEvent extends AcmEvent
{
    public UserPreferencePersistenceEvent(UserPreference source)
    {
        super(source);
        setObjectId(source.getUserPreferenceId());
        setEventDate(new Date());
        setUserId(source.getUser().getUserId());
    }

    @Override
    public String getObjectType()
    {
        return UserPreferenceConstants.OBJECT_TYPE;
    }
}
