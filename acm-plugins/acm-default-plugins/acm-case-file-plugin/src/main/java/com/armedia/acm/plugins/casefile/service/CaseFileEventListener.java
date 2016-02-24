package com.armedia.acm.plugins.casefile.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationListener;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;
import com.armedia.acm.services.participants.model.AcmParticipant;

public class CaseFileEventListener implements ApplicationListener<CaseEvent>
{

    private static final String OBJECT_TYPE = "CASE_FILE";
    private static final String EVENT_TYPE = "com.armedia.acm.casefile.event";

    private AcmObjectHistoryService acmObjectHistoryService;
    private CaseFileEventUtility caseFileEventUtility;

    @Override
    public void onApplicationEvent(CaseEvent event)
    {
        if (event != null && event.getSource() != null)
        {
            CaseFile updatedCaseFile = (CaseFile) event.getSource();
            
            boolean execute = checkExecution(event.getEventType(), updatedCaseFile.getStatus());

            if (execute)
            {
                AcmObjectHistory acmObjectHistory = getAcmObjectHistoryService().getAcmObjectHistory(updatedCaseFile.getId(), OBJECT_TYPE);

                String json = acmObjectHistory.getObjectString();
                // Convert JSON string to Object
                AcmUnmarshaller converter = ObjectConverter.createJSONUnmarshaller();
                CaseFile exsisting = (CaseFile) converter.unmarshall(json, CaseFile.class);

                if (priorityChanged(exsisting, updatedCaseFile))
                {
                    getCaseFileEventUtility().raiseFileModifiedEvent(updatedCaseFile, event.getIpAddress(), "priority.changed");
                }

                if (detailsChanged(exsisting, updatedCaseFile))
                {
                    getCaseFileEventUtility().raiseFileModifiedEvent(updatedCaseFile, event.getIpAddress(), "details.changed");
                }

                if (statusChanged(exsisting, updatedCaseFile))
                {
                    getCaseFileEventUtility().raiseFileModifiedEvent(updatedCaseFile, event.getIpAddress(), "status.changed");
                }
                
                checkParticipants(exsisting, updatedCaseFile, event);
            }
        }
    }

    private boolean priorityChanged(CaseFile caseFile, CaseFile updatedCaseFile)
    {
        String updatedPriority = updatedCaseFile.getPriority();
        String priority = caseFile.getPriority();
        return !updatedPriority.equals(priority);
    }

    private boolean detailsChanged(CaseFile caseFile, CaseFile updatedCaseFile)
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

    private boolean statusChanged(CaseFile caseFile, CaseFile updatedCaseFile)
    {
        String updatedStatus = updatedCaseFile.getStatus();
        String status = caseFile.getStatus();
        return !updatedStatus.equals(status);
    }
    
    public void checkParticipants(CaseFile caseFile, CaseFile updatedCaseFile, CaseEvent event){
        List<AcmParticipant> existing = caseFile.getParticipants();
        List<AcmParticipant> updated = updatedCaseFile.getParticipants();

        Set<AcmParticipant> es = new HashSet<>(existing);
        Set<AcmParticipant> us = new HashSet<>(updated);

        if (es.addAll(us))
        {
            // participants added
            getCaseFileEventUtility().raiseFileModifiedEvent(updatedCaseFile, event.getIpAddress(), "participants.added");
        }
        
        // set is mutable
        es = new HashSet<>(existing);
       
        if (us.addAll(es))
        {
            // participants deleted
            getCaseFileEventUtility().raiseFileModifiedEvent(updatedCaseFile, event.getIpAddress(), "participants.deleted");
        }
    }

    private boolean checkExecution(String eventType, String status)
    {
        return String.format("%s.updated", EVENT_TYPE).equals(eventType) || ( !status.equals("DRAFT") && String.format("%s.%s", EVENT_TYPE, status).equals(eventType));
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
}
