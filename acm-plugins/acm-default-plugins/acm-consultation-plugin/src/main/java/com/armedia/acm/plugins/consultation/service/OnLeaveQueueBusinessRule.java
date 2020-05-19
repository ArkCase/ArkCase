package com.armedia.acm.plugins.consultation.service;

import com.armedia.acm.drools.SimpleStatelessSingleObjectRuleManager;
import com.armedia.acm.plugins.businessprocess.model.OnLeaveQueueModel;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;

public class OnLeaveQueueBusinessRule extends SimpleStatelessSingleObjectRuleManager<OnLeaveQueueModel<Consultation, ConsultationPipelineContext>>
{

}
