package com.armedia.acm.services.buckslip.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmiller on 1/14/2017.
 */
public class TaskCompletedListener implements TaskListener
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void notify(DelegateTask delegateTask)
    {
        DelegateExecution execution = delegateTask.getExecution();

        // first, add the assignee of this task to the past approvers list
        String pastApprovers = (String) execution.getVariable("pastApprovers");
        String approver = delegateTask.getAssignee();
        String updatedApprovers = addApprover(pastApprovers, approver);
        execution.setVariable("pastApprovers", updatedApprovers);
        log.debug("Task ID: {}, past approvers {}", delegateTask.getId(), updatedApprovers);

        // next, set the future approver and current approver variables
        List<String> futureApprovers = (List<String>) execution.getVariable("futureApprovers");
        updateProcessVariables(futureApprovers, delegateTask, execution);

    }

    private void updateProcessVariables(List<String> futureApprovers, DelegateTask task, DelegateExecution execution)
    {
        log.debug("task {} has {} more approvers", task.getId(), futureApprovers.size());

        String moreApprovers;
        String currentApprover;
        if (!futureApprovers.isEmpty())
        {
            moreApprovers = "true";
            currentApprover = futureApprovers.get(0);
            futureApprovers = new ArrayList<>(futureApprovers.subList(1, futureApprovers.size()));

            execution.setVariable("currentApprover", currentApprover);
            execution.setVariable("moreApprovers", moreApprovers);
            execution.setVariable("futureApprovers", futureApprovers);

        } else
        {
            execution.setVariable("currentApprover", "");
            execution.setVariable("moreApprovers", "false");
            execution.setVariable("futureApprovers", new ArrayList<String>());
        }
    }

    private String addApprover(String approvalsSoFar, String approverId)
    {
        JSONArray jsonApprovers = new JSONArray(approvalsSoFar);
        JSONObject newApprover = new JSONObject();
        newApprover.put("approverId", approverId);
        ZonedDateTime date = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        String approvalDate = formatter.format(date);
        newApprover.put("approvalDate", approvalDate);
        jsonApprovers.put(newApprover);
        return jsonApprovers.toString();
    }
}
