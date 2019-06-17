package com.armedia.acm.plugins.task.listener;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class BuckslipWithdrawnListener implements JavaDelegate
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private BuckslipTaskHelper buckslipTaskHelper;

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
