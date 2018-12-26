package com.armedia.acm.plugins.ecm.service.sync.impl;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.persistence.NoResultException;

/**
 * @author ivana.shekerova on 12/21/2018.
 */
public class EcmFileMovedEventHandler implements ApplicationListener<EcmEvent>
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private AcmFolderService folderService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private AcmFolderDao folderDao;
    private EcmFileDao fileDao;
    private EcmFileService fileService;
    private FolderAndFilesUtils folderAndFilesUtils;

    public EcmFile onEcmFileMoved(EcmEvent ecmEvent)
    {

        getAuditPropertyEntityAdapter().setUserId(ecmEvent.getUserId());

        AcmFolder sourceParentFolder = lookupArkCaseFolder(ecmEvent.getSourceParentNodeId());
        // source parent folder is in arkcase
        if (sourceParentFolder != null)
        {
            AcmFolder targetParentFolder = lookupArkCaseFolder(ecmEvent.getTargetParentNodeId());
            // check if target folder is in arkcase
            if (targetParentFolder != null)
            {
                return moveFileIfTargetParentFolderIsInArkcase(ecmEvent, sourceParentFolder, targetParentFolder);
            }
            else
            {
                // delete the file, since target folder is not in arkcase
                try
                {
                    EcmFile arkCaseFile = lookupArkCaseFile(ecmEvent.getNodeId(), sourceParentFolder.getId());
                    if (arkCaseFile != null)
                    {
                        getFileService().deleteFileInArkcase(arkCaseFile.getId(), sourceParentFolder.getId(),
                                sourceParentFolder.getObjectType());
                        log.info("Deleted file with CMIS ID [{}]", ecmEvent.getNodeId());
                    }
                }
                catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
                {
                    log.error("Could not delete file with CMIS ID [{}] to ArkCase: {}", ecmEvent.getNodeId(), e.getMessage(), e);
                }
            }

        }
        else
        {
            // source parent folder is not in arkcase
            // check if target folder is in arkcase, if it is then upload file
            AcmFolder targetParentFolder = lookupArkCaseFolder(ecmEvent.getTargetParentNodeId());
            if (targetParentFolder != null)
            {
                AcmContainer container = lookupArkCaseContainer(targetParentFolder.getId());
                if (container == null)
                {
                    log.debug("Can't find container for the new file with id {}, exiting.", ecmEvent.getNodeId());
                    // return;
                }
                String cmisRepositoryId = getFolderService().getCmisRepositoryId(targetParentFolder);
                Document cmisDocument = lookupCmisDocument(cmisRepositoryId, ecmEvent.getNodeId());
                if (cmisDocument == null)
                {
                    log.error("No document to be loaded - exiting.");
                    // return;
                }
                EcmFile addedToArkCase = null;
                try
                {
                    addedToArkCase = getFileService().upload(
                            ecmEvent.getNodeName(),
                            findFileType(cmisDocument),
                            "Document",
                            cmisDocument.getContentStream().getStream(),
                            cmisDocument.getContentStreamMimeType(),
                            ecmEvent.getNodeName(),
                            new UsernamePasswordAuthenticationToken(ecmEvent.getUserId(), ecmEvent.getUserId()),
                            targetParentFolder.getCmisFolderId(),
                            container.getContainerObjectType(),
                            container.getContainerObjectId(),
                            targetParentFolder.getCmisRepositoryId(),
                            cmisDocument);
                }
                catch (AcmCreateObjectFailedException | AcmUserActionFailedException e)
                {
                    log.error("Could not add file with CMIS ID [{}] to ArkCase: {}", ecmEvent.getNodeId(), e.getMessage(), e);
                }
                return addedToArkCase;
            }
        }
        return null;
    }

    private EcmFile moveFileIfTargetParentFolderIsInArkcase(EcmEvent ecmEvent, AcmFolder sourceParentFolder, AcmFolder targetParentFolder)
    {
        EcmFile arkCaseFile = lookupArkCaseFile(ecmEvent.getNodeId(), sourceParentFolder.getId());
        if (arkCaseFile != null)
        {
            try
            {
                Long fileId = arkCaseFile.getId();
                Long targetObjectId = targetParentFolder.getId();
                Long dstFolderId = targetParentFolder.getId();

                EcmFile movedFile = getFileService().moveFileInAkcase(fileId, targetObjectId, ecmEvent.getTargetParentNodeType(),
                        dstFolderId);
                log.info("Moved file to ArkCase with CMIS ID [{}] and ArkCase ID [{}]", ecmEvent.getNodeId(), movedFile.getId());
                return movedFile;
            }
            catch (AcmUserActionFailedException | AcmObjectNotFoundException | AcmCreateObjectFailedException e)
            {
                log.error("Could not move file with CMIS ID [{}] to ArkCase: {}", ecmEvent.getNodeId(), e.getMessage(), e);
            }
        }
        return arkCaseFile;
    }

    /**
     * So subtypes can set the file type as needed.
     *
     * @param cmisDocument
     * @return
     */
    protected String findFileType(Document cmisDocument)
    {
        return "other";
    }

    protected Document lookupCmisDocument(String cmisRepositoryId, String nodeId)
    {
        try
        {
            CmisObject object = getFileService().findObjectById(cmisRepositoryId, nodeId);
            if (object != null && object instanceof Document)
            {
                return (Document) object;
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            log.error("Could not lookup CMIS document for node with id {}", nodeId);
            return null;
        }
    }

    protected AcmContainer lookupArkCaseContainer(Long parentFolderId)
    {
        try
        {
            AcmContainer found = getFolderService().findContainerByFolderId(parentFolderId);
            log.debug("ArkCase has container for folder with id {}", parentFolderId);
            return found;
        }
        catch (AcmObjectNotFoundException e)
        {
            log.debug("No container in ArkCase for folder: {}", parentFolderId);
            return null;
        }
    }

    protected EcmFile lookupArkCaseFile(String nodeId, Long parentFolderId)
    {
        try
        {
            EcmFile found = getFileDao().findByCmisFileIdAndFolderId(nodeId, parentFolderId);
            log.debug("ArkCase has file with CMIS ID {}: folder id is {}", nodeId, found.getId());
            return found;
        }
        catch (NoResultException e)
        {
            log.debug("No such file in ArkCase: {}", nodeId);
            return null;
        }
    }

    protected AcmFolder lookupArkCaseFolder(String folderCmisId)
    {
        try
        {
            AcmFolder found = getFolderDao().findByCmisFolderId(folderCmisId);
            log.debug("ArkCase has folder with CMIS ID {}: folder id is {}", folderCmisId, found.getId());
            return found;
        }
        catch (NoResultException e)
        {
            log.debug("No such folder in ArkCase: {}", folderCmisId);
            return null;
        }
    }

    protected boolean isMoveFileEvent(EcmEvent ecmEvent)
    {
        return EcmEventType.MOVE.equals(ecmEvent.getEcmEventType()) &&
                EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT.equals(ecmEvent.getNodeType());
    }

    @Override
    public void onApplicationEvent(EcmEvent ecmEvent)
    {
        if (isMoveFileEvent(ecmEvent))
        {
            onEcmFileMoved(ecmEvent);
        }
    }

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
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
