package com.armedia.acm.plugins.ecm.pipeline.presave;

import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.apache.chemistry.opencmis.client.api.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by joseph.mcgrady on 9/14/2015.
 */
public class EcmFileAppendMetadataHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext> {
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileDao ecmFileDao;
    private AcmFolderDao folderDao;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("metadata pre save handler called");

        EcmFile toAdd = entity;
        if (toAdd == null)
            throw new PipelineProcessException("ecmFile is null");

        if (pipelineContext.getIsPDF()) {
            Document cmisDocument = pipelineContext.getCmisDocument();
            if (cmisDocument == null)
                throw new PipelineProcessException("cmisDocument is null");

            toAdd.setVersionSeriesId(cmisDocument.getVersionSeriesId());
            toAdd.setActiveVersionTag(cmisDocument.getVersionLabel());
            toAdd.setFileName(pipelineContext.getOriginalFileName());

            // Updates the versioning of the file (it may be replacing an older copy)
            EcmFileVersion version = new EcmFileVersion();
            version.setCmisObjectId(cmisDocument.getId());
            version.setVersionTag(cmisDocument.getVersionLabel());
            toAdd.getVersions().add(version);

            // Determines the folder and container in which the file should be saved
            AcmFolder folder = getFolderDao().findByCmisFolderId(pipelineContext.getCmisFolderId());
            toAdd.setFolder(folder);
            toAdd.setContainer(pipelineContext.getContainer());

            if (!pipelineContext.getIsAppend()) {
                // Saves file metadata into ArkCase
                EcmFile saved = getEcmFileDao().save(toAdd);
                pipelineContext.setEcmFile(saved);
            } else {
                EcmFile oldFile = pipelineContext.getEcmFile();
                toAdd.setFileId(oldFile.getFileId());
                toAdd.setCreator(oldFile.getCreator());
                toAdd.setCreated(oldFile.getCreated());
                toAdd.setModified(new Date());
                toAdd.setModifier(oldFile.getModifier());
                toAdd.setStatus(oldFile.getStatus());
                pipelineContext.setEcmFile(toAdd);
            }
        } else {
            pipelineContext.setEcmFile(toAdd);
        }
        log.debug("metadata pre save handler ended");
    }

    @Override
    public void rollback(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        // rollback not needed, JPA will rollback the database changes.
    }

    public EcmFileDao getEcmFileDao() {
        return ecmFileDao;
    }
    public void setEcmFileDao(EcmFileDao ecmFileDao) {
        this.ecmFileDao = ecmFileDao;
    }
    public AcmFolderDao getFolderDao() {
        return folderDao;
    }
    public void setFolderDao(AcmFolderDao folderDao) {
        this.folderDao = folderDao;
    }
}