package com.armedia.acm.plugins.casefile.pipeline.postsave;

import com.armedia.acm.plugins.businessprocess.service.StartBusinessProcessService;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileStartBusinessProcessModel;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.service.CaseFileStartBusinessProcessBusinessRule;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CaseFileStartBusinessProcessIfNeededHandler implements PipelineHandler<CaseFile, CaseFilePipelineContext>
{

    private CaseFileStartBusinessProcessBusinessRule startBusinessProcessBusinessRule;

    private StartBusinessProcessService startBusinessProcessService;

    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        log.info("CaseFile entering CaseFileStartBusinessProcessIfNeededHandler : [{}]", entity);

        CaseFileStartBusinessProcessModel model = new CaseFileStartBusinessProcessModel();
        model.setBusinessObject(entity);
        model.setPipelineContext(pipelineContext);

        CaseFileStartBusinessProcessModel result = startBusinessProcessBusinessRule.applyRules(model);

        boolean processStarted = result.isStartProcess();
        log.info("Process started [{}]", processStarted);
        log.info("CaseFile exiting CaseFileStartBusinessProcessIfNeededHandler : [{}]", entity);

        if (processStarted)
        {
            Map<String, Object> processVariables = new HashMap<>();
            processVariables.put("OBJECT_TYPE", "CASE_FILE");
            processVariables.put("OBJECT_ID", entity.getId());
            processVariables.put("NEW_QUEUE_NAME", model.getBusinessObjectNewQueueName());
            processVariables.put("NEW_OBJECT_STATUS", model.getBusinessObjectNewStatus());

            String processName = result.getProcessName();

            getStartBusinessProcessService().startBusinessProcess(processName, processVariables);
        }
    }

    @Override
    public void rollback(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        // TODO Auto-generated method stub

    }

    public CaseFileStartBusinessProcessBusinessRule getStartBusinessProcessBusinessRule()
    {
        return startBusinessProcessBusinessRule;
    }

    public void setStartBusinessProcessBusinessRule(CaseFileStartBusinessProcessBusinessRule startBusinessProcessBusinessRule)
    {
        this.startBusinessProcessBusinessRule = startBusinessProcessBusinessRule;
    }

    public StartBusinessProcessService getStartBusinessProcessService()
    {
        return startBusinessProcessService;
    }

    public void setStartBusinessProcessService(StartBusinessProcessService startBusinessProcessService)
    {
        this.startBusinessProcessService = startBusinessProcessService;
    }
}
