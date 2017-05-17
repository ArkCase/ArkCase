package com.armedia.acm.plugins.ecm.service.sync.impl;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.spring.SpringContextHolder;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.persistence.NoResultException;

/**
 * Created by dmiller on 5/17/17.
 */
public class EcmFileCreatedEventHandler implements ApplicationListener<EcmEvent>
{

    private AcmFolderService folderService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private AcmFolderDao folderDao;

    private EcmFileDao fileDao;
    private EcmFileService fileService;
    private SpringContextHolder springContextHolder;

    private FolderAndFilesUtils folderAndFilesUtils = new FolderAndFilesUtils();

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public void onEcmFileCreated(EcmEvent ecmEvent)
    {
        // parent folder must already be in ArkCase
        AcmFolder parentFolder = lookupArkCaseFolder(ecmEvent.getParentNodeId());
        if (parentFolder == null)
        {
            log.debug("Parent folder is not in ArkCase, so not creating the new folder {}", ecmEvent.getNodeId());
            return;
        }

        if (lookupArkCaseFile(ecmEvent.getNodeId(), parentFolder.getId()) != null)
        {
            log.debug("ArkCase already has the file with CMIS id {}", ecmEvent.getNodeId());
            return;
        }

        AcmContainer container = lookupArkCaseContainer(parentFolder.getId());
        if (container == null)
        {
            log.debug("Can't find container for the new file with id {}, exiting.", ecmEvent.getNodeId());
            return;
        }

        String cmisRepositoryId = getFolderService().getCmisRepositoryId(parentFolder);

        Document cmisDocument = lookupCmisDocument(cmisRepositoryId, ecmEvent.getNodeId());
        if (cmisDocument == null)
        {
            log.error("No document to be loaded - exiting.");
            return;
        }

        // get the pipeline manager and load the file
        PipelineManager<EcmFile, EcmFileTransactionPipelineContext> pipelineManager =
                getSpringContextHolder().getBeanByName("ecmFileUploadPipelineManager", PipelineManager.class);
        EcmFileTransactionPipelineContext context = buildContext(ecmEvent, container);
        EcmFile newFile = buildFile(ecmEvent, parentFolder, cmisDocument);
        try
        {
            EcmFile addedToArkCase = pipelineManager.executeOperation(newFile, context, () ->
            {
                return newFile;
            });
        } catch (PipelineProcessException e)
        {
            log.error("Error loading new file: {}", e.getMessage(), e);
        }

    }

    protected EcmFile buildFile(EcmEvent ecmEvent, AcmFolder parentFolder, Document cmisDocument)
    {
        EcmFile newFile = new EcmFile();

        newFile.setFileActiveVersionMimeType(cmisDocument.getContentStreamMimeType());
        newFile.setFileActiveVersionNameExtension(folderAndFilesUtils.getFileNameExtension(ecmEvent.getNodeName()));
        newFile.setFileName(ecmEvent.getNodeName());
        newFile.setFileType(findFileType(cmisDocument));
        newFile.setCmisRepositoryId(parentFolder.getCmisRepositoryId());
        return newFile;
    }

    protected EcmFileTransactionPipelineContext buildContext(EcmEvent ecmEvent, AcmContainer container)
    {
        EcmFileTransactionPipelineContext context = new EcmFileTransactionPipelineContext();

        context.setCmisFolderId(ecmEvent.getParentNodeId());
        context.setContainer(container);
        context.setAuthentication(new UsernamePasswordAuthenticationToken(ecmEvent.getUserId(), ecmEvent.getUserId()));
        context.setOriginalFileName(ecmEvent.getNodeName());

        // TODO: need to set the byte array in the context
        return context;
    }

    /**
     * So subtypes can set the file type as needed.
     *
     * @param cmisDocument
     * @return
     */
    protected String findFileType(Document cmisDocument)
    {
        return "Other";
    }

    private Document lookupCmisDocument(String cmisRepositoryId, String nodeId)
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
        } catch (Exception e)
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
        } catch (AcmObjectNotFoundException e)
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
        } catch (NoResultException e)
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
        } catch (NoResultException e)
        {
            log.debug("No such folder in ArkCase: {}", folderCmisId);
            return null;
        }
    }

    protected boolean isNewFileEvent(EcmEvent ecmEvent)
    {
        return EcmEventType.CREATE.equals(ecmEvent.getEcmEventType()) &&
                EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT.equals(ecmEvent.getNodeType());
    }

    @Override
    public void onApplicationEvent(EcmEvent ecmEvent)
    {
        if (isNewFileEvent(ecmEvent))
        {
            onEcmFileCreated(ecmEvent);
        }
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

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFileDao(EcmFileDao fileDao)
    {
        this.fileDao = fileDao;
    }

    public EcmFileDao getFileDao()
    {
        return fileDao;
    }

    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }

    public EcmFileService getFileService()
    {
        return fileService;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }
}
