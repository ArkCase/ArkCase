package com.armedia.acm.plugins.consultation.service;

import com.armedia.acm.drools.SimpleStatelessSingleObjectRuleManager;
import com.armedia.acm.plugins.businessprocess.model.OnEnterQueueModel;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;

public class OnEnterQueueBusinessRule extends SimpleStatelessSingleObjectRuleManager<OnEnterQueueModel<Consultation, ConsultationPipelineContext>>
{

}
