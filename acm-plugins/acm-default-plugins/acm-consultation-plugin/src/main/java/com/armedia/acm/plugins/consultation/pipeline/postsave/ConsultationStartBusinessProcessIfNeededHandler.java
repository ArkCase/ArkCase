package com.armedia.acm.plugins.consultation.pipeline.postsave;

/*-
 * #%L
 * ACM Default Plugin: Consultation
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 *
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
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

    public ConsultationStartBusinessProcessBusinessRule getStartBusinessProcessBusinessRule()
    {
        return startBusinessProcessBusinessRule;
    }

    public void setStartBusinessProcessBusinessRule(ConsultationStartBusinessProcessBusinessRule startBusinessProcessBusinessRule)
    {
        this.startBusinessProcessBusinessRule = startBusinessProcessBusinessRule;
    }
}
