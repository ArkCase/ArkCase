package com.armedia.acm.plugins.personnelsecurity.casestatus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by armdev on 12/9/14.
 */
public class CaseFileStateService
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public void changeCaseFileState(Long caseId, String newState)
    {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        log.info("User '" + currentUser.getName() + "' updated case '" + caseId + "' to status '" + newState + "'");
    }
}
