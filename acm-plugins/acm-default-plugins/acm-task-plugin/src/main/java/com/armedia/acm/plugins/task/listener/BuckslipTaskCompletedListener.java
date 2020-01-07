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

import com.armedia.acm.plugins.task.model.BuckslipProcessStateEvent;
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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by dmiller on 1/14/2017.
 */
public class BuckslipTaskCompletedListener implements TaskListener, JavaDelegate
{

    private transient final Logger log = LogManager.getLogger(getClass());

    private UserDao userDao;
    private BuckslipTaskHelper buckslipTaskHelper;

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
        getBuckslipTaskHelper().notifyBuckslipProcessStateChanged(delegateExecution,
                BuckslipProcessStateEvent.BuckslipProcessState.INITIALIZED);
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
            String taskDueDateExpression = (String) delegateTask.getVariable("taskDueDateExpression");
            String details = (String) delegateTask.getVariable(TaskConstants.VARIABLE_NAME_DETAILS);
            String addedBy = (String) delegateTask.getVariable("addedBy");

            String updatedTasks = addTask(pastTasks, approver, taskName, details, addedBy, groupName, taskDueDateExpression, outcome);

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
            String approverFullName = futureTask.optString("approverFullName", "");
            String taskName = futureTask.optString("taskName", "Review");
            String group = futureTask.optString("groupName", "");
            String details = futureTask.optString("details", "");
            String addedBy = futureTask.optString("addedBy", "");
            String addedByFullName = futureTask.optString("addedByFullName", "");
            int maxTaskDurationInDays = futureTask.optInt("maxTaskDurationInDays", 3);
            String taskDueDateExpression = String.format("P%sD", maxTaskDurationInDays);

            log.debug("task due date expression: {}", taskDueDateExpression);

            jsonFutureTasks.remove(0);

            execution.setVariable("currentApprover", currentApprover);
            execution.setVariable("approverFullName", approverFullName);
            execution.setVariable("currentTaskName", taskName);
            execution.setVariable("currentGroup", group);
            execution.setVariable(TaskConstants.VARIABLE_NAME_DETAILS, details);
            execution.setVariable("addedBy", addedBy);
            execution.setVariable("addedByFullName", addedByFullName);
            execution.setVariable("moreTasks", moreTasks);
            execution.setVariable("taskDueDateExpression", taskDueDateExpression);
            execution.setVariable(TaskConstants.VARIABLE_NAME_BUCKSLIP_FUTURE_TASKS, jsonFutureTasks.toString());

        }
        else
        {
            execution.setVariable("currentApprover", "");
            execution.setVariable("approverFullName", "");
            execution.setVariable("currentTaskName", "");
            execution.setVariable("currentGroup", "");
            execution.setVariable(TaskConstants.VARIABLE_NAME_DETAILS, "");
            execution.setVariable("addedBy", "");
            execution.setVariable("addedByFullName", "");
            execution.setVariable("moreTasks", "false");
            execution.setVariable(TaskConstants.VARIABLE_NAME_BUCKSLIP_FUTURE_TASKS, "[]");
        }
    }

    private String addTask(String tasksSoFar, String approverId, String taskName, String details, String addedBy, String groupName,
            String taskDueDateExpression, String outcome)
    {
        AcmUser user = userDao.findByUserId(approverId);

        JSONArray jsonTasks = new JSONArray(tasksSoFar);
        JSONObject newJsonTask = new JSONObject();

        newJsonTask.put("approverId", approverId);
        if (user != null)
        {
            newJsonTask.put("approverFullName", user.getFullName());
        }

        AcmUser addByUser = userDao.findByUserId(addedBy);
        if (addByUser != null)
        {
            newJsonTask.put("addedByFullName", addByUser.getFullName());
        }

        newJsonTask.put("groupName", groupName);

        ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SearchConstants.SOLR_DATE_FORMAT);
        String approvalDate = formatter.format(date);
        newJsonTask.put("approvalDate", approvalDate);

        newJsonTask.put("approverDecision", outcome);

        newJsonTask.put("taskName", taskName);
        newJsonTask.put("details", details);
        newJsonTask.put("addedBy", addedBy);
        int maxTaskDurationInDays = getBuckslipTaskHelper().getMaxTaskDurationInDays(taskDueDateExpression);

        newJsonTask.put("maxTaskDurationInDays", maxTaskDurationInDays);

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
