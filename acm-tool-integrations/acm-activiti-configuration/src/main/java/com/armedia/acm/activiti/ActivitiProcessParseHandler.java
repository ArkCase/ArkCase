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

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.AbstractBpmnParseHandler;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.List;

/**
 * Created by armdev on 5/30/14.
 */
public class ActivitiProcessParseHandler extends AbstractBpmnParseHandler<org.activiti.bpmn.model.Process>
{
    private List<ActivitiListener> listenersToAdd;
    private Logger log = LogManager.getLogger(getClass());

    @Override
    protected void executeParse(BpmnParse bpmnParse, org.activiti.bpmn.model.Process element)
    {
        log.info("parse handler called for Process " + element.getName());

        element.getExecutionListeners().addAll(getListenersToAdd());
    }

    @Override
    protected Class<? extends BaseElement> getHandledType()
    {
        return org.activiti.bpmn.model.Process.class;
    }

    public List<ActivitiListener> getListenersToAdd()
    {
        return listenersToAdd;
    }

    public void setListenersToAdd(List<ActivitiListener> listenersToAdd)
    {
        this.listenersToAdd = listenersToAdd;
    }

}
