package com.armedia.acm.plugins.casefile.service;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.plugins.businessprocess.model.EnterQueueModel;
import com.armedia.acm.plugins.businessprocess.model.LeaveCurrentQueueModel;
import com.armedia.acm.plugins.businessprocess.model.NextPossibleQueuesModel;
import com.armedia.acm.plugins.businessprocess.model.OnEnterQueueModel;
import com.armedia.acm.plugins.businessprocess.model.OnLeaveQueueModel;
import com.armedia.acm.plugins.businessprocess.model.SystemConfiguration;
import com.armedia.acm.plugins.businessprocess.service.QueueService;
import com.armedia.acm.plugins.businessprocess.service.StartBusinessProcessService;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.plugins.casefile.web.api.CaseFileEnqueueResponse;
import com.armedia.acm.plugins.casefile.web.api.CaseFileEnqueueResponse.ErrorReason;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.services.timesheet.service.TimesheetService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnqueueCaseFileServiceImpl implements EnqueueCaseFileService
{
    private final Logger log = LogManager.getLogger(getClass());

    private CaseFileDao caseFileDao;

    private LeaveCurrentQueueBusinessRule leaveCurrentQueueBusinessRule;

    private CaseFileNextPossibleQueuesBusinessRule caseFileNextPossibleQueuesBusinessRule;

    private EnterQueueBusinessRule enterQueueBusinessRule;

    private OnLeaveQueueBusinessRule onLeaveQueueBusinessRule;

    private OnEnterQueueBusinessRule onEnterQueueBusinessRule;

    private QueueService queueService;

    private StartBusinessProcessService startBusinessProcessService;

    private SaveCaseFileBusinessRule saveCaseFileBusinessRule;

    private AcmObjectLockService acmObjectLockService;

    private SystemConfiguration systemConfiguration;

    private SystemConfigurationService systemConfigurationService;

    private CaseFileEventUtility caseFileEventUtility;

    private TimesheetService timesheetService;

    @Override
    @Transactional
    public CaseFileEnqueueResponse enqueueCaseFile(Long caseId, String nextQueue, CaseFilePipelineContext context)
    {
        return enqueueCaseFile(caseId, nextQueue, null, context);
    }

    @Override
    @Transactional
    public CaseFileEnqueueResponse enqueueCaseFile(Long caseId, String nextQueue, String nextQueueAction, CaseFilePipelineContext context)
    {
        // since we will make changes to this CaseFile, we should not detach it; the caseFileDao detaches
        // the object, so we won't use the dao.find() method here.
        CaseFile caseFile = getCaseFileDao().getEm().find(CaseFile.class, caseId);

        Boolean oldDeniedFlag = caseFile.getDeniedFlag();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AcmAuthenticationDetails details = (AcmAuthenticationDetails) auth.getDetails();
        String ipAddress = details.getRemoteAddress();

        boolean hasAnyAssociatedTimesheets = getTimesheetService().getByObjectIdAndType(
                caseId, CaseFileConstants.OBJECT_TYPE, 0, 1, "") != null;
        caseFile.setHasAnyAssociatedTimesheets(hasAnyAssociatedTimesheets);

        if (nextQueueAction != null && nextQueueAction.equals("Deny"))
        {
            caseFile.setDeniedFlag(true);
            caseFileEventUtility.raiseEvent(caseFile, "denied", new Date(), ipAddress, auth.getName(), auth);
        }

        context.setNewCase(false);
        context.setEnqueueName(nextQueue);

        List<String> cannotLeaveReasons = verifyLeaveConditions(context, caseFile);
        if (!cannotLeaveReasons.isEmpty())
        {
            caseFile.setDeniedFlag(oldDeniedFlag);
            return new CaseFileEnqueueResponse(ErrorReason.LEAVE, cannotLeaveReasons, nextQueue, caseFile);
        }

        List<String> nextPossibleQueues = verifyNextPossibleQueues(context, caseFile);
        if (nextPossibleQueues.isEmpty() || !nextPossibleQueues.contains(nextQueue))
        {
            List<String> errorList = null;
            if (nextPossibleQueues.isEmpty())
            {
                errorList = Arrays.asList(String.format("From the %s queue, it is not possible to move to any other queue.", nextQueue));
            }
            else if (!nextPossibleQueues.contains(nextQueue))
            {
                errorList = Arrays.asList(
                        String.format("From the %s queue, it is not possible to move to the %s queue.", nextQueue, nextPossibleQueues));
            }
            caseFile.setDeniedFlag(oldDeniedFlag);
            return new CaseFileEnqueueResponse(ErrorReason.NEXT_POSSIBLE, errorList, nextQueue, caseFile);
        }

        List<String> cannotEnterReasons = verifyNextConditions(context, caseFile);
        if (!cannotEnterReasons.isEmpty())
        {
            caseFile.setDeniedFlag(oldDeniedFlag);
            return new CaseFileEnqueueResponse(ErrorReason.ENTER, cannotEnterReasons, nextQueue, caseFile);
        }

        startLeaveProcess(context, caseFile);
        startEnterProcess(context, caseFile);

        if (nextQueueAction.equals(CaseFileConstants.NEXT_QUEUE_ACTION_NEXT))
        {
            caseFile.getParticipants().stream()
                    .filter(p -> "assignee".equals(p.getParticipantType()) || "owning group".equals(p.getParticipantType())).forEach(p -> {
                        p.setParticipantLdapId("");
                    });
        }

        // the unlock of the case file should be released from the UI,
        // but extensions already rely on the service releasing the lock, so we'll leave this here
        getAcmObjectLockService().removeLock(caseId, CaseFileConstants.OBJECT_TYPE, "OBJECT_LOCK", context.getAuthentication().getName());

        // we don't need to explicitly save the case file. Since the casefile is a managed entity (because we did
        // not detach it) any changes we made are automatically applied at the end of the transaction.

        return new CaseFileEnqueueResponse(ErrorReason.NO_ERROR, nextQueue, caseFile);
    }

    private List<String> verifyLeaveConditions(CaseFilePipelineContext context, CaseFile caseFile)
    {
        LeaveCurrentQueueModel<CaseFile, CaseFilePipelineContext> leaveModel = new LeaveCurrentQueueModel<>();
        leaveModel.setBusinessObject(caseFile);
        leaveModel.setPipelineContext(context);
        leaveModel = getLeaveCurrentQueueBusinessRule().applyRules(leaveModel);

        return leaveModel.getCannotLeaveReasons();
    }

    private List<String> verifyNextPossibleQueues(CaseFilePipelineContext context, CaseFile caseFile)
    {
        NextPossibleQueuesModel<CaseFile, CaseFilePipelineContext> nextPossibleQueuesModel = getQueueService().nextPossibleQueues(caseFile,
                context, getCaseFileNextPossibleQueuesBusinessRule());
        return nextPossibleQueuesModel.getNextPossibleQueues();
    }

    public List<String> verifyNextConditions(CaseFilePipelineContext context, CaseFile caseFile)
    {
        EnterQueueModel<CaseFile, CaseFilePipelineContext> enterModel = new EnterQueueModel<>();
        enterModel.setBusinessObject(caseFile);
        enterModel.setPipelineContext(context);
        enterModel.setSystemConfiguration(getSystemConfigurationService().readConfiguration());
        enterModel = getEnterQueueBusinessRule().applyRules(enterModel);

        return enterModel.getCannotEnterReasons();
    }

    private void startLeaveProcess(CaseFilePipelineContext context, CaseFile caseFile)
    {
        OnLeaveQueueModel<CaseFile, CaseFilePipelineContext> onLeaveModel = new OnLeaveQueueModel<>();
        onLeaveModel.setBusinessObject(caseFile);
        onLeaveModel.setPipelineContext(context);
        onLeaveModel = getOnLeaveQueueBusinessRule().applyRules(onLeaveModel);

        String leaveProcessName = onLeaveModel.getBusinessProcessName();
        if (leaveProcessName != null && !leaveProcessName.isEmpty())
        {
            Map<String, Object> processVariables = createProcessVariables(caseFile);
            getStartBusinessProcessService().startBusinessProcess(leaveProcessName, processVariables);
        }
    }

    private void startEnterProcess(CaseFilePipelineContext context, CaseFile caseFile)
    {
        OnEnterQueueModel<CaseFile, CaseFilePipelineContext> onEnterModel = new OnEnterQueueModel<>();
        onEnterModel.setBusinessObject(caseFile);
        onEnterModel.setPipelineContext(context);
        onEnterModel = getOnEnterQueueBusinessRule().applyRules(onEnterModel);

        String enterProcessName = onEnterModel.getBusinessProcessName();

        log.debug("enterProcessName: {}", enterProcessName);
        if (enterProcessName != null && !enterProcessName.isEmpty())
        {
            Map<String, Object> processVariables = createProcessVariables(caseFile);
            processVariables.put("NEW_QUEUE_NAME", onEnterModel.getBusinessObjectNewQueueName());
            processVariables.put("NEW_OBJECT_STATUS", onEnterModel.getBusinessObjectNewStatus());
            processVariables.put("ASSIGNEES", onEnterModel.getTaskAssignees());
            processVariables.put("TASK_NAME", onEnterModel.getTaskName());
            processVariables.put("TASK_OWNING_GROUP", onEnterModel.getTaskOwningGroup());
            processVariables.put("USERNAME", context.getAuthentication().getName());
            getStartBusinessProcessService().startBusinessProcess(enterProcessName, processVariables);

            getCaseFileDao().getEm().flush();
            caseFile = getCaseFileDao().find(caseFile.getId());
            getSaveCaseFileBusinessRule().applyRules(caseFile);
        }
    }

    private Map<String, Object> createProcessVariables(CaseFile caseFile)
    {
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("OBJECT_TYPE", "CASE_FILE");
        processVariables.put("OBJECT_ID", caseFile.getId());
        return processVariables;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public LeaveCurrentQueueBusinessRule getLeaveCurrentQueueBusinessRule()
    {
        return leaveCurrentQueueBusinessRule;
    }

    public void setLeaveCurrentQueueBusinessRule(LeaveCurrentQueueBusinessRule leaveCurrentQueueBusinessRule)
    {
        this.leaveCurrentQueueBusinessRule = leaveCurrentQueueBusinessRule;
    }

    public CaseFileNextPossibleQueuesBusinessRule getCaseFileNextPossibleQueuesBusinessRule()
    {
        return caseFileNextPossibleQueuesBusinessRule;
    }

    public void setCaseFileNextPossibleQueuesBusinessRule(CaseFileNextPossibleQueuesBusinessRule caseFileNextPossibleQueuesBusinessRule)
    {
        this.caseFileNextPossibleQueuesBusinessRule = caseFileNextPossibleQueuesBusinessRule;
    }

    public EnterQueueBusinessRule getEnterQueueBusinessRule()
    {
        return enterQueueBusinessRule;
    }

    public void setEnterQueueBusinessRule(EnterQueueBusinessRule enterQueueBusinessRule)
    {
        this.enterQueueBusinessRule = enterQueueBusinessRule;
    }

    public OnLeaveQueueBusinessRule getOnLeaveQueueBusinessRule()
    {
        return onLeaveQueueBusinessRule;
    }

    public void setOnLeaveQueueBusinessRule(OnLeaveQueueBusinessRule onLeaveQueueBusinessRule)
    {
        this.onLeaveQueueBusinessRule = onLeaveQueueBusinessRule;
    }

    public OnEnterQueueBusinessRule getOnEnterQueueBusinessRule()
    {
        return onEnterQueueBusinessRule;
    }

    public void setOnEnterQueueBusinessRule(OnEnterQueueBusinessRule onEnterQueueBusinessRule)
    {
        this.onEnterQueueBusinessRule = onEnterQueueBusinessRule;
    }

    public QueueService getQueueService()
    {
        return queueService;
    }

    public void setQueueService(QueueService queueService)
    {
        this.queueService = queueService;
    }

    public StartBusinessProcessService getStartBusinessProcessService()
    {
        return startBusinessProcessService;
    }

    public void setStartBusinessProcessService(StartBusinessProcessService startBusinessProcessService)
    {
        this.startBusinessProcessService = startBusinessProcessService;
    }

    public SaveCaseFileBusinessRule getSaveCaseFileBusinessRule()
    {
        return saveCaseFileBusinessRule;
    }

    public void setSaveCaseFileBusinessRule(SaveCaseFileBusinessRule saveCaseFileBusinessRule)
    {
        this.saveCaseFileBusinessRule = saveCaseFileBusinessRule;
    }

    public AcmObjectLockService getAcmObjectLockService()
    {
        return acmObjectLockService;
    }

    public void setAcmObjectLockService(AcmObjectLockService acmObjectLockService)
    {
        this.acmObjectLockService = acmObjectLockService;
    }

    public SystemConfiguration getSystemConfiguration()
    {
        return systemConfiguration;
    }

    public void setSystemConfiguration(SystemConfiguration systemConfiguration)
    {
        this.systemConfiguration = systemConfiguration;
    }

    public SystemConfigurationService getSystemConfigurationService()
    {
        return systemConfigurationService;
    }

    public void setSystemConfigurationService(SystemConfigurationService systemConfigurationService)
    {
        this.systemConfigurationService = systemConfigurationService;
    }

    public CaseFileEventUtility getCaseFileEventUtility()
    {
        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility)
    {
        this.caseFileEventUtility = caseFileEventUtility;
    }

    public TimesheetService getTimesheetService()
    {
        return timesheetService;
    }

    public void setTimesheetService(TimesheetService timesheetService)
    {
        this.timesheetService = timesheetService;
    }
}
