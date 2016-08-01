package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.drools.SimpleStatelessSingleObjectRuleManager;
import com.armedia.acm.plugins.businessprocess.model.OnLeaveQueueModel;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;

public class OnLeaveQueueBusinessRule extends SimpleStatelessSingleObjectRuleManager<OnLeaveQueueModel<CaseFile, CaseFilePipelineContext>>
{

}
