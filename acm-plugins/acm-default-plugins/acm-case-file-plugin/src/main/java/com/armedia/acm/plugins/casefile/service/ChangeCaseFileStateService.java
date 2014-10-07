package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Created by armdev on 9/6/14.
 */
public class ChangeCaseFileStateService
{
    private CaseFileDao dao;
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

    public CaseFileDao getDao()
    {
        return dao;
    }

    public void setDao(CaseFileDao dao)
    {
        this.dao = dao;
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
