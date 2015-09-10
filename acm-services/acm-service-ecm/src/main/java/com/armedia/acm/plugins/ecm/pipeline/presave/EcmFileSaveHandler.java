package com.armedia.acm.plugins.ecm.pipeline.presave;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.apache.chemistry.opencmis.client.api.Document;
import org.mule.api.ExceptionPayload;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by joseph.mcgrady on 9/9/2015.
 */
public class EcmFileSaveHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext> {
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private MuleContextManager muleContextManager;

    @Override
    public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("mule pre save handler called");
        EcmFile toAdd = entity;  //pipelineContext.getEcmFile();
        if (toAdd == null)
            throw new PipelineProcessException("ecmFile is null");

        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("cmisFolderId", pipelineContext.getCmisFolderId());
        messageProps.put("inputStream", pipelineContext.getFileInputStream());

        try {
            log.debug("invoking the mule add file flow");
            MuleMessage received = getMuleContextManager().send("vm://addFile.in", toAdd, messageProps);
            MuleException e = received.getInboundProperty("saveException");
            if (e != null) throw e;

            // The next pipeline stage needs to have access to the cmis document returned from mule
            pipelineContext.setCmisDocument(received.getPayload(Document.class));
        } catch (Exception e) {
            log.error("mule pre save handler failed: " + e.getMessage());
            throw new PipelineProcessException("mule pre save handler failed: " + e.getMessage());
        }
        log.debug("mule pre save handler ended");
    }

    @Override
    public void rollback(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("mule pre save handler rollback called");
        // Since the mule flow creates a file in the repository, JPA cannot roll it back and it needs to be deleted manually
        try {
            // We need the cmis id of the file in order to delete it
            Document cmisDocument = pipelineContext.getCmisDocument();
            if (cmisDocument == null)
                throw new Exception("cmisDocument is null");

            // This is the request payload for mule including the unique cmis id for the document to delete
            Map<String, Object> messageProps = new HashMap<>();
            messageProps.put("ecmFileId", cmisDocument.getId());

            // Invokes the mule flow to delete the file contents from the repository
            log.debug("rolling back file upload for cmis id: " + cmisDocument.getId() + " using vm://deleteFile.in mule flow");
            MuleMessage fileDeleteResponse = getMuleContextManager().send("vm://deleteFile.in", entity, messageProps);
            ExceptionPayload exceptionPayload = fileDeleteResponse.getExceptionPayload();
            if (exceptionPayload != null)
                throw new Exception(exceptionPayload.getRootException());

        } catch (Exception e) { // since the rollback failed an orphan document will exist in Alfresco
            log.error("rollback of file upload failed: " + e.getMessage());
            throw new PipelineProcessException("rollback of file upload failed: " + e.getMessage());
        }
        log.debug("mule pre save handler rollback ended");
    }

    public MuleContextManager getMuleContextManager() {
        return muleContextManager;
    }
    public void setMuleContextManager(MuleContextManager muleContextManager) {
        this.muleContextManager = muleContextManager;
    }
}