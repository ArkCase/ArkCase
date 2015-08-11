package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.exceptions.SplitCaseFileException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.SplitCaseOptions;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public interface SplitCaseService
{

    @Transactional
    CaseFile splitCase(Authentication auth,
                       String ipAddress,
                       SplitCaseOptions splitCaseOptions) throws MuleException, SplitCaseFileException, AcmUserActionFailedException, AcmCreateObjectFailedException, AcmFolderException, AcmObjectNotFoundException, PipelineProcessException;
}
