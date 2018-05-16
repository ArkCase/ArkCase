package com.armedia.acm.services.transcribe.rules;

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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeType;

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
        LOG.info("DRL: {}" + drl);

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

        LOG.debug("Remote ID: {}", transcribe.getRemoteId());
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

        LOG.debug("Status: {}", transcribe.getStatus());
    }

    @Test
    public void language() throws Exception
    {
        Transcribe transcribe = new Transcribe();

        workingMemory.execute(transcribe);

        assertNotNull(transcribe.getLanguage());

        LOG.debug("Language: {}", transcribe.getLanguage());
    }
}
