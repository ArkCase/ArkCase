package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.plugins.businessprocess.model.NextPossibleQueueModel;
import com.armedia.acm.plugins.businessprocess.service.QueueService;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.service.CaseFileNextPossibleQueuesBusinessRule;

import java.util.List;

public class CaseFileNextPossibleQueuesAPIController
{

    private QueueService queueService;

    private CaseFileNextPossibleQueuesBusinessRule businessRule;

    public List<String> nextPossibleQueues(CaseFile caseFile)
    {

        CaseFilePipelineContext context = new CaseFilePipelineContext();
        NextPossibleQueueModel<CaseFile, CaseFilePipelineContext> nextPossibleQueues = queueService.nextPossibleQueues(caseFile, context,
                businessRule);
        return nextPossibleQueues.getNextPossibleQueues();

    }

    public QueueService getQueueService()
    {
        return queueService;
    }

    public void setQueueService(QueueService queueService)
    {
        this.queueService = queueService;
    }

    public CaseFileNextPossibleQueuesBusinessRule getBusinessRule()
    {
        return businessRule;
    }

    public void setBusinessRule(CaseFileNextPossibleQueuesBusinessRule businessRule)
    {
        this.businessRule = businessRule;
    }

}
