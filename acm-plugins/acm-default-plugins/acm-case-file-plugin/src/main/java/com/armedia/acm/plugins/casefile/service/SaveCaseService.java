package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by armdev on 4/8/15.
 */
public interface SaveCaseService
{
    @Transactional
    CaseFile saveCase(CaseFile in, Authentication auth, String ipAddress) throws MuleException;
}
