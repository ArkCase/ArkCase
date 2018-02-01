package com.armedia.acm.plugins.ecm.pipeline.presave;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.utils.EcmFileMuleUtils;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.chemistry.opencmis.client.api.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by sasko.tanaskoski
 */
public class EcmFileUpdateContentHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext>
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
            try (InputStream fileInputStream = new FileInputStream(pipelineContext.getFileContents()))
            {
                // Updates the file to the Alfresco content repository as a new document
                Document newDocument = ecmFileMuleUtils.updateFile(entity, pipelineContext.getEcmFile(), fileInputStream);
                pipelineContext.setCmisDocument(newDocument);
            }
            catch (Exception e)
            {
                log.error("mule pre save handler failed: {}", e.getMessage(), e);
                throw new PipelineProcessException(e);
            }
        }
    }

    @Override
    public void rollback(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {

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