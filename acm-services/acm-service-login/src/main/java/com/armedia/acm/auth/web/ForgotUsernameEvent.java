package com.armedia.acm.auth.web;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.AbstractMap;
import java.util.Date;
import java.util.List;

public class ForgotUsernameEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.forgotusername";

    public ForgotUsernameEvent(AbstractMap.SimpleImmutableEntry<String, List<String>> emailToUserAccounts)
    {
        super(emailToUserAccounts);
        setUserId(emailToUserAccounts.getValue().get(0));
        setEventDate(new Date());
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

    public AcmUser getAcmUser()
    {
        return (AcmUser) getSource();
    }
}
