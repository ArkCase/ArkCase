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
public class AcmParseHandler extends AbstractBpmnParseHandler<UserTask>
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private List<ActivitiListener> listenersToAdd;

    @Override
    protected void executeParse(BpmnParse bpmnParse, UserTask element)
    {
        log.info("parse handler called for user task " + element.getName());


//        ActivitiListener listener = new ActivitiListener();
//        listener.setEvent("start");
//        listener.setImplementationType("expression");
//        listener.setImplementation("${cmMuleActivitiListener.sendMessage('vm://activiti-flow', 'From Parser!!!', 'bandResults', execution)}");

//        element.getExecutionListeners().addAll(getListenersToAdd());
//
//        List<ActivitiListener> executionListeners = element.getExecutionListeners();
//
//        log.info("# of listeners now: " + executionListeners.size());

//        String taskDefKey = element.getId();
//        TaskDefinition taskDef = ( (ProcessDefinitionEntity) bpmnParse.getCurrentScope()
//                .getProcessDefinition() ).getTaskDefinitions().get(taskDefKey);
//        Assert.notNull(taskDef);
        element.getTaskListeners().addAll(getListenersToAdd());
//        taskDef.addTaskListener(
//                TaskListener.EVENTNAME_ALL_EVENTS,
//                new ExpressionTaskListener(
//                        new FixedValue("${cmMuleActivitiListener.sendMessage('vm://activiti-flow', 'From Parser Task Listener!!!', 'bandResults', execution)}")));
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
