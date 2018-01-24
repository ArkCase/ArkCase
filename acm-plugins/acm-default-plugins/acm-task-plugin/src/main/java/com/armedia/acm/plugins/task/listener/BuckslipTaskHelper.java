package com.armedia.acm.plugins.task.listener;

import com.armedia.acm.plugins.task.model.TaskConstants;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.runtime.ProcessInstance;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class BuckslipTaskHelper
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public void resetFutureApproversAfterWithdrawOrNonConcur(DelegateExecution delegateExecution)
    {
        ProcessInstance pi = delegateExecution.getEngineServices().getRuntimeService().createProcessInstanceQuery()
                .processInstanceId(delegateExecution.getProcessInstanceId()).includeProcessVariables()
                .singleResult();

        log.debug("{} process variables", pi.getProcessVariables().size());
        String pastTasks = (String) pi.getProcessVariables().get("pastTasks");
        JSONArray futureTasks = new JSONArray(((String) pi.getProcessVariables().get("futureTasks")));
        String currentApprover = (String) pi.getProcessVariables().get("currentApprover");
        String currentTaskName = (String) pi.getProcessVariables().get("currentTaskName");
        String currentGroupName = (String) pi.getProcessVariables().get("currentGroup");
        String currentDetails = (String) pi.getProcessVariables().get(TaskConstants.VARIABLE_NAME_DETAILS);
        String currentAddedBy = (String) pi.getProcessVariables().get("addedBy");
        String taskDueDateExpression = (String) pi.getProcessVariables().get("taskDueDateExpression");

        JSONArray newFutureTasks = new JSONArray();

        JSONArray pastApproversJson = new JSONArray(pastTasks);

        boolean foundCurrentInPast = false;

        for (int i = 0; i < pastApproversJson.length(); i++)
        {
            JSONObject past = pastApproversJson.getJSONObject(i);
            String pastApproverId = past.getString("approverId");
            String pastTaskName = past.getString("taskName");
            String pastGroupName = past.getString("groupName");
            String pastDetails = past.getString("details");
            String pastAddedBy = past.getString("addedBy");
            int maxTaskDurationInDays = past.getInt("maxTaskDurationInDays");
            // account for possibly many withdrawal cycles; suppose the same tasks have already been recorded more
            // than once (e.g. Ann has completed her original task, then completed the same task after the first
            // withdraw-and-restart; now Ann will have two entries in the past tasks list; but we only want one new
            // task).
            boolean alreadyInFutureTasks = false;
            for (int j = 0; j < newFutureTasks.length(); j++)
            {
                JSONObject someFutureTask = newFutureTasks.getJSONObject(j);
                // note, the due date may be different, so we shouldn't consider the due date.
                if (Objects.equals(pastApproverId, someFutureTask.getString("approverId")) &&
                        Objects.equals(pastTaskName, someFutureTask.getString("taskName")))
                {
                    alreadyInFutureTasks = true;
                    break;
                }
            }
            if (!alreadyInFutureTasks)
            {
                JSONObject newFuture = new JSONObject();
                newFuture.put("approverId", pastApproverId);
                newFuture.put("taskName", pastTaskName);
                newFuture.put("groupName", pastGroupName);
                newFuture.put("details", pastDetails);
                newFuture.put("addedBy", pastAddedBy);
                newFuture.put("maxTaskDurationInDays", maxTaskDurationInDays);
                newFutureTasks.put(newFuture);
            }

            if (Objects.equals(pastApproverId, currentApprover) && Objects.equals(pastTaskName, currentTaskName))
            {
                foundCurrentInPast = true;
            }
        }

        if (!foundCurrentInPast)
        {
            JSONObject currentTask = new JSONObject();
            currentTask.put("approverId", currentApprover);
            currentTask.put("taskName", currentTaskName);
            currentTask.put("groupName", currentGroupName);
            currentTask.put("details", currentDetails);
            currentTask.put("addedBy", currentAddedBy);
            int maxTaskDurationInDays = getMaxTaskDurationInDays(taskDueDateExpression);
            currentTask.put("maxTaskDurationInDays", maxTaskDurationInDays);
            newFutureTasks.put(currentTask);
        }

        for (int i = 0; i < futureTasks.length(); i++)
        {
            newFutureTasks.put(futureTasks.get(i));
        }

        log.debug("past: {}, current: {}, future: {}", pastTasks, currentApprover, futureTasks);

        // when the approval cycle is restarted, everyone has to approve again
        delegateExecution.getEngineServices().getRuntimeService().setVariable(pi.getProcessInstanceId(), "futureTasks",
                newFutureTasks.toString());
    }

    public int getMaxTaskDurationInDays(String taskDueDateExpression)
    {
        // we can assume the due date expression is "P(some number)D", or more generally, remove the first and last
        // char to get the number
        String lostFirstChar = taskDueDateExpression.substring(1);
        String lostSecondChar = lostFirstChar.substring(0, lostFirstChar.length() - 1);
        log.debug("max task duration in days: {}", lostSecondChar);
        return Integer.valueOf(lostSecondChar);
    }
}
