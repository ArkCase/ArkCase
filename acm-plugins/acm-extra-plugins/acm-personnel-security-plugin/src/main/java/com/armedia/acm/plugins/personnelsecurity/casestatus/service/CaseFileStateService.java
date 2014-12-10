package com.armedia.acm.plugins.personnelsecurity.casestatus.service;

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.service.ChangeCaseFileStateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This helper class is meant for use from Activiti business processes. It retrieves the user's name and ip
 * address from the Spring context holder, then calls the actual change case state service.
 */
public class CaseFileStateService
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private ChangeCaseFileStateService changeCaseFileStateService;

    public void changeCaseFileState(Long caseId, String newState)
    {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        String userid = currentUser.getName();

        String ipAddress = null;

        if ( currentUser.getDetails() != null && AcmAuthenticationDetails.class.isAssignableFrom(currentUser.getDetails().getClass()) )
        {
            ipAddress = ((AcmAuthenticationDetails) currentUser.getDetails()).getRemoteAddress();
        }

        log.debug("User '" + userid + "' updated case '" + caseId + "' to status '" + newState + "'");

        try
        {
            getChangeCaseFileStateService().changeCaseState(currentUser, caseId, newState, ipAddress);
        }
        catch (AcmUserActionFailedException e)
        {
            log.error("Could not update case status: " + e.getMessage(), e);
        }
    }

    public ChangeCaseFileStateService getChangeCaseFileStateService()
    {
        return changeCaseFileStateService;
    }

    public void setChangeCaseFileStateService(ChangeCaseFileStateService changeCaseFileStateService)
    {
        this.changeCaseFileStateService = changeCaseFileStateService;
    }
}
