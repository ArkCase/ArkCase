package com.armedia.acm.plugins.consultation.pipeline.postsave;

import com.armedia.acm.plugins.businessprocess.service.StartBusinessProcessService;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationConstants;
import com.armedia.acm.plugins.consultation.model.ConsultationStartBusinessProcessModel;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.consultation.service.ConsultationStartBusinessProcessBusinessRule;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.HashMap;
import java.util.Map;

public class ConsultationStartBusinessProcessIfNeededHandler implements PipelineHandler<Consultation, ConsultationPipelineContext>
{

    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());
    private ConsultationStartBusinessProcessBusinessRule startBusinessProcessBusinessRule;
    private StartBusinessProcessService startBusinessProcessService;
    @PersistenceContext
    private EntityManager em;

    @Override
    public void execute(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.info("Consultation entering ConsultationStartBusinessProcessIfNeededHandler : [{}]", entity);

        em.flush();
        ConsultationStartBusinessProcessModel model = new ConsultationStartBusinessProcessModel();
        model.setBusinessObject(entity);
        model.setPipelineContext(pipelineContext);

        ConsultationStartBusinessProcessModel result = startBusinessProcessBusinessRule.applyRules(model);

        boolean processStarted = result.isStartProcess();
        log.info("Process started [{}]", processStarted);
        log.info("Consultation exiting ConsultationStartBusinessProcessIfNeededHandler : [{}]", entity);

        if (processStarted)
        {
            Map<String, Object> processVariables = new HashMap<>();
            processVariables.put("OBJECT_TYPE", ConsultationConstants.OBJECT_TYPE);
            processVariables.put("OBJECT_ID", entity.getId());
            processVariables.put("NEW_QUEUE_NAME", model.getBusinessObjectNewQueueName());
            processVariables.put("NEW_OBJECT_STATUS", model.getBusinessObjectNewStatus());

            String processName = result.getProcessName();

            getStartBusinessProcessService().startBusinessProcess(processName, processVariables);
        }
    }

    @Override
    public void rollback(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        // TODO Auto-generated method stub

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
