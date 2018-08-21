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

public class NextPossibleQueuesModel<T, P extends AbstractPipelineContext>
{
    private T businessObject;

    private P pipelineContext;

    private List<String> nextPossibleQueues = new ArrayList<>();

    private String defaultNextQueue;

    private String defaultReturnQueue;

    private String defaultDenyQueue;

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

    public List<String> getNextPossibleQueues()
    {
        return Collections.unmodifiableList(nextPossibleQueues);
    }

    public void setNextPossibleQueues(List<String> nextPossibleQueues)
    {
        List<String> queues = new ArrayList<>();
        queues.addAll(nextPossibleQueues);
        this.nextPossibleQueues = queues;
    }

    public String getDefaultNextQueue()
    {
        return defaultNextQueue;
    }

    public void setDefaultNextQueue(String defaultNextQueue)
    {
        this.defaultNextQueue = defaultNextQueue;
    }

    public String getDefaultReturnQueue()
    {
        return defaultReturnQueue;
    }

    public void setDefaultReturnQueue(String defaultReturnQueue)
    {
        this.defaultReturnQueue = defaultReturnQueue;
    }

    /**
     * @return the defaultDenyQueue
     */
    public String getDefaultDenyQueue()
    {
        return defaultDenyQueue;
    }

    /**
     * @param defaultDenyQueue
     *            the defaultDenyQueue to set
     */
    public void setDefaultDenyQueue(String defaultDenyQueue)
    {
        this.defaultDenyQueue = defaultDenyQueue;
    }

}
