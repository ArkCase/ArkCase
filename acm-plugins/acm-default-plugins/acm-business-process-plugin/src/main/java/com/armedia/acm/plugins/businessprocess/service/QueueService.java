package com.armedia.acm.plugins.businessprocess.service;

import com.armedia.acm.drools.SimpleStatelessSingleObjectRuleManager;
import com.armedia.acm.plugins.businessprocess.model.NextPossibleQueueModel;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;

public interface QueueService
{

    <T, P extends AbstractPipelineContext, R extends SimpleStatelessSingleObjectRuleManager<NextPossibleQueueModel<T, P>>> NextPossibleQueueModel<T, P> nextPossibleQueues(
            T businessObject, P context, R ruleManager);

}
