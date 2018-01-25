package com.armedia.acm.services.search.model;

import com.armedia.acm.pluginmanager.model.AcmPlugin;

public abstract class ReportGenerator
{
    private AcmPlugin propertyMap;

    public abstract String generateReport(String[] requestedFields, String[] titles, String jsonData);

    public abstract String generateReportName(String name);

    public abstract String getReportContentType();

    public AcmPlugin getPropertyMap()
    {
        return propertyMap;
    }

    public void setPropertyMap(AcmPlugin propertyMap)
    {
        this.propertyMap = propertyMap;
    }
}
