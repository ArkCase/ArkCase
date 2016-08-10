package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.drools.SimpleStatelessSingleObjectRuleManager;
import com.armedia.acm.plugins.businessprocess.model.EnterQueueModel;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;

public class EnterQueueBusinessRule extends SimpleStatelessSingleObjectRuleManager<EnterQueueModel<CaseFile, CaseFilePipelineContext>>
{

}
