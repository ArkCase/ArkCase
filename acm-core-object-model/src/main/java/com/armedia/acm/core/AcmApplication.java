package com.armedia.acm.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class AcmApplication implements Serializable
{
    private static final long serialVersionUID = -4533090175042467646L;
    private String applicationName;
    private List<AcmUserAction> topbarActions;
    private List<AcmUserAction> navigatorTabs;
    private List<AcmObjectType> businessObjects;

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
        return Collections.unmodifiableList(topbarActions);
    }

    public void setTopbarActions(List<AcmUserAction> topbarActions)
    {
        this.topbarActions = topbarActions;
    }

    public List<AcmUserAction> getNavigatorTabs()
    {
        return Collections.unmodifiableList(navigatorTabs);
    }

    public void setNavigatorTabs(List<AcmUserAction> navigatorTabs)
    {
        this.navigatorTabs = navigatorTabs;
    }

    public List<AcmObjectType> getBusinessObjects()
    {
        return businessObjects;
    }

    public void setBusinessObjects(List<AcmObjectType> businessObjects)
    {
        this.businessObjects = businessObjects;
    }
}
