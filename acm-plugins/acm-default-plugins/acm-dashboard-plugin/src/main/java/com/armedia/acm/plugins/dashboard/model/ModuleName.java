package com.armedia.acm.plugins.dashboard.model;

/**
 * Created by marjan.stefanoski on 12.01.2016.
 */
public enum ModuleName
{
    DASHBOARD("DASHBOARD"),
    CASE("CASE"),
    TIME("TIME"),
    COMPLAINT("COMPLAINT"),
    TASK("TASK"),
    COST("COST");

    private String moduleName;

    private ModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public String getModuleNameValue()
    {
        return moduleName;
    }

    public static ModuleName getModuleName(String text)
    {
        for (ModuleName attribute : values())
        {
            if (attribute.moduleName.equals(text))
            {
                return attribute;
            }
        }
        return ModuleName.DASHBOARD;
    }

}
