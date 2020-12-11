package com.armedia.acm.plugins.casefile.service;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.plugins.casefile.model.CaseFile;

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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import gov.foia.model.FOIARequest;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on December, 2020
 */
public class SplitRequestRulesTest
{

    private Logger log = LogManager.getLogger(getClass());
    private StatelessKieSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-split-case-file-rules-foia.xlsx");
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

        workingMemory = kbuilder.newKieBase().newStatelessKieSession();

        assertNotNull(workingMemory);
    }

    @Test
    public void checkSplitRequestRules()
    {
        FOIARequest originalRequest = new FOIARequest();

        originalRequest.setTitle("REQ_2020_00001");
        originalRequest.setRequestType("New Request");
        originalRequest.setRequestCategory("Commercial Use");
        originalRequest.setDeliveryMethodOfResponse("Email");
        originalRequest.setDetails("Some details");
        originalRequest.setRequestTrack("Simple");
        originalRequest.setComponentAgency("FOIA Agency");

        originalRequest.setRecordSearchDateFrom(LocalDateTime.now().minusDays(10));
        originalRequest.setRecordSearchDateTo(LocalDateTime.now().plusDays(10));
        originalRequest.setExpediteFlag(false);

        FOIARequest copyRequest = new FOIARequest();

        Map<String, CaseFile> caseFiles = new HashMap<>();
        caseFiles.put("source", originalRequest);
        caseFiles.put("copy", copyRequest);

        workingMemory.execute(caseFiles);

        assertEquals(originalRequest.getTitle(), copyRequest.getTitle());
        assertEquals(originalRequest.getRequestType(), copyRequest.getRequestType());
        assertEquals(originalRequest.getRequestCategory(), copyRequest.getRequestCategory());
        assertEquals(originalRequest.getDeliveryMethodOfResponse(), copyRequest.getDeliveryMethodOfResponse());
        assertEquals(originalRequest.getRequestTrack(), copyRequest.getRequestTrack());
        assertEquals(originalRequest.getRecordSearchDateFrom(), copyRequest.getRecordSearchDateFrom());
        assertEquals(originalRequest.getRecordSearchDateTo(), copyRequest.getRecordSearchDateTo());
        assertEquals(originalRequest.getExpediteFlag(), copyRequest.getExpediteFlag());
    }

}
