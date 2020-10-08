package com.armedia.acm.services.exemption.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class ExemptionStatuteEvent extends AcmEvent
{

    private static final long serialVersionUID = -2520075988463723949L;

    public ExemptionStatuteEvent(ExemptionStatute source)
    {
        super(source);
        setEventDate(new Date());
        setObjectId(source.getId());
        setObjectType(source.getParentObjectType());
    }
}
