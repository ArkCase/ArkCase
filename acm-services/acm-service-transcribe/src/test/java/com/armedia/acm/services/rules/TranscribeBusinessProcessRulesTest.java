package com.armedia.acm.services.rules;

/*-
 * #%L
 * ACM Service: Transcribe
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 *
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.services.mediaengine.model.MediaEngineBusinessProcessModel;
import com.armedia.acm.services.mediaengine.model.MediaEngineType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public class TranscribeBusinessProcessRulesTest
{

    private Logger LOG = LogManager.getLogger(getClass());
    private StatelessKieSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-transcribe-business-process-rules.xlsx");
        assertTrue(xls.exists());

        String drl = sc.compile(xls.getInputStream(), InputType.XLS);

        LOG.info("DRL: {}", drl);

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

        workingMemory = kbuilder.newKieBase().newStatelessKieSession();

        assertNotNull(workingMemory);

    }

    @Test
    public void businessProcess_Automatic_Start() throws Exception
    {
        MediaEngineBusinessProcessModel model = new MediaEngineBusinessProcessModel();
        model.setType(MediaEngineType.AUTOMATIC.toString());

        workingMemory.execute(model);

        assertNotNull(model.isStart());

        LOG.debug("Start: {}", model.isStart());
    }

    @Test
    public void businessProcess_Automatic_Name() throws Exception
    {
        MediaEngineBusinessProcessModel model = new MediaEngineBusinessProcessModel();
        model.setType(MediaEngineType.AUTOMATIC.toString());

        workingMemory.execute(model);

        assertNotNull(model.isStart());

        LOG.debug("Name: {}", model.getName());
    }

    @Test
    public void businessProcess_Manual_Start() throws Exception
    {
        MediaEngineBusinessProcessModel model = new MediaEngineBusinessProcessModel();
        model.setType(MediaEngineType.MANUAL.toString());

        workingMemory.execute(model);

        assertNotNull(model.isStart());

        LOG.debug("Start: {}", model.isStart());
    }

    @Test
    public void businessProcess_Manual_Name() throws Exception
    {
        MediaEngineBusinessProcessModel model = new MediaEngineBusinessProcessModel();
        model.setType(MediaEngineType.MANUAL.toString());

        workingMemory.execute(model);

        assertNotNull(model.isStart());

        LOG.debug("Name: {}", model.getName());
    }
}