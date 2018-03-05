package com.armedia.acm.services.dataupdate.service;

import static com.armedia.acm.services.search.service.AcmJpaBatchUpdateService.SOLR_LAST_RUN_DATE_PROPERTY_KEY;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;

import java.util.ArrayList;
import java.util.List;

public class TriggerCostsheetSolrUpdateExecutor implements AcmDataUpdateExecutor
{

    private String lastBatchUpdatePropertyFileLocation;
    private PropertyFileManager propertyFileManager;

    @Override
    public String getUpdateId()
    {
        return "solr-costsheet-update-v2";
    }

    @Override
    public void execute()
    {
        List<String> propertiesToDelete = new ArrayList<>();
        propertiesToDelete.add(SOLR_LAST_RUN_DATE_PROPERTY_KEY + "." + AcmCostsheet.class.getName());

        propertyFileManager.removeMultiple(propertiesToDelete, lastBatchUpdatePropertyFileLocation);
    }

    public void setLastBatchUpdatePropertyFileLocation(String lastBatchUpdatePropertyFileLocation)
    {
        this.lastBatchUpdatePropertyFileLocation = lastBatchUpdatePropertyFileLocation;
    }

    public String getLastBatchUpdatePropertyFileLocation()
    {
        return lastBatchUpdatePropertyFileLocation;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }
}
