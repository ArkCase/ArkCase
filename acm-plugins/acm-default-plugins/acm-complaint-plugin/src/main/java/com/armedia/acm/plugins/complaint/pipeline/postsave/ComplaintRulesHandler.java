package com.armedia.acm.plugins.complaint.pipeline.postsave;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.ComplaintPipelineContext;
import com.armedia.acm.plugins.complaint.service.SaveComplaintBusinessRule;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Apply business rules to a Complaint.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 12.08.2015.
 */
public class ComplaintRulesHandler implements PipelineHandler<Complaint, ComplaintPipelineContext>
{
    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());
    /**
     * Business rule manager.
     */
    private SaveComplaintBusinessRule saveRule;

    @Override
    public void execute(Complaint entity, ComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.info("Complaint entering ComplaintRulesHandler : [{}]", entity);

        entity = saveRule.applyRules(entity);

        log.info("Complaint exiting ComplaintRulesHandler : [{}]", entity);

    }

    @Override
    public void rollback(Complaint entity, ComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public SaveComplaintBusinessRule getSaveRule()
    {
        return saveRule;
    }

    public void setSaveRule(SaveComplaintBusinessRule saveRule)
    {
        this.saveRule = saveRule;
    }
}
