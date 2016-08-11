package com.armedia.acm.plugins.ecm.pipeline.presave;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.utils.EcmFileMuleUtils;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.chemistry.opencmis.client.api.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

/**
 * Created by joseph.mcgrady on 9/28/2015.
 */
public class EcmFileNewContentHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext>
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileMuleUtils ecmFileMuleUtils;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        if (entity == null)
        {
            throw new PipelineProcessException("ecmFile is null");
        }

        if (!pipelineContext.getIsAppend())
        {
            try
            {
                // Adds the file to the Alfresco content repository as a new document
                Document newDocument = ecmFileMuleUtils.addFile(entity, pipelineContext.getCmisFolderId(),
                        new ByteArrayInputStream(pipelineContext.getFileByteArray()));
                pipelineContext.setCmisDocument(newDocument);
            } catch (Exception e)
            {
                log.error("mule pre save handler failed: {}", e.getMessage(), e);
                throw new PipelineProcessException(e);
            }
        }
    }

    @Override
    public void rollback(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("mule pre save handler rollback called");

        // JPA cannot rollback content in the Alfresco repository so it must be manually deleted
        if (!pipelineContext.getIsAppend())
        {
            try
            {
                // We need the cmis id of the file in order to delete it
                Document cmisDocument = pipelineContext.getCmisDocument();
                if (cmisDocument == null)
                {
                    throw new Exception("cmisDocument is null");
                }

                // Removes the document from the Alfresco content repository
                ecmFileMuleUtils.deleteFile(entity, cmisDocument.getId());

            } catch (Exception e)
            { // since the rollback failed an orphan document will exist in Alfresco
                log.error("rollback of file upload failed: {}", e.getMessage(), e);
                throw new PipelineProcessException(e);
            }
            log.debug("mule pre save handler rollback ended");
        }
    }

    public EcmFileMuleUtils getEcmFileMuleUtils()
    {
        return ecmFileMuleUtils;
    }

    public void setEcmFileMuleUtils(EcmFileMuleUtils ecmFileMuleUtils)
    {
        this.ecmFileMuleUtils = ecmFileMuleUtils;
    }
}