package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.Disposition;
import com.armedia.acm.plugins.casefile.service.SaveCaseService;
import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociation;

import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 11/13/14.
 */
public class CloseCompaintRequestService
{
    private ComplaintDao complaintDao;
    private CloseComplaintRequestDao closeComplaintRequestDao;
    private ComplaintEventPublisher complaintEventPublisher;
    private SaveCaseService saveCaseService;
    private CaseFileDao caseFileDao;
    private EcmFileDao ecmFileDao;
    private EcmFileService ecmFileService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void handleCloseComplaintRequestApproved(
            Long complaintId,
            Long closeComplaintRequestId,
            String user,
            Date approvalDate) throws MuleException
    {
        CloseComplaintRequest updatedRequest = updateCloseComplaintRequestStatus(closeComplaintRequestId);

        Complaint updatedComplaint = updateComplaintStatus(complaintId, updatedRequest.getDisposition());

        boolean shouldFullInvestigationBeOpened = shallWeOpenAFullInvestigation(updatedRequest);
        log.debug("Open a new investigation? " + shouldFullInvestigationBeOpened);

        if ( shouldFullInvestigationBeOpened )
        {
            CaseFile fullInvestigation = openFullInvestigation(updatedComplaint, user);
            log.debug("Opened a full investigation: " + fullInvestigation.getCaseNumber());
        }

        boolean shouldComplaintBeAddedToExistingCase = shallWeAddComplaintToExistingCase(updatedRequest);
        log.debug("Add to existing case file? " + shouldComplaintBeAddedToExistingCase);

        if ( shouldComplaintBeAddedToExistingCase )
        {
            CaseFile updatedCaseFile = addToExistingCaseFile(updatedRequest, updatedComplaint, user);
            if ( updatedCaseFile != null )
            {
                log.debug("Added complaint to existing case file: " + updatedCaseFile.getCaseNumber());
            }
        }

        // get the entity manager to flush any outstanding SQL, so if there are any SQL exceptions, they get
        // raised here; the resulting exception will ensure we don't raise the complaint closed event.
        entityManager.flush();

        // only raise the completed event if we get here, e.g. everything has happened and no exception has been
        // raised.
        getComplaintEventPublisher().publishComplaintClosedEvent(updatedComplaint, user, true, approvalDate);
    }

    private CaseFile addToExistingCaseFile(CloseComplaintRequest updatedRequest, Complaint updatedComplaint, String userId)
            throws MuleException
    {
        String caseNumber = updatedRequest.getDisposition().getExistingCaseNumber();

        if ( caseNumber == null )
        {
            log.error("Can not add complaint to existing case file since there is no case number!");
            return null;
        }

        CaseFile existingCaseFile = getCaseFileDao().findByCaseNumber(caseNumber);
        if ( existingCaseFile == null )
        {
            log.error("Can not add complaint to existing case file since there is no case file with number '" +
                    caseNumber + "'!");
            return null;
        }

        // since we are adding the complaint to an existing case, which already has its own details, we do
        // not update the case file's details.  User can read the complaint details via the link to the
        // complaint from the references table.

        ObjectAssociation originalComplaint = makeObjectAssociation(updatedComplaint);
        existingCaseFile.addChildObject(originalComplaint);

        
        addPersonsToCaseFile(updatedComplaint.getPersonAssociations(), existingCaseFile);

        // here we need a full Authentication object
        Authentication auth = new UsernamePasswordAuthenticationToken(userId, userId);
        existingCaseFile = getSaveCaseService().saveCase(existingCaseFile, auth, null);

        addChildObjectsToCaseFile(updatedComplaint, existingCaseFile, auth);
        
        return existingCaseFile;

    }

    private void addChildObjectsToCaseFile(Complaint updatedComplaint, CaseFile existingCaseFile, Authentication auth)
    {
        if ( updatedComplaint == null || existingCaseFile == null)
        {
            return;
        }
        
		try 
		{
			AcmContainer container = getEcmFileService().getOrCreateContainer(updatedComplaint.getObjectType(), updatedComplaint.getId());
			AcmCmisObjectList files = getEcmFileService().allFilesForContainer(auth, container);
			
			AcmContainer containerCaseFile = getEcmFileService().getOrCreateContainer(existingCaseFile.getObjectType(), existingCaseFile.getId());
			
			if (files != null && files.getChildren() != null)
			{
				for (AcmCmisObject file : files.getChildren())
				{
					EcmFile ecmFile = new EcmFile();
					
					ecmFile.setFileName(file.getName());
					ecmFile.setFileType(file.getType());
					ecmFile.setContainer(containerCaseFile);
					ecmFile.setFolder(containerCaseFile.getFolder());
					ecmFile.setCategory(file.getCategory());
					ecmFile.setStatus(file.getStatus());
					ecmFile.setActiveVersionTag(file.getVersion());
					ecmFile.setVersionSeriesId(file.getCmisObjectId());
					ecmFile.setFileMimeType(file.getMimeType());
					
					getEcmFileDao().save(ecmFile);
				}
			}
		} 
		catch (AcmListObjectsFailedException | AcmCreateObjectFailedException | AcmUserActionFailedException e) 
		{
			log.error("Cannot save files.", e);
		}
    }

    private void addPersonsToCaseFile(List<PersonAssociation> personAssociations, CaseFile existingCaseFile)
    {
        if ( personAssociations == null )
        {
            return;
        }

        // Remove the person ID, so when we save the case, new person association records will be created.
        // Since we are not saving the complaint, nothing bad will happen to the complaint person associations.
        for ( PersonAssociation pa : personAssociations )
        {
            PersonAssociation paCopy = new PersonAssociation();
            paCopy.setPersonType(pa.getPersonType());
            paCopy.setPerson(pa.getPerson());
            paCopy.setPersonDescription(pa.getPersonDescription());
            paCopy.setNotes(pa.getNotes());
            paCopy.setTags(pa.getTags());
            paCopy.setParentId(existingCaseFile.getId());
            paCopy.setParentType(existingCaseFile.getObjectType());

            existingCaseFile.getPersonAssociations().add(paCopy);
        }
    }

    private boolean shallWeAddComplaintToExistingCase(CloseComplaintRequest updatedRequest)
    {
        if ( updatedRequest.getDisposition() == null )
        {
            log.debug("No disposition for request ID '" + updatedRequest.getId() + "'");
            return false;
        }

        return "add_exising_case".equals(updatedRequest.getDisposition().getDispositionType());
    }

    private CaseFile openFullInvestigation(Complaint updatedComplaint, String userId) throws MuleException
    {
        CaseFile caseFile = new CaseFile();
        caseFile.setStatus("ACTIVE");
        caseFile.setCaseType(updatedComplaint.getComplaintType());

        String details = formatCaseDetails(updatedComplaint);

        caseFile.setDetails(details);
        caseFile.setPriority(updatedComplaint.getPriority());
        caseFile.setTitle(updatedComplaint.getComplaintTitle());

        ObjectAssociation originalComplaint = makeObjectAssociation(updatedComplaint);
        caseFile.addChildObject(originalComplaint);

        addPersonsToCaseFile(updatedComplaint.getPersonAssociations(), caseFile);

        log.debug("About to save case file");

        // here we need a full Authentication object
        Authentication auth = new UsernamePasswordAuthenticationToken(userId, userId);
        CaseFile fullInvestigation = getSaveCaseService().saveCase(caseFile, auth, null);
        
        addChildObjectsToCaseFile(updatedComplaint, fullInvestigation, auth);

        return fullInvestigation;
    }

    private String formatCaseDetails(Complaint updatedComplaint)
    {
        String details = "This case file is based on Complaint '" + updatedComplaint.getComplaintNumber() + "'.";
        if ( updatedComplaint.getDetails() != null )
        {
            // details are displayed as HTML
            details += "<p/>";
            details += "Complaint '" + updatedComplaint.getComplaintNumber() + "' had these details: ";
            details += "<p/>";
            details += updatedComplaint.getDetails();
        }
        return details;
    }

    private ObjectAssociation makeObjectAssociation(Complaint updatedComplaint)
    {
        ObjectAssociation originalComplaint = new ObjectAssociation();
        originalComplaint.setTargetId(updatedComplaint.getComplaintId());
        originalComplaint.setTargetName(updatedComplaint.getComplaintNumber());
        originalComplaint.setTargetType("COMPLAINT");
        originalComplaint.setAssociationType("REFERENCE");
        return originalComplaint;
    }

    private boolean shallWeOpenAFullInvestigation(CloseComplaintRequest updatedRequest)
    {
        if ( updatedRequest.getDisposition() == null )
        {
            log.debug("No disposition for request ID '" + updatedRequest.getId() + "'");
            return false;
        }

        return "open_investigation".equals(updatedRequest.getDisposition().getDispositionType());
    }

    private CloseComplaintRequest updateCloseComplaintRequestStatus(Long closeComplaintRequestId)
    {
        CloseComplaintRequest ccr = getCloseComplaintRequestDao().find(closeComplaintRequestId);
        ccr.setStatus("APPROVED");
        CloseComplaintRequest updated = getCloseComplaintRequestDao().save(ccr);
        return updated;
    }

    private Complaint updateComplaintStatus(Long complaintId, Disposition disposition)
    {
        Complaint c = getComplaintDao().find(complaintId);
        c.setStatus("CLOSED");
        c.setDisposition(disposition);

        c = getComplaintDao().save(c);

        return c;
    }


    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    public CloseComplaintRequestDao getCloseComplaintRequestDao()
    {
        return closeComplaintRequestDao;
    }

    public void setCloseComplaintRequestDao(CloseComplaintRequestDao closeComplaintRequestDao)
    {
        this.closeComplaintRequestDao = closeComplaintRequestDao;
    }

    public ComplaintEventPublisher getComplaintEventPublisher()
    {
        return complaintEventPublisher;
    }

    public void setComplaintEventPublisher(ComplaintEventPublisher complaintEventPublisher)
    {
        this.complaintEventPublisher = complaintEventPublisher;
    }

    public SaveCaseService getSaveCaseService()
    {
        return saveCaseService;
    }

    public void setSaveCaseService(SaveCaseService saveCaseService)
    {
        this.saveCaseService = saveCaseService;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

	public EcmFileDao getEcmFileDao() {
		return ecmFileDao;
	}

	public void setEcmFileDao(EcmFileDao ecmFileDao) {
		this.ecmFileDao = ecmFileDao;
	}

	public EcmFileService getEcmFileService() {
		return ecmFileService;
	}

	public void setEcmFileService(EcmFileService ecmFileService) {
		this.ecmFileService = ecmFileService;
	}
}
