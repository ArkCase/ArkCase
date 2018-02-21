package com.armedia.acm.services.dataupdate.service;

import static com.armedia.acm.services.search.service.AcmJpaBatchUpdateService.SOLR_LAST_RUN_DATE_PROPERTY_KEY;

import com.armedia.acm.files.propertymanager.PropertyFileManager;

import java.util.ArrayList;
import java.util.List;

public class SolarReindexExecutor
{
    private String lastBatchUpdatePropertyFileLocation;
    private PropertyFileManager propertyFileManager;

    public void reindex(List<Class> entities)
    {
        List<String> propertiesToDelete = new ArrayList<>();
        for (Class entity : entities)
        {
            propertiesToDelete.add(SOLR_LAST_RUN_DATE_PROPERTY_KEY + "." + entity.getName());
        }

        propertyFileManager.removeMultiple(propertiesToDelete, lastBatchUpdatePropertyFileLocation);
    }

    public String getLastBatchUpdatePropertyFileLocation()
    {
        return lastBatchUpdatePropertyFileLocation;
    }

    public void setLastBatchUpdatePropertyFileLocation(String lastBatchUpdatePropertyFileLocation)
    {
        this.lastBatchUpdatePropertyFileLocation = lastBatchUpdatePropertyFileLocation;
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
