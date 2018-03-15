package com.armedia.acm.services.transcribe.rules;

import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeType;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.*;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/06/2018
 */
public class TranscribeRulesTest
{

    private Logger LOG = LoggerFactory.getLogger(getClass());
    private StatelessKnowledgeSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-transcribe-rules.xlsx");
        assertTrue(xls.exists());

        String drl = sc.compile(xls.getInputStream(), InputType.XLS);
        LOG.info("DRL: " + drl);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        kbuilder.add(ResourceFactory.newInputStreamResource(xls.getInputStream()), ResourceType.DTABLE, dtconf);

        if (kbuilder.hasErrors())
        {
            for (KnowledgeBuilderError error : kbuilder.getErrors())
            {
                LOG.error("Error building rules: " + error);
            }

            throw new RuntimeException("Could not build rules from " + xls.getFile().getAbsolutePath());
        }

        workingMemory = kbuilder.newKnowledgeBase().newStatelessKnowledgeSession();

        assertNotNull(workingMemory);
    }

    @Test
    public void remoteId_Automatic() throws Exception
    {
        Transcribe transcribe = new Transcribe();
        transcribe.setId(101L);
        transcribe.setType(TranscribeType.AUTOMATIC.toString());

        workingMemory.execute(transcribe);

        assertNotNull(transcribe.getRemoteId());

        LOG.debug("Remote ID: " + transcribe.getRemoteId());
    }

    @Test
    public void remoteId_Manual() throws Exception
    {
        Transcribe transcribe = new Transcribe();
        transcribe.setId(101L);
        transcribe.setType(TranscribeType.MANUAL.toString());

        workingMemory.execute(transcribe);

        assertNull(transcribe.getRemoteId());
    }

    @Test
    public void status() throws Exception
    {
        Transcribe transcribe = new Transcribe();

        workingMemory.execute(transcribe);

        assertNotNull(transcribe.getStatus());

        LOG.debug("Status: " + transcribe.getStatus());
    }

    @Test
    public void language() throws Exception
    {
        Transcribe transcribe = new Transcribe();

        workingMemory.execute(transcribe);

        assertNotNull(transcribe.getLanguage());

        LOG.debug("Language: " + transcribe.getLanguage());
    }
}
