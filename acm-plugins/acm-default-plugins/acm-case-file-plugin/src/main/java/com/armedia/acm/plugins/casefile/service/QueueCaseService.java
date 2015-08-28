package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

/**
 * Created by armdev on 8/26/15.
 */
public interface QueueCaseService
{
    CaseFile enqueue(Long caseFileId, String queueName) throws PipelineProcessException;
}
