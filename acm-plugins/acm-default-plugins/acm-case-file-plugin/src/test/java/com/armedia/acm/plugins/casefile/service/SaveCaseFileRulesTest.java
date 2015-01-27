package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.model.CaseFile;
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

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by armdev on 4/17/14.
 */
public class SaveCaseFileRulesTest
{

    private Logger log = LoggerFactory.getLogger(getClass());
    private StatelessKnowledgeSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-case-file-rules.xlsx");
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

        assertNotNull(workingMemory);
    }

    @Test
    public void nullCaseNumber() throws Exception
    {
        CaseFile caseFile = new CaseFile();
        caseFile.setId(12345L);

        workingMemory.execute(caseFile);

        assertNotNull(caseFile.getCaseNumber());

        log.info("Case number: " + caseFile.getCaseNumber());
    }

    @Test
    public void nullDueDate() throws Exception
    {
        CaseFile caseFile = new CaseFile();
        caseFile.setId(12345L);

        workingMemory.execute(caseFile);

        assertNotNull(caseFile.getDueDate());

        log.debug("due date: " + caseFile.getDueDate());
    }

    @Test
    public void caseNumberExists() throws Exception
    {
        CaseFile caseFile = new CaseFile();
        caseFile.setId(12345L);
        caseFile.setCaseNumber("A Number");

        workingMemory.execute(caseFile);
        assertEquals("A Number", caseFile.getCaseNumber());
    }

    @Test
    public void nullCasePriority() throws Exception
    {
        CaseFile caseFile = new CaseFile();
        caseFile.setId(12345L);

        workingMemory.execute(caseFile);

        assertNotNull(caseFile.getPriority());

        log.info("Case priority: " + caseFile.getPriority());
    }

    @Test
    public void date()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 180);

        Date date180 = cal.getTime();
        log.debug("date180: " + date180);
    }

}
