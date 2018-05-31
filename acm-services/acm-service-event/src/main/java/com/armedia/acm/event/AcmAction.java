package com.armedia.acm.event;

/*-
 * #%L
 * ACM Service: Events
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

import java.util.List;

/**
 * An action that could be triggered in response to an AcmEvent. Actions could include: start an Activiti workflow,
 * send an e-mail, split a document. Actions are implemented in appropriate functional libraries, e.g., the action to
 * start a workflow is implemented in an activiti-workflow library; this library also implements the Mule flow that
 * will actually implement the action. Actions are (usually) triggered from the library that raises the event; e.g.,
 * the complaint plugin raises the Complaint Created event, and it also registers an EventResponse object that says to
 * launch the complaint workflow. However, EventResponses can be added to any module; this allows a customer-specific
 * module to register an EventResponse for a built-in event.
 */
public class AcmAction
{
    private String actionName;
    private String actionId;
    private List<String> parameters;
    private String targetMuleEndpoint;

    public String getActionName()
    {
        return actionName;
    }

    public void setActionName(String actionName)
    {
        this.actionName = actionName;
    }

    public String getActionId()
    {
        return actionId;
    }

    public void setActionId(String actionId)
    {
        this.actionId = actionId;
    }

    public List<String> getParameters()
    {
        return parameters;
    }

    public void setParameters(List<String> parameters)
    {
        this.parameters = parameters;
    }

    public String getTargetMuleEndpoint()
    {
        return targetMuleEndpoint;
    }

    public void setTargetMuleEndpoint(String targetMuleEndpoint)
    {
        this.targetMuleEndpoint = targetMuleEndpoint;
    }
}
