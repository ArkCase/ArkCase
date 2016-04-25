package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.service.objecthistory.dao.AcmAssignmentDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryEventPublisher;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import org.springframework.context.ApplicationListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CaseFileEventListener implements ApplicationListener<AcmObjectHistoryEvent>
{

    private AcmObjectHistoryService acmObjectHistoryService;
    private AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher;
    private CaseFileEventUtility caseFileEventUtility;
    private AcmAssignmentDao acmAssignmentDao;

    @Override
    public void onApplicationEvent(AcmObjectHistoryEvent event)
    {
        if (event != null)
        {
            AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();

            boolean isCaseFile = checkExecution(acmObjectHistory.getObjectType());

            if (isCaseFile)
            {
                // Converter for JSON string to Object
                AcmUnmarshaller converter = ObjectConverter.createJSONUnmarshaller();

                String jsonUpdatedCaseFile = acmObjectHistory.getObjectString();
                CaseFile updatedCaseFile = (CaseFile) converter.unmarshall(jsonUpdatedCaseFile, CaseFile.class);

                AcmAssignment acmAssignment = createAcmAssignment(updatedCaseFile);

                AcmObjectHistory acmObjectHistoryExisting = getAcmObjectHistoryService().getAcmObjectHistory(updatedCaseFile.getId(),
                        CaseFileConstants.OBJECT_TYPE);

                if (acmObjectHistoryExisting != null)
                {

                    String json = acmObjectHistoryExisting.getObjectString();
                    CaseFile existing = (CaseFile) converter.unmarshall(json, CaseFile.class);

                    acmAssignment.setOldAssignee(ParticipantUtils.getAssigneeIdFromParticipants(existing.getParticipants()));

                    if (isPriorityChanged(existing, updatedCaseFile))
                    {
                        getCaseFileEventUtility().raiseCaseFileModifiedEvent(updatedCaseFile, event.getIpAddress(), "priority.changed");
                    }

                    if (isDetailsChanged(existing, updatedCaseFile))
                    {
                        getCaseFileEventUtility().raiseCaseFileModifiedEvent(updatedCaseFile, event.getIpAddress(), "details.changed");
                    }

                    checkParticipants(existing, updatedCaseFile, event.getIpAddress());

                    if (isStatusChanged(existing, updatedCaseFile))
                    {
                        getCaseFileEventUtility().raiseCaseFileModifiedEvent(updatedCaseFile, event.getIpAddress(), "status.changed");
                    }
                }

                if (isAssigneeChanged(acmAssignment))
                {
                    // Save assignment change in the database
                    getAcmAssignmentDao().save(acmAssignment);

                    // Raise an event
                    getAcmObjectHistoryEventPublisher().publishAssigneeChangeEvent(acmAssignment, event.getUserId(), event.getIpAddress());
                }
            }
        }
    }

    public boolean isAssigneeChanged(AcmAssignment assignment)
    {
        if (assignment.getNewAssignee() != null && assignment.getOldAssignee() != null)
        {
            if (assignment.getNewAssignee().equals(assignment.getOldAssignee()))
            {
                return false;
            }
        }

        if (assignment.getNewAssignee() == null && assignment.getOldAssignee() == null)
        {
            return false;
        }

        return true;
    }

    private AcmAssignment createAcmAssignment(CaseFile updatedCaseFile)
    {
        AcmAssignment assignment = new AcmAssignment();
        assignment.setObjectId(updatedCaseFile.getId());
        assignment.setObjectTitle(updatedCaseFile.getTitle());
        assignment.setObjectName(updatedCaseFile.getCaseNumber());
        assignment.setNewAssignee(ParticipantUtils.getAssigneeIdFromParticipants(updatedCaseFile.getParticipants()));
        assignment.setObjectType(CaseFileConstants.OBJECT_TYPE);

        return assignment;
    }

    private boolean isPriorityChanged(CaseFile caseFile, CaseFile updatedCaseFile)
    {
        String updatedPriority = updatedCaseFile.getPriority();
        String priority = caseFile.getPriority();
        return !updatedPriority.equals(priority);
    }

    private boolean isDetailsChanged(CaseFile caseFile, CaseFile updatedCaseFile)
    {
        String updatedDetails = updatedCaseFile.getDetails();
        String details = caseFile.getDetails();
        if (updatedDetails != null && details != null)
        {
            return !details.equals(updatedDetails);
        } else if (updatedDetails != null)
        {
            return true;
        }
        return false;
    }

    public void checkParticipants(CaseFile caseFile, CaseFile updatedCaseFile, String ipAddress)
    {
        List<AcmParticipant> existing = caseFile.getParticipants();
        List<AcmParticipant> updated = updatedCaseFile.getParticipants();

        Set<AcmParticipant> es = new HashSet<>(existing);
        Set<AcmParticipant> us = new HashSet<>(updated);

        if (us.addAll(es))
        {
            // set is mutable
            us = new HashSet<>(updated);

            for (AcmParticipant participant : es)
            {
                if (!us.contains(participant))
                {
                    // participants deleted
                    getCaseFileEventUtility().raiseParticipantDeletedInCaseFile(participant, updatedCaseFile, ipAddress);
                }
            }
        }
    }

    private boolean isStatusChanged(CaseFile caseFile, CaseFile updatedCaseFile)
    {
        String updatedStatus = updatedCaseFile.getStatus();
        String status = caseFile.getStatus();
        return !updatedStatus.equals(status);
    }

    private boolean checkExecution(String objectType)
    {

        return objectType.equals(CaseFileConstants.OBJECT_TYPE);
    }

    public AcmObjectHistoryService getAcmObjectHistoryService()
    {

        return acmObjectHistoryService;
    }

    public void setAcmObjectHistoryService(AcmObjectHistoryService acmObjectHistoryService)
    {

        this.acmObjectHistoryService = acmObjectHistoryService;
    }

    public CaseFileEventUtility getCaseFileEventUtility()
    {

        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility)
    {

        this.caseFileEventUtility = caseFileEventUtility;
    }

    public AcmObjectHistoryEventPublisher getAcmObjectHistoryEventPublisher()
    {
        return acmObjectHistoryEventPublisher;
    }

    public void setAcmObjectHistoryEventPublisher(AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher)
    {
        this.acmObjectHistoryEventPublisher = acmObjectHistoryEventPublisher;
    }

    public AcmAssignmentDao getAcmAssignmentDao()
    {
        return acmAssignmentDao;
    }

    public void setAcmAssignmentDao(AcmAssignmentDao acmAssignmentDao)
    {
        this.acmAssignmentDao = acmAssignmentDao;
    }
}
