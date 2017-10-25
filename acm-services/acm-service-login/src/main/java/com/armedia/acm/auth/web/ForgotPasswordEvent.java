package com.armedia.acm.auth.web;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;

public class ForgotPasswordEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.forgotpassword";

    public ForgotPasswordEvent(AcmUser acmUser)
    {
        super(acmUser);
        setUserId(acmUser.getUserId());
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
