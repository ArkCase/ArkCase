package com.armedia.acm.plugins.casefile.pipeline.postsave;

/*-
 * #%L
 * ACM Default Plugin: Case File
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileStartBusinessProcessModel;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.service.CaseFileStartBusinessProcessBusinessRule;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.Map;

public class CaseFileStartBusinessProcessIfNeededHandler implements PipelineHandler<CaseFile, CaseFilePipelineContext>
{

    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());
    private CaseFileStartBusinessProcessBusinessRule startBusinessProcessBusinessRule;
    private StartBusinessProcessService startBusinessProcessService;
    @PersistenceContext
    private EntityManager em;

    @Override
    public void execute(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        log.info("CaseFile entering CaseFileStartBusinessProcessIfNeededHandler : [{}]", entity);

        em.flush();
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
