package com.armedia.acm.core;

import java.io.Serializable;

/**
 *
 */
public class AcmExternalEvent implements Serializable
{
    private static final long serialVersionUID = -3552970932726234291L;
    private String externalEventName;

    public String getExternalEventName()
    {
        return externalEventName;
    }

    public void setExternalEventName(String externalEventName)
    {
        this.externalEventName = externalEventName;
    }
}
