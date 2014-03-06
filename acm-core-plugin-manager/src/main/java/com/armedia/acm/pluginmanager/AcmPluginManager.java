package com.armedia.acm.pluginmanager;

import java.util.Collection;
import java.util.Collections;

public class AcmPluginManager
{
    private Collection<AcmPlugin> acmPlugins;

    public String getName()
    {
        return "acmPluginManager Restored";
    }

    public Collection<AcmPlugin> getAcmPlugins()
    {
        return Collections.unmodifiableCollection(acmPlugins);
    }

    public void setAcmPlugins(Collection<AcmPlugin> acmPlugins)
    {
        this.acmPlugins = acmPlugins;
    }
}
