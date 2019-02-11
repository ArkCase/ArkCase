package com.armedia.acm.services.mediaengine.rules;

/*-
 * #%L
 * ACM Service: Media Engine
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.mediaengine.model.MediaEngineType;

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

import java.io.IOException;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class MediaEngineRulesTest
{

    private Logger LOG = LoggerFactory.getLogger(getClass());
    private StatelessKnowledgeSession transcribeWorkingMemory;
    private StatelessKnowledgeSession ocrWorkingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource transcribeXLS = new ClassPathResource("/rules/drools-transcribe-rules.xlsx");
        assertTrue(transcribeXLS.exists());

        transcribeWorkingMemory = getWorkingMemory(sc, transcribeXLS);

        assertNotNull(transcribeWorkingMemory);

        Resource ocrXLS = new ClassPathResource("/rules/drools-ocr-rules.xlsx");
        assertTrue(ocrXLS.exists());

        ocrWorkingMemory = getWorkingMemory(sc, ocrXLS);

        assertNotNull(transcribeWorkingMemory);
    }

    @Test
    public void remoteId_Automatic() throws Exception
    {
        MediaEngine mediaEngine = getMediaEngine(MediaEngineType.AUTOMATIC);

        transcribeWorkingMemory.execute(mediaEngine);

        assertNotNull(mediaEngine.getRemoteId());

        mediaEngine = getMediaEngine(MediaEngineType.AUTOMATIC);

        ocrWorkingMemory.execute(mediaEngine);

        assertNotNull(mediaEngine.getRemoteId());

        LOG.debug("Remote ID: {}", mediaEngine.getRemoteId());
    }

    @Test
    public void remoteId_Manual() throws Exception
    {
        MediaEngine mediaEngine = getMediaEngine(MediaEngineType.MANUAL);

        transcribeWorkingMemory.execute(mediaEngine);

        assertNull(mediaEngine.getRemoteId());
    }

    @Test
    public void status() throws Exception
    {
        MediaEngine mediaEngine = new MediaEngine();

        transcribeWorkingMemory.execute(mediaEngine);

        assertNotNull(mediaEngine.getStatus());

        LOG.debug("Status: {}", mediaEngine.getStatus());
    }

    @Test
    public void language() throws Exception
    {
        MediaEngine mediaEngine = new MediaEngine();

        transcribeWorkingMemory.execute(mediaEngine);

        assertNotNull(mediaEngine.getLanguage());

        LOG.debug("Language: {}", mediaEngine.getLanguage());
    }

    private MediaEngine getMediaEngine(MediaEngineType type)
    {
        MediaEngine mediaEngine = new MediaEngine();
        mediaEngine.setId(101L);
        mediaEngine.setType(type.toString());
        return mediaEngine;
    }

    private StatelessKnowledgeSession getWorkingMemory(SpreadsheetCompiler sc, Resource resource) throws IOException
    {
        String drl = sc.compile(resource.getInputStream(), InputType.XLS);
        LOG.info("DRL: {}" + drl);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        kbuilder.add(ResourceFactory.newInputStreamResource(resource.getInputStream()), ResourceType.DTABLE, dtconf);

        if (kbuilder.hasErrors())
        {
            for (KnowledgeBuilderError error : kbuilder.getErrors())
            {
                LOG.error("Error building rules: " + error);
            }

            throw new RuntimeException("Could not build rules from " + resource.getFile().getAbsolutePath());
        }

        return kbuilder.newKnowledgeBase().newStatelessKnowledgeSession();
    }
}
