package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.RecycleBinConstants;
import com.armedia.acm.plugins.ecm.service.RecycleBinItemService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CmisRepositoryRecycleBinContainerExecutor implements AcmDataUpdateExecutor
{
    private RecycleBinItemService recycleBinItemService;
    private final Logger log = LogManager.getLogger(getClass());

    @Override
    public String getUpdateId()
    {
        return "default-cmis-recycle-bin-container-created";
    }

    @Override
    public void execute()
    {
        try
        {
            getRecycleBinItemService().getOrCreateContainerForRecycleBin(RecycleBinConstants.OBJECT_TYPE,
                    EcmFileConstants.DEFAULT_CMIS_REPOSITORY_ID);
        }
        catch (AcmCreateObjectFailedException e)
        {
            log.error("Error on creating Recycle Bin container");
        }
    }

    public RecycleBinItemService getRecycleBinItemService()
    {
        return recycleBinItemService;
    }

    public void setRecycleBinItemService(RecycleBinItemService recycleBinItemService)
    {
        this.recycleBinItemService = recycleBinItemService;
    }
}
