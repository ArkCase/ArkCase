package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileStartBusinessProcessModel;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.junit.Assert.*;

/**
 * Created by dmiller on 7/7/16.
 */
public class CaseFileStartBusinessProcessRuleTest
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private StatelessKnowledgeSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-start-business-process-rules.xlsx");
        assertTrue(xls.exists());

        String drl = sc.compile(xls.getInputStream(), InputType.XLS);
        log.info("DRL: " + drl);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        kbuilder.add(ResourceFactory.newInputStreamResource(xls.getInputStream()), ResourceType.DTABLE, dtconf);

        if (kbuilder.hasErrors())
        {
            for (KnowledgeBuilderError error : kbuilder.getErrors())
            {
                log.error("Error building rules: " + error);
            }

            throw new RuntimeException("Could not build rules from " + xls.getFile().getAbsolutePath());
        }

        workingMemory = kbuilder.newKnowledgeBase().newStatelessKnowledgeSession();

        assertNotNull(workingMemory);
    }

    @Test
    public void coreArkCaseShouldNotStartWorkflow() throws Exception
    {
        CaseFile caseFile = new CaseFile();
        caseFile.setCaseType("Investigative");
        CaseFilePipelineContext context = new CaseFilePipelineContext();
        context.setNewCase(true);

        CaseFileStartBusinessProcessModel businessProcessModel = new CaseFileStartBusinessProcessModel();
        businessProcessModel.setBusinessObject(caseFile);
        businessProcessModel.setPipelineContext(context);

        workingMemory.execute(businessProcessModel);

        assertFalse(businessProcessModel.isStartProcess());
        assertNull(businessProcessModel.getProcessName());
    }

    @Test
    public void caseWithNoCaseType() throws Exception
    {
        CaseFile caseFile = new CaseFile();
        caseFile.setCaseType(null);
        CaseFilePipelineContext context = new CaseFilePipelineContext();
        context.setNewCase(true);

        CaseFileStartBusinessProcessModel businessProcessModel = new CaseFileStartBusinessProcessModel();
        businessProcessModel.setBusinessObject(caseFile);
        businessProcessModel.setPipelineContext(context);

        workingMemory.execute(businessProcessModel);

        assertFalse(businessProcessModel.isStartProcess());
        assertNull(businessProcessModel.getProcessName());
    }


}
