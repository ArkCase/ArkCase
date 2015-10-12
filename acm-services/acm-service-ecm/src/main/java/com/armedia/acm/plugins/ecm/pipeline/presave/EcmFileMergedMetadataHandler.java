package com.armedia.acm.plugins.ecm.pipeline.presave;

import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
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
import java.util.Date;

/**
 * Created by joseph.mcgrady on 9/28/2015.
 */
public class EcmFileMergedMetadataHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext>
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileDao ecmFileDao;
    private AcmFolderDao folderDao;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        // This handler only executes when the file was merged with a pre-existing document
        if (pipelineContext.getIsAppend())
        {

            // The new content is merged into an existing document, so the old document metadata is returned
            EcmFile oldFile = pipelineContext.getEcmFile();
            if (oldFile == null)
            {
                throw new PipelineProcessException("oldFile is null");
            }

            Document cmisDocument = pipelineContext.getCmisDocument();
            if (cmisDocument == null)
            {
                throw new PipelineProcessException("cmisDocument is null");
            }

            // Updates the versioning of the file
            oldFile.setVersionSeriesId(cmisDocument.getVersionSeriesId());
            oldFile.setActiveVersionTag(cmisDocument.getVersionLabel());
            EcmFileVersion version = new EcmFileVersion();
            version.setCmisObjectId(cmisDocument.getId());
            version.setVersionTag(cmisDocument.getVersionLabel());
            oldFile.getVersions().add(version);
            oldFile.setModified(new Date());

            // set page count
            if ("application/pdf".equals(entity.getFileMimeType()))
            {
                try
                {
                    PDDocument pdDocument = PDDocument.load(new ByteArrayInputStream(pipelineContext.getMergedFileByteArray()));
                    oldFile.setPageCount(pdDocument.getNumberOfPages());
                } catch (IOException e)
                {
                    throw new PipelineProcessException(e);
                }
            } else
            {
                log.warn("Still don't know how to retrieve the page count for [{}] mime type");
            }

            // Updates the database with the version changes
            EcmFile savedFile = ecmFileDao.save(oldFile);

            // The pipeline will output the updated metadata for the merged file
            pipelineContext.setEcmFile(savedFile);
        }
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