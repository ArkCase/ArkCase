package com.armedia.acm.services.transcribe.pipline.postsave;

import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeActionType;
import com.armedia.acm.services.transcribe.model.TranscribeBusinessProcessModel;
import com.armedia.acm.services.transcribe.pipline.TranscribePipelineContext;
import com.armedia.acm.services.transcribe.rules.TranscribeBusinessProcessRulesExecutor;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;
import com.armedia.acm.services.transcribe.service.TranscribeEventPublisher;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/06/2018
 */
public class TranscribeBusinessProcessHandler implements PipelineHandler<Transcribe, TranscribePipelineContext>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private ArkCaseTranscribeService arkCaseTranscribeService;
    private RuntimeService activitiRuntimeService;
    private TranscribeEventPublisher transcribeEventPublisher;

    @Override
    public void execute(Transcribe entity, TranscribePipelineContext pipelineContext) throws PipelineProcessException
    {
        LOG.debug("Transcribe entering TranscribeBusinessProcessHandler : [{}]", entity);

        ProcessInstance processInstance = getArkCaseTranscribeService().startBusinessProcess(entity);
        if (processInstance != null)
        {
            pipelineContext.setProcessId(processInstance.getId());
        }

        LOG.debug("Transcribe leaving TranscribeBusinessProcessHandler : [{}]", entity);
    }

    @Override
    public void rollback(Transcribe entity, TranscribePipelineContext pipelineContext) throws PipelineProcessException
    {
        // Stop the process is started during execute
        if (pipelineContext != null && StringUtils.isNotEmpty(pipelineContext.getProcessId()))
        {
            getActivitiRuntimeService().deleteProcessInstance(pipelineContext.getProcessId(), "Pipeline rollback action.");
        }

        getTranscribeEventPublisher().publish(entity, TranscribeActionType.ROLLBACK.toString());
    }

    public ArkCaseTranscribeService getArkCaseTranscribeService()
    {
        return arkCaseTranscribeService;
    }

    public void setArkCaseTranscribeService(ArkCaseTranscribeService arkCaseTranscribeService)
    {
        this.arkCaseTranscribeService = arkCaseTranscribeService;
    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }

    public TranscribeEventPublisher getTranscribeEventPublisher()
    {
        return transcribeEventPublisher;
    }

    public void setTranscribeEventPublisher(TranscribeEventPublisher transcribeEventPublisher)
    {
        this.transcribeEventPublisher = transcribeEventPublisher;
    }
}
