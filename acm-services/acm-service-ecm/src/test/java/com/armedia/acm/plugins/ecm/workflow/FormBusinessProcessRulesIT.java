package com.armedia.acm.plugins.ecm.workflow;

import com.armedia.acm.plugins.ecm.model.EcmFile;
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
 * Created by armdev on 11/4/14.
 */
public class FormBusinessProcessRulesIT
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private StatelessKnowledgeSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-form-business-process-rules.xlsx");
        assertTrue(xls.exists());

        String drl = sc.compile(xls.getInputStream(), InputType.XLS);
        log.info("DRL: " + drl);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        kbuilder.add(ResourceFactory.newInputStreamResource(xls.getInputStream()), ResourceType.DTABLE, dtconf);

        if ( kbuilder.hasErrors() )
        {
            for (KnowledgeBuilderError error : kbuilder.getErrors() )
            {
                log.error("Error building rules: " + error);
            }

            throw new RuntimeException("Could not build rules from " + xls.getFile().getAbsolutePath());
        }

        workingMemory = kbuilder.newKnowledgeBase().newStatelessKnowledgeSession();


    }

    @Test
    public void defaultNoWorkflow() throws Exception
    {
        assertNotNull(workingMemory);

        EcmFile randomType = new EcmFile();
        randomType.setFileType("9283982");

        EcmFileWorkflowConfiguration config = new EcmFileWorkflowConfiguration();
        config.setEcmFile(randomType);

        workingMemory.execute(config);

        assertFalse(config.isStartProcess());
    }

    @Test
    public void closeComplaintProcess() throws Exception
    {
        assertNotNull(workingMemory);

        EcmFile closeComplaint = new EcmFile();
        closeComplaint.setFileType("close_complaint");

        EcmFileWorkflowConfiguration config = new EcmFileWorkflowConfiguration();
        config.setEcmFile(closeComplaint);

        workingMemory.execute(config);

        assertTrue(config.isStartProcess());
        assertEquals("acmDocumentWorkflow", config.getProcessName());
        assertEquals("P3D", config.getTaskDueDateExpression());
        assertEquals(50, config.getTaskPriority());
    }
}
