package com.armedia.acm.plugins.task.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * Created by dmiller on 1/16/2017.
 */
public class BuckslipWorkflowStartedListener implements ExecutionListener
{
    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception
    {
        delegateExecution.setVariable("isBuckslipWorkflow", Boolean.TRUE);
    }
}
