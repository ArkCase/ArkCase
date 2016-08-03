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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by joseph.mcgrady on 9/14/2015.
 */
public class EcmFileNewMetadataHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext>
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileDao ecmFileDao;
    private AcmFolderDao folderDao;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("metadata pre save handler called");

        // Writes metadata for new document uploads into the database
        if (!pipelineContext.getIsAppend())
        {
            if (entity == null)
            {
                throw new PipelineProcessException("ecmFile is null");
            }

            Document cmisDocument = pipelineContext.getCmisDocument();
            if (cmisDocument == null)
            {
                throw new PipelineProcessException("cmisDocument is null");
            }

            entity.setVersionSeriesId(cmisDocument.getVersionSeriesId());
            entity.setActiveVersionTag(cmisDocument.getVersionLabel());
            entity.setFileName(pipelineContext.getOriginalFileName());

            // Sets the versioning of the file
            EcmFileVersion version = new EcmFileVersion();
            version.setCmisObjectId(cmisDocument.getId());
            version.setVersionTag(cmisDocument.getVersionLabel());
            version.setVersionMimeType(entity.getFileActiveVersionMimeType());
            version.setVersionFileNameExtension(entity.getFileActiveVersionNameExtension());
            entity.getVersions().add(version);

            // Determines the folder and container in which the file should be saved
            AcmFolder folder = getFolderDao().findByCmisFolderId(pipelineContext.getCmisFolderId());
            entity.setFolder(folder);
            entity.setContainer(pipelineContext.getContainer());

            // set page count
            if ("application/pdf".equals(entity.getFileActiveVersionMimeType()))
            {
                PDDocument pdDocument = null;
                try
                {
                    pdDocument = PDDocument.load(new ByteArrayInputStream(pipelineContext.getFileByteArray()));
                    entity.setPageCount(pdDocument.getNumberOfPages());
                } catch (IOException e)
                {
                    throw new PipelineProcessException(e);
                } finally
                {
                    if (pdDocument != null)
                    {
                        try
                        {
                            pdDocument.close();
                        } catch (Exception ex)
                        {
                            log.error("cannot close PDF: {}", ex.getMessage(), ex);
                        }
                    }
                }
            } else
            {
                log.warn("Still don't know how to retrieve the page count for [{}] mime type");
            }

            // Saves new file metadata into ArkCase database
            EcmFile saved = getEcmFileDao().save(entity);
            pipelineContext.setEcmFile(saved);
        }
        log.debug("metadata pre save handler ended");
    }

    @Override
    public void rollback(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        // rollback not needed, JPA will rollback the database changes.
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
    }
}