package com.armedia.acm.plugins.ecm.service.sync.impl;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.List;

/**
 * @author ivana.shekerova on 1/4/2019.
 */
public class EcmFileCopiedEventHandler implements ApplicationListener<EcmEvent>
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private EcmFileDao fileDao;
    private EcmFileService fileService;
    private FolderAndFilesUtils folderAndFilesUtils;

    public EcmFile onEcmFileCopied(EcmEvent ecmEvent)
    {
        getAuditPropertyEntityAdapter().setUserId(ecmEvent.getUserId());

        // if the copied file's folder ID is not an ArkCase folder, then the file was copied to a
        // non-ArkCase destination, and no further work is needed
        AcmFolder targetParentFolder = getFolderAndFilesUtils().lookupArkCaseFolder(ecmEvent.getParentNodeId());
        if (targetParentFolder == null)
        {
            log.debug("Can't find folder for the copied file with id {}, no further action.", ecmEvent.getNodeId());
            return null;
        }

        // If it already exists, the copy has already been done in ArkCase, and no further work is needed
        EcmFile arkCaseFile = getFolderAndFilesUtils().lookupArkCaseFile(ecmEvent.getNodeId(), targetParentFolder.getId());
        if (targetParentFolder != null && arkCaseFile == null)
        {
            List<EcmFile> listFiles = getFileDao().findByCmisFileId(ecmEvent.getSourceOfCopyNodeId());
            if (listFiles.isEmpty())
            {
                // not in arkcase, so the folder that the file was copied from is not an Arkcase folder
                return getFolderAndFilesUtils().uploadFile(ecmEvent, targetParentFolder);
            }
            else
            {
                // in arkcase, so the folder that the file was copied from is Arkcase folder
                return copyFile(ecmEvent, targetParentFolder, listFiles);
            }
        }

        return null;
    }

    private EcmFile copyFile(EcmEvent ecmEvent, AcmFolder targetParentFolder, List<EcmFile> listFiles)
    {
        EcmFile originalFile = listFiles.get(0);
        EcmFile copiedFile = null;
        try
        {
            copiedFile = getFileService().copyFileInArkcase(originalFile, ecmEvent.getNodeId(), targetParentFolder);
        }
        catch (AcmUserActionFailedException e)
        {
            log.error("Could not copy file with CMIS ID [{}] to ArkCase: {}", ecmEvent.getNodeId(), e.getMessage(), e);
        }
        return copiedFile;
    }

    protected boolean isCopyFileEvent(EcmEvent ecmEvent)
    {
        return EcmEventType.COPY.equals(ecmEvent.getEcmEventType()) &&
                EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT.equals(ecmEvent.getNodeType());
    }

    @Override
    public void onApplicationEvent(EcmEvent ecmEvent)
    {
        if (isCopyFileEvent(ecmEvent))
        {
            onEcmFileCopied(ecmEvent);
        }
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public EcmFileDao getFileDao()
    {
        return fileDao;
    }

    public void setFileDao(EcmFileDao fileDao)
    {
        this.fileDao = fileDao;
    }

    public EcmFileService getFileService()
    {
        return fileService;
    }

    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }
}
