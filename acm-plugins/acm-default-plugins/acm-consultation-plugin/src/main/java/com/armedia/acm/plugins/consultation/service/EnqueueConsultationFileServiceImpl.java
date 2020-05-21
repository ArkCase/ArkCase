package com.armedia.acm.plugins.consultation.service;

/*-
 * #%L
 * ACM Default Plugin: Consultation
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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
import com.armedia.acm.plugins.casefile.service.SystemConfigurationService;
import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationConstants;
import com.armedia.acm.plugins.consultation.model.ConsultationEnqueueResponse;
import com.armedia.acm.plugins.consultation.model.ConsultationEnqueueResponse.ErrorReason;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.consultation.utility.ConsultationEventUtility;
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

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class EnqueueConsultationFileServiceImpl implements EnqueueConsultationFileService
{
    private final Logger log = LogManager.getLogger(getClass());

    private ConsultationDao consultationDao;

    private LeaveCurrentQueueBusinessRule leaveCurrentQueueBusinessRule;

    private ConsultationNextPossibleQueuesBusinessRule consultationNextPossibleQueuesBusinessRule;

    private EnterQueueBusinessRule enterQueueBusinessRule;

    private OnLeaveQueueBusinessRule onLeaveQueueBusinessRule;

    private OnEnterQueueBusinessRule onEnterQueueBusinessRule;

    private QueueService queueService;

    private StartBusinessProcessService startBusinessProcessService;

    private SaveConsultationBusinessRule saveConsultationBusinessRule;

    private AcmObjectLockService acmObjectLockService;

    private SystemConfiguration systemConfiguration;

    private SystemConfigurationService systemConfigurationService;

    private ConsultationEventUtility consultationEventUtility;

    private TimesheetService timesheetService;

    @Override
    @Transactional
    public ConsultationEnqueueResponse enqueueConsultation(Long consultationId, String nextQueue, ConsultationPipelineContext context)
    {
        return enqueueConsultation(consultationId, nextQueue, null, context);
    }

    @Override
    @Transactional
    public ConsultationEnqueueResponse enqueueConsultation(Long consultationId, String nextQueue, String nextQueueAction, ConsultationPipelineContext context)
    {
        // since we will make changes to this Consultation, we should not detach it; the consultationDao detaches
        // the object, so we won't use the dao.find() method here.
        Consultation consultation = getConsultationDao().getEm().find(Consultation.class, consultationId);

        Boolean oldDeniedFlag = consultation.getDeniedFlag();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AcmAuthenticationDetails details = (AcmAuthenticationDetails) auth.getDetails();
        String ipAddress = details.getRemoteAddress();

        boolean hasAnyAssociatedTimesheets = getTimesheetService().getByObjectIdAndType(
                consultationId, ConsultationConstants.OBJECT_TYPE, 0, 1, "") != null;
        consultation.setHasAnyAssociatedTimesheets(hasAnyAssociatedTimesheets);

        if (nextQueueAction != null && nextQueueAction.equals("Deny"))
        {
            consultation.setDeniedFlag(true);
            consultationEventUtility.raiseEvent(consultation, "denied", new Date(), ipAddress, auth.getName(), auth);
        }

        context.setNewConsultation(false);
        context.setQueueName(consultation.getQueue().getName());
        context.setEnqueueName(nextQueue);

        List<String> cannotLeaveReasons = verifyLeaveConditions(context, consultation);
        if (!cannotLeaveReasons.isEmpty())
        {
            consultation.setDeniedFlag(oldDeniedFlag);
            return new ConsultationEnqueueResponse(ErrorReason.LEAVE, cannotLeaveReasons, nextQueue, consultation);
        }

        List<String> nextPossibleQueues = verifyNextPossibleQueues(context, consultation);
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
            consultation.setDeniedFlag(oldDeniedFlag);
            return new ConsultationEnqueueResponse(ErrorReason.NEXT_POSSIBLE, errorList, nextQueue, consultation);
        }

        List<String> cannotEnterReasons = verifyNextConditions(context, consultation);
        if (!cannotEnterReasons.isEmpty())
        {
            consultation.setDeniedFlag(oldDeniedFlag);
            return new ConsultationEnqueueResponse(ErrorReason.ENTER, cannotEnterReasons, nextQueue, consultation);
        }

        startLeaveProcess(context, consultation);
        startEnterProcess(context, consultation);

        if (nextQueueAction.equals(ConsultationConstants.NEXT_QUEUE_ACTION_NEXT))
        {
            consultation.getParticipants().stream()
                    .filter(p -> "assignee".equals(p.getParticipantType()) || "owning group".equals(p.getParticipantType())).forEach(p -> {
                        p.setParticipantLdapId("");
                    });
        }

        // the unlock of the consultation should be released from the UI,
        // but extensions already rely on the service releasing the lock, so we'll leave this here
        getAcmObjectLockService().removeLock(consultationId, ConsultationConstants.OBJECT_TYPE, "OBJECT_LOCK", context.getAuthentication().getName());

        // we don't need to explicitly save the consultation. Since the consultation is a managed entity (because we did
        // not detach it) any changes we made are automatically applied at the end of the transaction.

        consultationEventUtility.raiseConsultationModifiedEvent(consultation, ipAddress, "queue.changed",
                "from " + consultation.getPreviousQueue().getName() + " to " + nextQueue);

        return new ConsultationEnqueueResponse(ErrorReason.NO_ERROR, nextQueue, consultation);
    }

    private List<String> verifyLeaveConditions(ConsultationPipelineContext context, Consultation consultation)
    {
        LeaveCurrentQueueModel<Consultation, ConsultationPipelineContext> leaveModel = new LeaveCurrentQueueModel<>();
        leaveModel.setBusinessObject(consultation);
        leaveModel.setPipelineContext(context);
        leaveModel = getLeaveCurrentQueueBusinessRule().applyRules(leaveModel);

        return leaveModel.getCannotLeaveReasons();
    }

    private List<String> verifyNextPossibleQueues(ConsultationPipelineContext context, Consultation consultation)
    {
        NextPossibleQueuesModel<Consultation, ConsultationPipelineContext> nextPossibleQueuesModel = getQueueService().nextPossibleQueues(consultation,
                context, getConsultationNextPossibleQueuesBusinessRule());
        return nextPossibleQueuesModel.getNextPossibleQueues();
    }

    public List<String> verifyNextConditions(ConsultationPipelineContext context, Consultation consultation)
    {
        EnterQueueModel<Consultation, ConsultationPipelineContext> enterModel = new EnterQueueModel<>();
        enterModel.setBusinessObject(consultation);
        enterModel.setPipelineContext(context);
        enterModel.setSystemConfiguration(getSystemConfigurationService().readConfiguration());
        enterModel = getEnterQueueBusinessRule().applyRules(enterModel);

        return enterModel.getCannotEnterReasons();
    }

    private void startLeaveProcess(ConsultationPipelineContext context, Consultation consultation)
    {
        OnLeaveQueueModel<Consultation, ConsultationPipelineContext> onLeaveModel = new OnLeaveQueueModel<>();
        onLeaveModel.setBusinessObject(consultation);
        onLeaveModel.setPipelineContext(context);
        onLeaveModel = getOnLeaveQueueBusinessRule().applyRules(onLeaveModel);

        String leaveProcessName = onLeaveModel.getBusinessProcessName();
        if (leaveProcessName != null && !leaveProcessName.isEmpty())
        {
            Map<String, Object> processVariables = createProcessVariables(consultation);
            getStartBusinessProcessService().startBusinessProcess(leaveProcessName, processVariables);
        }
    }

    private void startEnterProcess(ConsultationPipelineContext context, Consultation consultation)
    {
        OnEnterQueueModel<Consultation, ConsultationPipelineContext> onEnterModel = new OnEnterQueueModel<>();
        onEnterModel.setBusinessObject(consultation);
        onEnterModel.setPipelineContext(context);
        onEnterModel = getOnEnterQueueBusinessRule().applyRules(onEnterModel);

        String enterProcessName = onEnterModel.getBusinessProcessName();

        log.debug("enterProcessName: {}", enterProcessName);
        if (enterProcessName != null && !enterProcessName.isEmpty())
        {
            Map<String, Object> processVariables = createProcessVariables(consultation);
            processVariables.put("NEW_QUEUE_NAME", onEnterModel.getBusinessObjectNewQueueName());
            processVariables.put("NEW_OBJECT_STATUS", onEnterModel.getBusinessObjectNewStatus());
            processVariables.put("ASSIGNEES", onEnterModel.getTaskAssignees());
            processVariables.put("TASK_NAME", onEnterModel.getTaskName());
            processVariables.put("TASK_OWNING_GROUP", onEnterModel.getTaskOwningGroup());
            processVariables.put("USERNAME", context.getAuthentication().getName());
            getStartBusinessProcessService().startBusinessProcess(enterProcessName, processVariables);

            getConsultationDao().getEm().flush();
            consultation = getConsultationDao().find(consultation.getId());
            getSaveConsultationBusinessRule().applyRules(consultation);
        }
    }

    private Map<String, Object> createProcessVariables(Consultation consultation)
    {
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("OBJECT_TYPE", ConsultationConstants.OBJECT_TYPE);
        processVariables.put("OBJECT_ID", consultation.getId());
        processVariables.put("OBJECT_STATUS", consultation.getStatus());
        processVariables.put("OBJECT_DENIED_FLAG", consultation.getDeniedFlag());
        return processVariables;
    }

    public ConsultationDao getConsultationDao()
    {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao)
    {
        this.consultationDao = consultationDao;
    }

    public LeaveCurrentQueueBusinessRule getLeaveCurrentQueueBusinessRule()
    {
        return leaveCurrentQueueBusinessRule;
    }

    public void setLeaveCurrentQueueBusinessRule(LeaveCurrentQueueBusinessRule leaveCurrentQueueBusinessRule)
    {
        this.leaveCurrentQueueBusinessRule = leaveCurrentQueueBusinessRule;
    }

    public ConsultationNextPossibleQueuesBusinessRule getConsultationNextPossibleQueuesBusinessRule()
    {
        return consultationNextPossibleQueuesBusinessRule;
    }

    public void setConsultationNextPossibleQueuesBusinessRule(ConsultationNextPossibleQueuesBusinessRule consultationNextPossibleQueuesBusinessRule)
    {
        this.consultationNextPossibleQueuesBusinessRule = consultationNextPossibleQueuesBusinessRule;
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

    public SaveConsultationBusinessRule getSaveConsultationBusinessRule()
    {
        return saveConsultationBusinessRule;
    }

    public void setSaveConsultationBusinessRule(SaveConsultationBusinessRule saveConsultationBusinessRule)
    {
        this.saveConsultationBusinessRule = saveConsultationBusinessRule;
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

    public ConsultationEventUtility getConsultationEventUtility()
    {
        return consultationEventUtility;
    }

    public void setConsultationEventUtility(ConsultationEventUtility consultationEventUtility)
    {
        this.consultationEventUtility = consultationEventUtility;
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
