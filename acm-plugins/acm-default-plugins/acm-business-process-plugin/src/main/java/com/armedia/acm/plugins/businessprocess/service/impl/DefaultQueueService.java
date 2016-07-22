package com.armedia.acm.plugins.businessprocess.service.impl;

import com.armedia.acm.drools.SimpleStatelessSingleObjectRuleManager;
import com.armedia.acm.plugins.businessprocess.model.NextPossibleQueuesModel;
import com.armedia.acm.plugins.businessprocess.service.QueueService;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;

public class DefaultQueueService implements QueueService
{

    @Override
    public <T, P extends AbstractPipelineContext, R extends SimpleStatelessSingleObjectRuleManager<NextPossibleQueuesModel<T, P>>> NextPossibleQueuesModel<T, P> nextPossibleQueues(
            T businessObject, P context, R ruleManager)
    {
        NextPossibleQueuesModel<T, P> nextQueues = new NextPossibleQueuesModel<>();
        nextQueues.setBusinessObject(businessObject);
        nextQueues.setPipelineContext(context);

        return ruleManager.applyRules(nextQueues);

    }

}
