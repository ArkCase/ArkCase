package com.armedia.acm.plugins.task.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuckslipWithdrawnListener implements JavaDelegate
{
    private BuckslipTaskHelper buckslipTaskHelper = new BuckslipTaskHelper();

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception
    {
        log.debug("Withdrawing approvals for process id {}, event {}", delegateExecution.getProcessInstanceId(),
                delegateExecution.getEventName());

        if ("start".equals(delegateExecution.getEventName()))
        {
            buckslipTaskHelper.resetFutureApproversAfterWithdrawOrNonConcur(delegateExecution);
        }
    }

    public BuckslipTaskHelper getBuckslipTaskHelper()
    {
        return buckslipTaskHelper;
    }

    public void setBuckslipTaskHelper(BuckslipTaskHelper buckslipTaskHelper)
    {
        this.buckslipTaskHelper = buckslipTaskHelper;
    }
}
