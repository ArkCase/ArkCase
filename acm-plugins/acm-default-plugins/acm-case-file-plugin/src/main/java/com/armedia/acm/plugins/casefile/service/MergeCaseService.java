package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.exceptions.MergeCaseFilesException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.MergeCaseOptions;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public interface MergeCaseService
{
    public static final String MERGE_TEXT_SEPPARATOR = "\n\nAppended from %s(%s):\n";

    @Transactional
    CaseFile mergeCases(Authentication auth,
                        String ipAddress,
                        MergeCaseOptions mergeCaseOptions) throws MuleException, MergeCaseFilesException, AcmUserActionFailedException, AcmCreateObjectFailedException, PipelineProcessException, AcmAccessControlException;
}
