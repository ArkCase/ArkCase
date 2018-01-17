package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFolderDeclareRequestEvent;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class AcmFolderDeclareRequestListener implements ApplicationListener<EcmFolderDeclareRequestEvent>
{

    private transient Logger log = LoggerFactory.getLogger(getClass());
    private AlfrescoRecordsService alfrescoRecordsService;
    private AcmFolderService acmFolderService;
    private AcmFolderDao acmFolderDao;

    @Override
    public void onApplicationEvent(EcmFolderDeclareRequestEvent ecmFolderDeclareRequestEvent)
    {
        boolean proceed = getAlfrescoRecordsService()
                .checkIntegrationEnabled(AlfrescoRmaPluginConstants.FOLDER_DECLARE_REQUEST_INTEGRATION_KEY);

        if (!proceed)
        {
            return;
        }

        if (!ecmFolderDeclareRequestEvent.isSucceeded())
        {
            if (log.isTraceEnabled())
            {
                log.trace("Returning - folder declaration request was not successful");
            }
            return;
        }
        if (null != ecmFolderDeclareRequestEvent.getSource())
        {
            AcmCmisObjectList acmCmisObjectList = ecmFolderDeclareRequestEvent.getSource();
            getAlfrescoRecordsService().declareAllFilesInFolderAsRecords(acmCmisObjectList, ecmFolderDeclareRequestEvent.getContainer(),
                    ecmFolderDeclareRequestEvent.getEventDate(), ecmFolderDeclareRequestEvent.getParentObjectName());

            AcmFolder folder = getAcmFolderService().findById(acmCmisObjectList.getFolderId());
            try
            {
                if (folder == null)
                {
                    throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, folder.getId(), "Folder not found", null);
                }
                else
                {
                    folder.setStatus(EcmFileConstants.RECORD);
                    getAcmFolderDao().save(folder);
                    if (log.isDebugEnabled())
                    {
                        log.debug("Folder with ID: " + folder.getId() + " Status is changed to " + EcmFileConstants.RECORD);
                    }
                }
            }
            catch (Exception e)
            {
                if (log.isErrorEnabled())
                {
                    log.error("Status change failed for " + folder.getName() + " folder" + e.getMessage(), e);
                }
            }
        }
        else
        {
            return;
        }
    }

    public AlfrescoRecordsService getAlfrescoRecordsService()
    {
        return alfrescoRecordsService;
    }

    public void setAlfrescoRecordsService(AlfrescoRecordsService alfrescoRecordsService)
    {
        this.alfrescoRecordsService = alfrescoRecordsService;
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

    public AcmFolderDao getAcmFolderDao()
    {
        return acmFolderDao;
    }

    public void setAcmFolderDao(AcmFolderDao acmFolderDao)
    {
        this.acmFolderDao = acmFolderDao;
    }
}
