package com.armedia.acm.activiti;

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.AbstractBpmnParseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by dmiller on 3/5/14.
 */
public class ActivitiTaskParseHandler extends AbstractBpmnParseHandler<UserTask>
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private List<ActivitiListener> listenersToAdd;

    @Override
    protected void executeParse(BpmnParse bpmnParse, UserTask element)
    {
        log.info("parse handler called for user task " + element.getName());

        element.getTaskListeners().addAll(getListenersToAdd());

    }

    @Override
    protected Class<? extends BaseElement> getHandledType()
    {
        return UserTask.class;
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
