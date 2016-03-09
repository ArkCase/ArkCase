package com.armedia.acm.plugins.complaint.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;
import com.armedia.acm.services.participants.model.AcmParticipant;

public class ComplaintEventListener implements ApplicationListener<AcmObjectHistoryEvent> {

    private AcmObjectHistoryService acmObjectHistoryService;
    private ComplaintEventPublisher complaintEventPublisher;

    @Override
    public void onApplicationEvent(AcmObjectHistoryEvent event) {
        if (event != null) {
            AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();

            boolean execute = checkExecution(acmObjectHistory.getObjectType());

            if (execute) {
                // Converter for JSON string to Object
                AcmUnmarshaller converter = ObjectConverter.createJSONUnmarshaller();

                String jsonUpdatedComplaint = acmObjectHistory.getObjectString();
                Complaint updatedComplaint = (Complaint) converter.unmarshall(jsonUpdatedComplaint, Complaint.class);

                AcmObjectHistory acmObjectHistoryExisting = getAcmObjectHistoryService().getAcmObjectHistory(updatedComplaint.getComplaintId(),
                        ComplaintConstants.OBJECT_TYPE);

                if (acmObjectHistoryExisting != null) {

                    String json = acmObjectHistoryExisting.getObjectString();
                    Complaint existing = (Complaint) converter.unmarshall(json, Complaint.class);

                    if (isPriorityChanged(existing, updatedComplaint)) {
                        getComplaintEventPublisher().publishComplaintModified(updatedComplaint, event.getIpAddress(), "priority.changed");
                    }

                    if (isDetailsChanged(existing, updatedComplaint)) {
                        getComplaintEventPublisher().publishComplaintModified(updatedComplaint, event.getIpAddress(), "details.changed");
                    }

                    if (isStatusChanged(existing, updatedComplaint)) {
                        getComplaintEventPublisher().publishComplaintModified(updatedComplaint, event.getIpAddress(), "status.changed");
                    }

                    checkParticipants(existing, updatedComplaint);
                }
            }
        }
    }

    private boolean isPriorityChanged(Complaint complaint, Complaint updatedComplaint) {
        String updatedPriority = updatedComplaint.getPriority();
        String priority = complaint.getPriority();
        return !updatedPriority.equals(priority);
    }

    private boolean isDetailsChanged(Complaint complaint, Complaint updatedComplaint) {
        String updatedDetails = updatedComplaint.getDetails();
        String details = complaint.getDetails();
        if (updatedDetails != null && details != null) {
            return !details.equals(updatedDetails);
        } else if (updatedDetails != null) {
            return true;
        }
        return false;
    }

    private void checkParticipants(Complaint complaint, Complaint updatedComplaint) {
        List<AcmParticipant> existing = complaint.getParticipants();
        List<AcmParticipant> updated = updatedComplaint.getParticipants();

        Set<AcmParticipant> es = new HashSet<>(existing);
        Set<AcmParticipant> us = new HashSet<>(updated);

        if (es.addAll(us)) {
            // participants added
            getComplaintEventPublisher().publishComplaintModified(updatedComplaint, "", "participants.added");
        }

        // set is mutable
        es = new HashSet<>(existing);

        if (us.addAll(es)) {
            // participants deleted
            getComplaintEventPublisher().publishComplaintModified(updatedComplaint, "", "participants.deleted");
        }
    }

    private boolean isStatusChanged(Complaint complaint, Complaint updatedComplaint) {
        String updatedStatus = updatedComplaint.getStatus();
        String status = complaint.getStatus();
        return !updatedStatus.equals(status);
    }

    private boolean checkExecution(String objectType) {
        return objectType.equals(ComplaintConstants.OBJECT_TYPE);
    }

    public AcmObjectHistoryService getAcmObjectHistoryService() {
        return acmObjectHistoryService;
    }

    public void setAcmObjectHistoryService(AcmObjectHistoryService acmObjectHistoryService) {
        this.acmObjectHistoryService = acmObjectHistoryService;
    }

    public ComplaintEventPublisher getComplaintEventPublisher() {
        return complaintEventPublisher;
    }

    public void setComplaintEventPublisher(ComplaintEventPublisher complaintEventPublisher) {
        this.complaintEventPublisher = complaintEventPublisher;
    }
}
