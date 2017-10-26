package com.armedia.acm.plugins.task.listener;

import com.armedia.acm.plugins.task.model.TaskConstants;
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
        delegateExecution.setVariable(TaskConstants.VARIABLE_NAME_IS_BUCKSLIP_WORKFLOW, Boolean.TRUE);

        // initialize past approvers to "[]" (JSON empty array)
        delegateExecution.setVariable(TaskConstants.VARIABLE_NAME_PAST_TASKS, "[]");

        // set nonConcurEndsApprovals to a default value if necessary
        if (!delegateExecution.hasVariable(TaskConstants.VARIABLE_NAME_NON_CONCUR_ENDS_APPROVALS))
        {
            delegateExecution.setVariable(TaskConstants.VARIABLE_NAME_NON_CONCUR_ENDS_APPROVALS, Boolean.FALSE);
        }

        LOG.debug("Starting a buckslip task with {} variables", delegateExecution.getVariables().size());
    }

}
