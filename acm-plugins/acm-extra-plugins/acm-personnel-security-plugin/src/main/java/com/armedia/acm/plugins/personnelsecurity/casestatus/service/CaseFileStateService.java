package com.armedia.acm.plugins.personnelsecurity.casestatus.service;

/*-
 * #%L
 * ACM Personnel Security
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

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.service.ChangeCaseFileStateService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This helper class is meant for use from Activiti business processes. It retrieves the user's name and ip
 * address from the Spring context holder, then calls the actual change case state service.
 */
public class CaseFileStateService
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private ChangeCaseFileStateService changeCaseFileStateService;

    public void changeCaseFileState(Long caseId, String newState)
    {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        String userid = currentUser.getName();

        String ipAddress = null;

        if (currentUser.getDetails() != null && AcmAuthenticationDetails.class.isAssignableFrom(currentUser.getDetails().getClass()))
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
