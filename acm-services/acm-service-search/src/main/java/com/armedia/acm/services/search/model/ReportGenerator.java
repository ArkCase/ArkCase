package com.armedia.acm.services.search.model;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import org.json.JSONObject;


public abstract class ReportGenerator
{
    private AcmPlugin propertyMap;

    public abstract byte[] generateReport(String[] requestedFields, String jsonData);

    public abstract String generateReportName(String name);

    public abstract String getReportContentType();

    public JSONObject findFields()
    {
        String exportFields = (String) getPropertyMap().getPluginProperties().get(SearchConstants.EXPORT_FIELDS);
        JSONObject fields = new JSONObject(exportFields);
        return fields;
    }

    public AcmPlugin getPropertyMap()
    {
        return propertyMap;
    }

    public void setPropertyMap(AcmPlugin propertyMap)
    {
        this.propertyMap = propertyMap;
    }
}
