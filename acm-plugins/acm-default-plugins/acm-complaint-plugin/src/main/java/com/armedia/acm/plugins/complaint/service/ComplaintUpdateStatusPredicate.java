package com.armedia.acm.plugins.complaint.service;

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

import com.armedia.acm.activiti.AcmBusinessProcessEvent;

import org.apache.commons.collections.Predicate;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Map;

/**
 * Created by armdev on 6/5/14.
 */
public class ComplaintUpdateStatusPredicate implements Predicate
{
    private Logger log = LogManager.getLogger(getClass());

    @Override
    public boolean evaluate(Object object)
    {
        log.debug("type of event: {}", object.getClass().toString());

        if (!(object instanceof AcmBusinessProcessEvent))
        {
            return false;
        }

        AcmBusinessProcessEvent event = (AcmBusinessProcessEvent) object;

        return shouldComplaintStatusBeUpdated(event);
    }

    public boolean shouldComplaintStatusBeUpdated(AcmBusinessProcessEvent event)
    {
        Map<String, Object> processVariables = event.getProcessVariables();

        log.debug("# of process variables: {}", event.getProcessVariables().size());
        if (log.isDebugEnabled())
        {
            processVariables.forEach((key, value) -> log.debug("pvar - {} = {}", key, value));
        }
        return "COMPLAINT".equals(processVariables.get("OBJECT_TYPE"))
                && "cmComplaintWorkflow".equals(processVariables.get("processDefinitionKey"));
    }

}
