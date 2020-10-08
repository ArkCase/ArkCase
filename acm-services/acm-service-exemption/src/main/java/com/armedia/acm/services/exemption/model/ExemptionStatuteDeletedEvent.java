package com.armedia.acm.services.exemption.model;

public class ExemptionStatuteDeletedEvent extends ExemptionStatuteEvent
{

    public ExemptionStatuteDeletedEvent(ExemptionStatute source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return ExemptionConstants.EXEMPTION_STATUTE_DELETED_EVENT;
    }
}
