package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by armdev on 4/8/15.
 */
public interface MergeCaseService
{
    public static final String MERGE_TEXT_SEPPARATOR = "\n";

    @Transactional
    CaseFile mergeCases(Authentication auth,
                        String ipAddress,
                        Long sourceId, Long targetId) throws MuleException;
}
