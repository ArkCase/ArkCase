package com.armedia.acm.plugins.dashboard.model.userPreference;

import java.util.List;

/**
 * Created by marjan.stefanoski on 14.01.2016.
 */
public class PreferredWidgetsDto
{
    private String moduleName;
    private List<String> preferredWidgets;

    public String getModuleName()
    {
        return moduleName;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public List<String> getPreferredWidgets()
    {
        return preferredWidgets;
    }

    public void setPreferredWidgets(List<String> preferredWidgets)
    {
        this.preferredWidgets = preferredWidgets;
    }
}
