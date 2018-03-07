package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Created by armdev on 4/8/15.
 */
public interface SaveCaseService
{
    @Transactional
    CaseFile saveCase(CaseFile in, Authentication auth, String ipAddress) throws PipelineProcessException;

    /**
     * save casefile data
     *
     * @param casefile
     *            casefile data
     * @param files
     *            casefile pictures
     * @param authentication
     *            authentication
     * @return PerCaseFileson saved casefile
     */
    @Transactional
    CaseFile saveCase(CaseFile casefile, List<MultipartFile> files, Authentication authentication, String ipAddress)
            throws AcmUserActionFailedException,
            AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmObjectNotFoundException, PipelineProcessException,
            IOException;
}
