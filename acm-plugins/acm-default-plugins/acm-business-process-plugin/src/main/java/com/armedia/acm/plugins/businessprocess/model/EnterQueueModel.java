package com.armedia.acm.plugins.businessprocess.model;

/*-
 * #%L
 * ACM Default Plugin: Acm Business Process
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

import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnterQueueModel<T, P extends AbstractPipelineContext>
{

    private T businessObject;

    private P pipelineContext;

    private SystemConfiguration systemConfiguration;

    private List<String> cannotEnterReasons = new ArrayList<>();

    public T getBusinessObject()
    {
        return businessObject;
    }

    public void setBusinessObject(T businessObject)
    {
        this.businessObject = businessObject;
    }

    public P getPipelineContext()
    {
        return pipelineContext;
    }

    public void setPipelineContext(P pipelineContext)
    {
        this.pipelineContext = pipelineContext;
    }

    public List<String> getCannotEnterReasons()
    {
        return Collections.unmodifiableList(cannotEnterReasons);
    }

    public void setCannotEnterReasons(List<String> cannotEnterReasons)
    {
        List<String> reasons = new ArrayList<>();
        reasons.addAll(cannotEnterReasons);
        this.cannotEnterReasons = reasons;
    }

    public void addCannotEnterReason(String reason)
    {
        cannotEnterReasons.add(reason);
    }

    public SystemConfiguration getSystemConfiguration()
    {
        return systemConfiguration;
    }

    public void setSystemConfiguration(SystemConfiguration systemConfiguration)
    {
        this.systemConfiguration = systemConfiguration;
    }
}
