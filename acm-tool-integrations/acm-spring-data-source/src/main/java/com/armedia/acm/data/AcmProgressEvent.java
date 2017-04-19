package com.armedia.acm.data;

import org.springframework.context.ApplicationEvent;


public class AcmProgressEvent extends ApplicationEvent
{
    private AcmProgressIndicator acmProgressIndicator;
    public AcmProgressEvent(AcmProgressIndicator source)
    {
        super(source);
        this.acmProgressIndicator = source;
    }

    public AcmProgressIndicator getAcmProgressIndicator()
    {
        return acmProgressIndicator;
    }
}
