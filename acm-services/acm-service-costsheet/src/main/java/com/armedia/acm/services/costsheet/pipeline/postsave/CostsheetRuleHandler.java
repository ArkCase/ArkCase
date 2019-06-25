package com.armedia.acm.services.costsheet.pipeline.postsave;

/*-
 * #%L
 * ACM Service: Costsheet
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

import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.pipeline.CostsheetPipelineContext;
import com.armedia.acm.services.costsheet.service.SaveCostsheetBusinessRule;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CostsheetRuleHandler implements PipelineHandler<AcmCostsheet, CostsheetPipelineContext>
{

    private final Logger log = LogManager.getLogger(getClass());
    private SaveCostsheetBusinessRule costsheetBusinessRule;

    @Override
    public void execute(AcmCostsheet entity, CostsheetPipelineContext ctx) throws PipelineProcessException
    {
        // apply costsheet business rules after save
        log.info("Costsheet with id [{}] and title [{}] entering CostsheetRulesHandler", entity.getId(), entity.getTitle());

        entity = costsheetBusinessRule.applyRules(entity);

        log.info("Costsheet with id [{}] and title [{}] exiting CostsheetRulesHandler", entity.getId(), entity.getTitle());
    }

    @Override
    public void rollback(AcmCostsheet entity, CostsheetPipelineContext ctx) throws PipelineProcessException
    {
        // nothing to execute on rollback
    }

    public void setCostsheetBusinessRule(SaveCostsheetBusinessRule costsheetBusinessRule)
    {
        this.costsheetBusinessRule = costsheetBusinessRule;
    }
}
