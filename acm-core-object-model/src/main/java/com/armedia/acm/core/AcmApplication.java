package com.armedia.acm.core;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public class AcmApplication implements Serializable
{
    private static final long serialVersionUID = -4533090175042467646L;
    private String applicationName;
    private List<AcmUserAction> topbarActions;

    public String getApplicationName()
    {
        return applicationName;
    }

    public void setApplicationName(String applicationName)
    {
        this.applicationName = applicationName;
    }

    public List<AcmUserAction> getTopbarActions()
    {
        return topbarActions;
    }

    public void setTopbarActions(List<AcmUserAction> topbarActions)
    {
        this.topbarActions = topbarActions;
    }
}
