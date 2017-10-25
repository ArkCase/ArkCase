package com.armedia.acm.plugins.task.listener;

import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by dmiller on 1/14/2017.
 */
public class BuckslipTaskCompletedListener implements TaskListener, JavaDelegate
{
    private BuckslipTaskHelper buckslipTaskHelper = new BuckslipTaskHelper();

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private UserDao userDao;

    /**
     * This method is called when the initiate task is signalled; the method must setup the first current approver.
     *
     * @param delegateExecution
     * @throws Exception
     */
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception
    {
        String futureTasks = (String) delegateExecution.getVariable(TaskConstants.VARIABLE_NAME_BUCKSLIP_FUTURE_TASKS);
        updateProcessVariables(futureTasks, delegateExecution);
    }

    /**
     * This method is called when a user task completes; the method must record the results of the current task,
     * and setup the next task if any.
     *
     * @param delegateTask
     */
    @Override
    public void notify(DelegateTask delegateTask)
    {
        DelegateExecution execution = delegateTask.getExecution();

        String outcome = (String) delegateTask.getVariable("buckslipOutcome");
        log.debug("Task id {} has outcome {}", delegateTask.getId(), outcome);
        Boolean nonConcurEndsApprovals = (Boolean) execution.getVariable(TaskConstants.VARIABLE_NAME_NON_CONCUR_ENDS_APPROVALS);
        log.debug("Non-concur policy is to end approvals? {}", nonConcurEndsApprovals);

        if (nonConcurEndsApprovals && "NON_CONCUR".equals(outcome))
        {
            getBuckslipTaskHelper().resetFutureApproversAfterWithdrawOrNonConcur(execution);
        }
        else
        {
            // first, add the assignee of this task to the past approvers list
            String pastTasks = (String) execution.getVariable(TaskConstants.VARIABLE_NAME_PAST_TASKS);
            String approver = delegateTask.getAssignee();
            String taskName = delegateTask.getName();
            String groupName = (String) delegateTask.getVariable("currentGroup");

            String updatedTasks = addTask(pastTasks, approver, taskName, groupName, outcome);

            execution.setVariable(TaskConstants.VARIABLE_NAME_PAST_TASKS, updatedTasks);
            log.debug("Task ID: {}, past approvers {}", delegateTask.getId(), updatedTasks);

            // next, set the future approver and current approver variables
            String futureTasks = (String) execution.getVariable(TaskConstants.VARIABLE_NAME_BUCKSLIP_FUTURE_TASKS);
            updateProcessVariables(futureTasks, execution);
        }

    }

    private void updateProcessVariables(String futureTasks, DelegateExecution execution)
    {
        log.debug("process {} has future tasks {}", execution.getProcessInstanceId(), futureTasks);

        JSONArray jsonFutureTasks = new JSONArray(futureTasks);

        if (jsonFutureTasks.length() != 0)
        {
            String moreTasks = "true";
            JSONObject futureTask = jsonFutureTasks.getJSONObject(0);

            String currentApprover = futureTask.optString("approverId", "");
            String taskName = futureTask.optString("taskName", "Review");
            String group = futureTask.optString("groupName", "");

            jsonFutureTasks.remove(0);

            execution.setVariable("currentApprover", currentApprover);
            execution.setVariable("currentTaskName", taskName);
            execution.setVariable("currentGroup", group);
            execution.setVariable("moreTasks", moreTasks);
            execution.setVariable(TaskConstants.VARIABLE_NAME_BUCKSLIP_FUTURE_TASKS, jsonFutureTasks.toString());

        }
        else
        {
            execution.setVariable("currentApprover", "");
            execution.setVariable("currentTaskName", "");
            execution.setVariable("moreTasks", "false");
            execution.setVariable(TaskConstants.VARIABLE_NAME_BUCKSLIP_FUTURE_TASKS, "[]");
        }
    }

    private String addTask(String tasksSoFar, String approverId, String taskName, String groupName, String outcome)
    {
        AcmUser user = userDao.findByUserId(approverId);

        JSONArray jsonTasks = new JSONArray(tasksSoFar);
        JSONObject newJsonTask = new JSONObject();

        newJsonTask.put("approverId", approverId);
        if (user != null)
        {
            newJsonTask.put("approverFullName", user.getFullName());
        }

        newJsonTask.put("groupName", groupName);


        ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SearchConstants.SOLR_DATE_FORMAT);
        String approvalDate = formatter.format(date);
        newJsonTask.put("approvalDate", approvalDate);

        newJsonTask.put("approverDecision", outcome);

        newJsonTask.put("taskName", taskName);

        jsonTasks.put(newJsonTask);
        return jsonTasks.toString();
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
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
