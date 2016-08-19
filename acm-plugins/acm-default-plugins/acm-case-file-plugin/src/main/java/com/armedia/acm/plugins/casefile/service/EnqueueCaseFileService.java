package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.web.api.CaseFileEnqueueResponse;

public interface EnqueueCaseFileService
{

    CaseFileEnqueueResponse enqueueCaseFile(Long caseId, String nextQueue, CaseFilePipelineContext context);

}
