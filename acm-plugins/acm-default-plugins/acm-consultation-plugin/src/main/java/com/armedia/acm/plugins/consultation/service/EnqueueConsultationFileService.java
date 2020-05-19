package com.armedia.acm.plugins.consultation.service;

import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.consultation.web.api.ConsultationEnqueueResponse;

public interface EnqueueConsultationFileService
{
    ConsultationEnqueueResponse enqueueConsultation(Long consultationId, String nextQueue, ConsultationPipelineContext context);

    ConsultationEnqueueResponse enqueueConsultation(Long consultationId, String nextQueue, String nextQueueAction, ConsultationPipelineContext context);

}
