package com.armedia.acm.plugins.ecm.workflow;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.core.RuleBase;
import org.drools.core.RuleBaseFactory;
import org.drools.core.StatelessSession;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.StringReader;

import static org.junit.Assert.*;

/**
 * Created by armdev on 11/4/14.
 */
public class FormBusinessProcessRulesIT
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private StatelessSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-form-business-process-rules.xlsx");
        assertTrue(xls.exists());

        String drl = sc.compile(xls.getInputStream(), InputType.XLS);
        log.info("DRL: " + drl);

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl(new StringReader(drl));

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(builder.getPackage());

        workingMemory = ruleBase.newStatelessSession();
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
