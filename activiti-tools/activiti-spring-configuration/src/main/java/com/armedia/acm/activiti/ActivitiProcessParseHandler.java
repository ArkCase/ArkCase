package com.armedia.acm.activiti;

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.AbstractBpmnParseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by armdev on 5/30/14.
 */
public class ActivitiProcessParseHandler extends AbstractBpmnParseHandler<org.activiti.bpmn.model.Process>
{
    private List<ActivitiListener> listenersToAdd;
    private Logger log = LoggerFactory.getLogger(getClass());

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
