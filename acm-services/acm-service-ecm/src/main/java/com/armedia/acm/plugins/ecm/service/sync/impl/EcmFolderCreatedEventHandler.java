package com.armedia.acm.plugins.ecm.service.sync.impl;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

/**
 * Created by dmiller on 5/17/17.
 */
public class EcmFolderCreatedEventHandler implements ApplicationListener<EcmEvent>
{
    private AcmFolderDao folderDao;

    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private AcmFolderService folderService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    public void onEcmFolderCreated(EcmEvent folderCreated)
    {
        log.debug("Handling new folder {}", folderCreated.getNodeId());

        if (lookupArkCaseFolder(folderCreated.getNodeId()) != null)
        {
            return;
        }

        AcmFolder parentFolder = lookupArkCaseFolder(folderCreated.getParentNodeId());
        if (parentFolder == null)
        {
            log.debug("Parent folder is not in ArkCase, so not creating the new folder {}", folderCreated.getNodeId());
            return;
        }

        AcmFolder newFolder = prepareFolder(parentFolder, folderCreated);

        try
        {
            // new folder should have creator and modifier of the user that took the action in the ECM system
            getAuditPropertyEntityAdapter().setUserId(folderCreated.getUserId());
            AcmFolder created = getFolderDao().save(newFolder);

            log.debug("Finished creating new folder with node id {}, ArkCase id {}", folderCreated.getNodeId(), created.getId());
        } catch (PersistenceException pe)
        {
            log.error("Cannot create new folder with CMIS ID {}: [{}]", folderCreated.getNodeId(), pe.getMessage(), pe);
        }
    }

    protected AcmFolder prepareFolder(AcmFolder parentFolder, EcmEvent folderCreated)
    {
        AcmFolder folder = new AcmFolder();
        folder.setParentFolder(parentFolder);
        folder.setName(folderCreated.getNodeName());
        folder.setCmisFolderId(folderCreated.getNodeId());

        String cmisRepositoryId = getFolderService().getCmisRepositoryId(parentFolder);
        folder.setCmisRepositoryId(cmisRepositoryId);

        return folder;
    }

    protected AcmFolder lookupArkCaseFolder(String folderCmisId)
    {
        try
        {
            AcmFolder found = getFolderDao().findByCmisFolderId(folderCmisId);
            log.debug("ArkCase has folder with CMIS ID {}: folder id is {}", folderCmisId, found.getId());
            return found;
        } catch (NoResultException e)
        {
            log.debug("No such folder in ArkCase: {}", folderCmisId);
            return null;
        }
    }

    protected boolean isNewFolderEvent(EcmEvent ecmEvent)
    {
        return EcmEventType.CREATE.equals(ecmEvent.getEcmEventType()) &&
                EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER.equals(ecmEvent.getNodeType());
    }

    @Override
    public void onApplicationEvent(EcmEvent ecmEvent)
    {
        if (isNewFolderEvent(ecmEvent))
        {
            onEcmFolderCreated(ecmEvent);
        }
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }
}
