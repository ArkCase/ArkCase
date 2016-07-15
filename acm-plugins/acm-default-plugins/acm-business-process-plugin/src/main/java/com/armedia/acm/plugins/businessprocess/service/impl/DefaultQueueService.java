package com.armedia.acm.plugins.businessprocess.service.impl;

import com.armedia.acm.drools.SimpleStatelessSingleObjectRuleManager;
import com.armedia.acm.plugins.businessprocess.model.NextPossibleQueueModel;
import com.armedia.acm.plugins.businessprocess.service.QueueService;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;

public class DefaultQueueService implements QueueService
{

    @Override
    public <T, P extends AbstractPipelineContext, R extends SimpleStatelessSingleObjectRuleManager<NextPossibleQueueModel<T, P>>> NextPossibleQueueModel<T, P> nextPossibleQueues(
            T businessObject, P context, R ruleManager)
    {
        NextPossibleQueueModel<T, P> nextQueues = new NextPossibleQueueModel<>();
        nextQueues.setBusinessObject(businessObject);
        nextQueues.setPipelineContext(context);

        return ruleManager.applyRules(nextQueues);

    }

}
