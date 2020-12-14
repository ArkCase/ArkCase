package com.armedia.acm.plugins.task.listener;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.activiti.services.AcmBpmnService;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.BuckslipFutureTask;
import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileAddedEvent;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.ecm.workflow.EcmFileWorkflowConfiguration;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.service.AcmParticipantService;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by dmiller on 12/5/2016.
 */
public class FileCreatedBuckslipWorkflowHandler implements ApplicationListener<EcmFileAddedEvent>
{
    private transient final Logger LOG = LogManager.getLogger(getClass());
    private FileWorkflowBusinessRule fileWorkflowBusinessRule;
    private RuntimeService activitiRuntimeService;
    private ObjectConverter objectConverter;
    private UserDao userDao;
    private TaskDao taskDao;
    private AcmParticipantService acmParticipantService;

    @Override
    public void onApplicationEvent(EcmFileAddedEvent event)
    {
        EcmFile source = event.getSource();

        EcmFileWorkflowConfiguration configuration = new EcmFileWorkflowConfiguration();

        configuration.setEcmFile(source);

        LOG.debug("Calling business rules for new file id {}, type {}", source.getId(), source.getFileType());

        configuration = getFileWorkflowBusinessRule().applyRules(configuration);
        if (!configuration.isBuckslipProcess())
        {
            return;
        }

        LOG.debug("start process for file id {}, type {}? {}", source.getId(), source.getFileType(), configuration.isStartProcess());

        if (configuration.isStartProcess())
        {
            try {
                startBusinessProcess(event, configuration);
            } catch (AcmCreateObjectFailedException | AcmUserActionFailedException e) {
                // Nothing we can do at this point, just rethrow error
                throw new RuntimeException("Error caused while starting business process, FileCreatedBuckslip", e);
            }
        }
    }

    private void startBusinessProcess(EcmFileAddedEvent event, EcmFileWorkflowConfiguration configuration) throws AcmCreateObjectFailedException, AcmUserActionFailedException {
        String processName = configuration.getProcessName();

        Map<String, Object> pvars = new HashMap<>();

        //getting owningGroup needed for Future Tasks
        List<AcmParticipant> participants = getAcmParticipantService().getParticipantsFromParentObject(event.getParentObjectId(), event.getParentObjectType());
        String owningGroup = ParticipantUtils.getOwningGroupIdFromParticipants(participants);

        String approversCsv = configuration.getApprovers();
        List<String> approvers = approversCsv == null ? new ArrayList<>()
                : Arrays.stream(approversCsv.split(",")).filter(s -> s != null).map(s -> s.trim()).collect(Collectors.toList());
        // the process should work with either "approvers" or "futureApprovers"
        pvars.put("approvers", approvers);
        pvars.put("taskName", configuration.getTaskName());
        pvars.put("documentAuthor", event.getUserId());
        pvars.put("pdfRenditionId", event.getSource().getFileId());

        // "documentType" is a misleading name here, but keeping it for backwards compatibility
        pvars.put("documentType", event.getSource().getContainer().getContainerObjectTitle());
        pvars.put("PARENT_OBJECT_NAME", event.getSource().getContainer().getContainerObjectTitle());

        pvars.put("OBJECT_TYPE", "FILE");
        pvars.put("OBJECT_ID", event.getSource().getFileId());
        pvars.put("OBJECT_NAME", event.getSource().getFileName());
        pvars.put("PARENT_OBJECT_TYPE", event.getParentObjectType());
        pvars.put("PARENT_OBJECT_ID", event.getParentObjectId());
        pvars.put(event.getParentObjectType(), event.getParentObjectId());
        pvars.put("REQUEST_TYPE", configuration.getRequestType());
        pvars.put("IP_ADDRESS", event.getIpAddress());

        pvars.put("taskDueDateExpression", configuration.getTaskDueDateExpression());
        pvars.put("taskPriority", configuration.getTaskPriority());

        pvars.put("futureTasks",
                getFutureTasks(approvers, configuration.getTaskName(), owningGroup, configuration.getTaskName(), event.getUserId(), 3));


        getTaskDao().startBusinessProcess(pvars, processName);
    }

    private String getFutureTasks(List<String> approvers, String taskName, String groupName, String details, String addedBy,
            int maxDurationInDays)
    {
        AcmMarshaller converter = getObjectConverter().getJsonMarshaller();
        List<BuckslipFutureTask> futureTasks = new ArrayList<>();

        approvers.forEach(approver -> {
            BuckslipFutureTask task = new BuckslipFutureTask();

            AcmUser approverUser = getUserDao().findByUserId(approver);
            AcmUser addedByUser = getUserDao().findByUserId(addedBy);

            String approverFullName = Objects.nonNull(approverUser) ? approverUser.getFullName() : "";
            String addedByFullName = Objects.nonNull(addedByUser) ? addedByUser.getFullName() : "";

            task.setApproverId(approver);
            task.setApproverFullName(approverFullName);
            task.setTaskName(taskName);
            task.setGroupName(groupName);
            task.setDetails(details);
            task.setAddedBy(addedBy);
            task.setAddedByFullName(addedByFullName);
            task.setMaxTaskDurationInDays(maxDurationInDays);

            futureTasks.add(task);
        });

        return converter.marshal(futureTasks);
    }

    public FileWorkflowBusinessRule getFileWorkflowBusinessRule()
    {
        return fileWorkflowBusinessRule;
    }

    public void setFileWorkflowBusinessRule(FileWorkflowBusinessRule fileWorkflowBusinessRule)
    {
        this.fileWorkflowBusinessRule = fileWorkflowBusinessRule;
    }
    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public TaskDao getTaskDao() {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public AcmParticipantService getAcmParticipantService() {
        return acmParticipantService;
    }

    public void setAcmParticipantService(AcmParticipantService acmParticipantService) {
        this.acmParticipantService = acmParticipantService;
    }
}
