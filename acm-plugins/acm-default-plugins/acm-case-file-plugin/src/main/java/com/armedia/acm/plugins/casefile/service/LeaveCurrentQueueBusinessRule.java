package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.drools.SimpleStatelessSingleObjectRuleManager;
import com.armedia.acm.plugins.businessprocess.model.LeaveCurrentQueueModel;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;

public class LeaveCurrentQueueBusinessRule
        extends SimpleStatelessSingleObjectRuleManager<LeaveCurrentQueueModel<CaseFile, CaseFilePipelineContext>>
{

}
