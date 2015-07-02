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

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by armdev on 4/17/14.
 */
public class SplitCaseFileRulesTest
{

    private Logger log = LoggerFactory.getLogger(getClass());
    private StatelessKnowledgeSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-split-case-file-rules.xlsx");
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
    public void caseTitle() throws Exception
    {
        CaseFile source = new CaseFile();
        source.setTitle("My Title");
        source.setCaseType("caseType");
        source.setCourtroomName("courtRoomName");
        source.setResponsibleOrganization("responsibleOrganization");
        source.setDetails("details");
        source.setStatus("status");

        CaseFile copy = new CaseFile();

        Map<String, CaseFile> caseFiles = new HashMap<>();
        caseFiles.put("source", source);
        caseFiles.put("copy", copy);

        workingMemory.execute(caseFiles);

        assertEquals(source.getTitle(), copy.getTitle());
        assertEquals(source.getCaseType(), copy.getCaseType());
        assertEquals(source.getCourtroomName(), copy.getCourtroomName());
        assertEquals(source.getResponsibleOrganization(), copy.getResponsibleOrganization());
        assertEquals(source.getDetails(), copy.getDetails());
        assertEquals(source.getStatus(), copy.getStatus());
    }



}
