package com.armedia.acm.activiti;

/*-
 * #%L
 * Tool Integrations: Activiti Configuration
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

import com.armedia.acm.core.model.AcmEvent;

import org.activiti.engine.runtime.ProcessInstance;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Date;
import java.util.Map;

/**
 * Created by armdev on 5/30/14.
 */
public class AcmBusinessProcessEvent extends AcmEvent
{
    private ProcessInstance source;
    private Map<String, Object> processVariables;

    private Logger log = LogManager.getLogger(getClass());

    public AcmBusinessProcessEvent(ProcessInstance source)
    {
        super(source);

        setSucceeded(true);
        setObjectType("BUSINESS_PROCESS");
        setObjectId(Long.valueOf(source.getProcessInstanceId()));
        setEventDate(new Date());
        setUserId("ACTIVITI_SYSTEM");
        if (source.getProcessVariables() != null && source.getProcessVariables().containsKey("IP_ADDRESS"))
        {
            setIpAddress((String) source.getProcessVariables().get("IP_ADDRESS"));
        }
        log.debug("# of process variables: " + source.getProcessVariables().size());
        setEventProperties(source.getProcessVariables());

        // do not put the ProcessInstance in the event source... since
        // the ProcessInstance is not Serializable, so it can't be sent to a
        // JMS queue. // setSource(source);
    }

    @Override
    public ProcessInstance getSource()
    {
        return source;
    }

    public void setSource(ProcessInstance source)
    {
        this.source = source;
    }

    public Map<String, Object> getProcessVariables()
    {
        return processVariables;
    }

    public void setProcessVariables(Map<String, Object> processVariables)
    {
        this.processVariables = processVariables;
    }
}
