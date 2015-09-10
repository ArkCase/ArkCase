package com.armedia.acm.plugins.ecm.pipeline.presave;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.apache.chemistry.opencmis.client.api.Document;
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
        // rollback not needed, JPA will rollback the database changes.
    }

    public MuleContextManager getMuleContextManager() {
        return muleContextManager;
    }
    public void setMuleContextManager(MuleContextManager muleContextManager) {
        this.muleContextManager = muleContextManager;
    }
}