package com.armedia.acm.plugins.casefile.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;
import com.armedia.acm.services.participants.model.AcmParticipant;

public class CaseFileEventListener implements ApplicationListener<AcmObjectHistoryEvent> {

    private AcmObjectHistoryService acmObjectHistoryService;
    private CaseFileEventUtility caseFileEventUtility;

    @Override
    public void onApplicationEvent(AcmObjectHistoryEvent event) {
        if (event != null) {
            AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();

            boolean isCaseFile = checkExecution(acmObjectHistory.getObjectType());

            if (isCaseFile) {
                // Converter for JSON string to Object
                AcmUnmarshaller converter = ObjectConverter.createJSONUnmarshaller();

                String jsonUpdatedCaseFile = acmObjectHistory.getObjectString();
                CaseFile updatedCaseFile = (CaseFile) converter.unmarshall(jsonUpdatedCaseFile, CaseFile.class);

                AcmObjectHistory acmObjectHistoryExisting = getAcmObjectHistoryService().getAcmObjectHistory(updatedCaseFile.getId(),
                        CaseFileConstants.OBJECT_TYPE);

                if (acmObjectHistoryExisting != null) {

                    String json = acmObjectHistoryExisting.getObjectString();
                    CaseFile existing = (CaseFile) converter.unmarshall(json, CaseFile.class);

                    if (isPriorityChanged(existing, updatedCaseFile)) {
                        getCaseFileEventUtility().raiseCaseFileModifiedEvent(updatedCaseFile, event.getIpAddress(), "priority.changed");
                    }

                    if (isDetailsChanged(existing, updatedCaseFile)) {
                        getCaseFileEventUtility().raiseCaseFileModifiedEvent(updatedCaseFile, event.getIpAddress(), "details.changed");
                    }

                    checkParticipants(existing, updatedCaseFile);

                    if (isStatusChanged(existing, updatedCaseFile)) {
                        getCaseFileEventUtility().raiseCaseFileModifiedEvent(updatedCaseFile, event.getIpAddress(), "status.changed");
                    }
                }
            }
        }
    }

    private boolean isPriorityChanged(CaseFile caseFile, CaseFile updatedCaseFile) {
        String updatedPriority = updatedCaseFile.getPriority();
        String priority = caseFile.getPriority();
        return !updatedPriority.equals(priority);
    }

    private boolean isDetailsChanged(CaseFile caseFile, CaseFile updatedCaseFile) {
        String updatedDetails = updatedCaseFile.getDetails();
        String details = caseFile.getDetails();
        if (updatedDetails != null && details != null) {
            return !details.equals(updatedDetails);
        } else if (updatedDetails != null) {
            return true;
        }
        return false;
    }

    public void checkParticipants(CaseFile caseFile, CaseFile updatedCaseFile) {
        List<AcmParticipant> existing = caseFile.getParticipants();
        List<AcmParticipant> updated = updatedCaseFile.getParticipants();

        Set<AcmParticipant> es = new HashSet<>(existing);
        Set<AcmParticipant> us = new HashSet<>(updated);

        if (es.addAll(us)) {
            // participants added
            getCaseFileEventUtility().raiseCaseFileModifiedEvent(updatedCaseFile, "", "participants.added");
        }

        // set is mutable
        es = new HashSet<>(existing);

        if (us.addAll(es)) {
            // participants deleted
            getCaseFileEventUtility().raiseCaseFileModifiedEvent(updatedCaseFile, "", "participants.deleted");
        }
    }

    private boolean isStatusChanged(CaseFile caseFile, CaseFile updatedCaseFile) {
        String updatedStatus = updatedCaseFile.getStatus();
        String status = caseFile.getStatus();
        return !updatedStatus.equals(status);
    }

    private boolean checkExecution(String objectType) {
        return objectType.equals(CaseFileConstants.OBJECT_TYPE);
    }

    public AcmObjectHistoryService getAcmObjectHistoryService() {
        return acmObjectHistoryService;
    }

    public void setAcmObjectHistoryService(AcmObjectHistoryService acmObjectHistoryService) {
        this.acmObjectHistoryService = acmObjectHistoryService;
    }

    public CaseFileEventUtility getCaseFileEventUtility() {
        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility) {
        this.caseFileEventUtility = caseFileEventUtility;
    }
}
