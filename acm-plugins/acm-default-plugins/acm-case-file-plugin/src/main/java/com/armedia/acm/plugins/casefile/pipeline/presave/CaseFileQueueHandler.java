package com.armedia.acm.plugins.casefile.pipeline.presave;

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

import com.armedia.acm.plugins.casefile.dao.AcmQueueDao;
import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Created by armdev on 9/1/15.
 */
public class CaseFileQueueHandler implements PipelineHandler<CaseFile, CaseFilePipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private AcmQueueDao acmQueueDao;

    @Override
    public void execute(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        String queueName = pipelineContext.getEnqueueName();
        AcmQueue queue = getAcmQueueDao().findByName(queueName);

        entity.setQueue(queue);

        entity.setStatus("ACTIVE");

        log.debug("Set case file queue to {}", queue.getName());
    }

    @Override
    public void rollback(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        // rollback not needed, JPA will rollback the database changes.
    }

    public AcmQueueDao getAcmQueueDao()
    {
        return acmQueueDao;
    }

    public void setAcmQueueDao(AcmQueueDao acmQueueDao)
    {
        this.acmQueueDao = acmQueueDao;
    }
}
