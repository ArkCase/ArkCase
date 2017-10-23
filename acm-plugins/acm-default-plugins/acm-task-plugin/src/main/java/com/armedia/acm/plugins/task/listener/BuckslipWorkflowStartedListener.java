package com.armedia.acm.plugins.task.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dmiller on 1/16/2017.
 */
public class BuckslipWorkflowStartedListener implements ExecutionListener
{
    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception
    {
        delegateExecution.setVariable("isBuckslipWorkflow", Boolean.TRUE);

        // initialize past approvers to "[]" (JSON empty array)
        delegateExecution.setVariable("pastTasks", "[]");
    }

}
