package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.springframework.security.core.Authentication;

/**
 * Created by armdev on 8/26/15.
 */
public interface QueueCaseService
{
    CaseFile enqueue(Long caseFileId, String queueName, Authentication auth, String ipAddress) throws PipelineProcessException;

    CaseFile enqueue(Long caseFileId, String queueName) throws PipelineProcessException;

}
