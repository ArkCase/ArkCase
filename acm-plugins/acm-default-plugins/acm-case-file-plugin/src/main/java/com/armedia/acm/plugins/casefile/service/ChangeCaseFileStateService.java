package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.dao.ChangeCaseStatusDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;

import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Created by armdev on 9/6/14.
 */
public class ChangeCaseFileStateService
{
    private CaseFileDao dao;
    private ChangeCaseStatusDao changeCaseStatusDao;
    private CaseFileEventUtility caseFileEventUtility;

    public CaseFile changeCaseState(Authentication auth, Long caseId, String newState, String ipAddress)
            throws AcmUserActionFailedException
    {
        try
        {
            CaseFile retval = getDao().find(caseId);

            // do we need to do anything?
            if ( retval.getStatus().equals(newState) )
            {
                return retval;
            }

            Date now = new Date();

            retval.setStatus(newState);
            retval.setModified(now);
            retval.setModifier(auth.getName());

            retval.setStatus(newState);

            if ( "CLOSED".equals(newState) )
            {
                retval.setClosed(now);
            }

            retval = getDao().save(retval);

            getCaseFileEventUtility().raiseEvent(retval, newState, now, ipAddress, auth.getName(), auth);

            return retval;
        }
        catch (Exception e)
        {
            throw new AcmUserActionFailedException("Set case to " + newState, "Case File", caseId, e.getMessage(), e);
        }
    }
    
    public void handleChangeCaseStatusApproved(Long caseId, Long requestId, String userId, Date approvalDate, String ipAddress)
    {
    	CaseFile updatedCase = updateCaseStatus(caseId, requestId);
    	
    	updateCaseStatusRequestToApproved(requestId);
    	
    	getCaseFileEventUtility().raiseEvent(updatedCase, updatedCase.getStatus(), approvalDate, ipAddress, userId, null);
    }
    
    private CaseFile updateCaseStatus(Long caseId, Long requestId)
    {
    	ChangeCaseStatus changeCaseStatus = getChangeCaseStatusDao().find(requestId);
    	
    	CaseFile toSave = getDao().find(caseId);
    	toSave.setStatus(changeCaseStatus.getStatus());
    	
    	CaseFile updated = getDao().save(toSave);
    	
    	return updated;
    }
    
    private ChangeCaseStatus updateCaseStatusRequestToApproved(Long id)
    {
    	ChangeCaseStatus toSave = getChangeCaseStatusDao().find(id);
    	toSave.setStatus("APPROVED");
    	
    	ChangeCaseStatus updated = getChangeCaseStatusDao().save(toSave);
    	
    	return updated;
    }

    public CaseFileDao getDao()
    {
        return dao;
    }

    public void setDao(CaseFileDao dao)
    {
        this.dao = dao;
    }

    public ChangeCaseStatusDao getChangeCaseStatusDao() {
		return changeCaseStatusDao;
	}

	public void setChangeCaseStatusDao(ChangeCaseStatusDao changeCaseStatusDao) {
		this.changeCaseStatusDao = changeCaseStatusDao;
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
