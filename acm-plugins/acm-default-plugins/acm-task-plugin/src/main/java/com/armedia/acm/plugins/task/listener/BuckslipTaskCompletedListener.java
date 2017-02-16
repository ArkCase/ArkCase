package com.armedia.acm.plugins.task.listener;

import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmiller on 1/14/2017.
 */
public class BuckslipTaskCompletedListener implements TaskListener
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private UserDao userDao;


    @Override
    public void notify(DelegateTask delegateTask)
    {
        DelegateExecution execution = delegateTask.getExecution();

        String outcome = (String) delegateTask.getVariable("buckslipOutcome");
        log.debug("Task id {} has outcome {}", delegateTask.getId(), outcome);

        // first, add the assignee of this task to the past approvers list
        String pastApprovers = (String) execution.getVariable(TaskConstants.VARIABLE_NAME_PAST_APPROVERS);
        String approver = delegateTask.getAssignee();
        String updatedApprovers = addApprover(pastApprovers, approver, outcome);
        execution.setVariable(TaskConstants.VARIABLE_NAME_PAST_APPROVERS, updatedApprovers);
        log.debug("Task ID: {}, past approvers {}", delegateTask.getId(), updatedApprovers);

        // next, set the future approver and current approver variables
        List<String> futureApprovers = (List<String>) execution.getVariable("futureApprovers");
        updateProcessVariables(futureApprovers, delegateTask, execution);

    }

    private void updateProcessVariables(List<String> futureApprovers, DelegateTask task, DelegateExecution execution)
    {
        log.debug("task {} has {} more approvers", task.getId(), futureApprovers == null ? 0 : futureApprovers.size());

        if (futureApprovers != null && !futureApprovers.isEmpty())
        {
            String moreApprovers = "true";
            String currentApprover = futureApprovers.get(0);

            // Activiti throws an exception if we send the subList itself, so we have to create a whole new
            // list based on the subList.
            futureApprovers = new ArrayList<>(futureApprovers.subList(1, futureApprovers.size()));

            execution.setVariable("currentApprover", currentApprover);
            execution.setVariable("moreApprovers", moreApprovers);
            execution.setVariable(TaskConstants.VARIABLE_NAME_BUCKSLIP_FUTURE_APPROVERS, futureApprovers);

        } else
        {
            execution.setVariable("currentApprover", "");
            execution.setVariable("moreApprovers", "false");
            execution.setVariable(TaskConstants.VARIABLE_NAME_BUCKSLIP_FUTURE_APPROVERS, new ArrayList<String>());
        }
    }

    private String addApprover(String approvalsSoFar, String approverId, String outcome)
    {
        AcmUser user = userDao.findByUserId(approverId);

        JSONArray jsonApprovers = new JSONArray(approvalsSoFar);
        JSONObject newApprover = new JSONObject();

        newApprover.put("approverId", approverId);
        if (user != null)
        {
            newApprover.put("approverFullName", user.getFullName());
        }


        ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SearchConstants.SOLR_DATE_FORMAT);
        String approvalDate = formatter.format(date);
        newApprover.put("approvalDate", approvalDate);

        newApprover.put("approverDecision", outcome);

        jsonApprovers.put(newApprover);
        return jsonApprovers.toString();
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}
