package com.armedia.acm.services.exemption.model;

public class ExemptionStatuteCreatedEvent extends ExemptionStatuteEvent
{
    public ExemptionStatuteCreatedEvent(ExemptionStatute source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return ExemptionConstants.EXEMPTION_STATUTE_CREATED_EVENT;
    }
}