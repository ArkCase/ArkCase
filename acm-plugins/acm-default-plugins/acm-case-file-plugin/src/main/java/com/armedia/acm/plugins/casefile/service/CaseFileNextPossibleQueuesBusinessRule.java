package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.drools.SimpleStatelessSingleObjectRuleManager;
import com.armedia.acm.plugins.businessprocess.model.NextPossibleQueuesModel;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;

public class CaseFileNextPossibleQueuesBusinessRule
        extends SimpleStatelessSingleObjectRuleManager<NextPossibleQueuesModel<CaseFile, CaseFilePipelineContext>>
{

}
