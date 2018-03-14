package com.armedia.acm.services.transcribe.rules;

import com.armedia.acm.services.transcribe.model.TranscribeBusinessProcessModel;
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
import static org.junit.Assert.assertTrue;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/06/2018
 */
public class TranscribeBusinessProcessRulesTest
{

    private Logger LOG = LoggerFactory.getLogger(getClass());
    private StatelessKnowledgeSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-transcribe-business-process-rules.xlsx");
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
    public void businessProcess_Automatic_Start() throws Exception
    {
        TranscribeBusinessProcessModel model = new TranscribeBusinessProcessModel();
        model.setType(TranscribeType.AUTOMATIC.toString());

        workingMemory.execute(model);

        assertNotNull(model.isStart());

        LOG.debug("Start: " + model.isStart());
    }

    @Test
    public void businessProcess_Automatic_Name() throws Exception
    {
        TranscribeBusinessProcessModel model = new TranscribeBusinessProcessModel();
        model.setType(TranscribeType.AUTOMATIC.toString());

        workingMemory.execute(model);

        assertNotNull(model.getName());

        LOG.debug("Name: " + model.getName());
    }

    @Test
    public void businessProcess_Manual_Start() throws Exception
    {
        TranscribeBusinessProcessModel model = new TranscribeBusinessProcessModel();
        model.setType(TranscribeType.MANUAL.toString());

        workingMemory.execute(model);

        assertNotNull(model.isStart());

        LOG.debug("Start: " + model.isStart());
    }

    @Test
    public void businessProcess_Manual_Name() throws Exception
    {
        TranscribeBusinessProcessModel model = new TranscribeBusinessProcessModel();
        model.setType(TranscribeType.MANUAL.toString());

        workingMemory.execute(model);

        assertNotNull(model.getName());

        LOG.debug("Name: " + model.getName());
    }
}
