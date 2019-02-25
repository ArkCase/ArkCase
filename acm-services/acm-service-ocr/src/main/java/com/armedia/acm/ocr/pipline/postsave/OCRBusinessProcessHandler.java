package com.armedia.acm.ocr.pipline.postsave;

/*-
 * #%L
 * ACM Service: OCR
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

import com.armedia.acm.ocr.model.OCR;
import com.armedia.acm.ocr.model.OCRActionType;
import com.armedia.acm.ocr.pipline.OCRPipelineContext;
import com.armedia.acm.ocr.service.ArkCaseOCRService;
import com.armedia.acm.ocr.service.OCREventPublisher;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class OCRBusinessProcessHandler implements PipelineHandler<OCR, OCRPipelineContext>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private ArkCaseOCRService arkCaseOCRService;
    private RuntimeService activitiRuntimeService;
    private OCREventPublisher ocrEventPublisher;

    @Override
    public void execute(OCR entity, OCRPipelineContext pipelineContext) throws PipelineProcessException
    {
        LOG.debug("OCR entering OCRBusinessProcessHandler : [{}]", entity);

        ProcessInstance processInstance = getArkCaseOCRService().startBusinessProcess(entity);
        if (processInstance != null)
        {
            pipelineContext.setProcessId(processInstance.getId());
        }

        LOG.debug("OCR leaving OCRBusinessProcessHandler : [{}]", entity);
    }

    @Override
    public void rollback(OCR entity, OCRPipelineContext pipelineContext) throws PipelineProcessException
    {
        // Stop the process is started during execute
        if (pipelineContext != null && StringUtils.isNotEmpty(pipelineContext.getProcessId()))
        {
            getActivitiRuntimeService().deleteProcessInstance(pipelineContext.getProcessId(), "Pipeline rollback action.");
        }

        getOcrEventPublisher().publish(entity, OCRActionType.ROLLBACK.toString());
    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }

    public ArkCaseOCRService getArkCaseOCRService()
    {
        return arkCaseOCRService;
    }

    public void setArkCaseOCRService(ArkCaseOCRService arkCaseOCRService)
    {
        this.arkCaseOCRService = arkCaseOCRService;
    }

    public OCREventPublisher getOcrEventPublisher()
    {
        return ocrEventPublisher;
    }

    public void setOcrEventPublisher(OCREventPublisher ocrEventPublisher)
    {
        this.ocrEventPublisher = ocrEventPublisher;
    }
}
