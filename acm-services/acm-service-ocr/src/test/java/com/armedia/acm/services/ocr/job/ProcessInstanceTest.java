package com.armedia.acm.services.ocr.job;

/*-
 * #%L
 * ACM Services: Optical character recognition via Tesseract
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import org.activiti.engine.runtime.ProcessInstance;

import java.util.Map;

public class ProcessInstanceTest implements ProcessInstance
{
    private String id;
    private Map<String, Object> processVariables;

    public ProcessInstanceTest(String id, Map<String, Object> processVariables)
    {
        this.id = id;
        this.processVariables = processVariables;
    }

    @Override
    public String getProcessDefinitionId()
    {
        return null;
    }

    @Override
    public String getBusinessKey()
    {
        return null;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public boolean isSuspended()
    {
        return false;
    }

    @Override
    public boolean isEnded()
    {
        return false;
    }

    @Override
    public String getActivityId()
    {
        return null;
    }

    @Override
    public String getProcessInstanceId()
    {
        return null;
    }

    @Override
    public String getParentId()
    {
        return null;
    }

    @Override
    public Map<String, Object> getProcessVariables()
    {
        return processVariables;
    }

    @Override
    public String getTenantId()
    {
        return null;
    }
}
