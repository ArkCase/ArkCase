package com.armedia.acm.plugins.casefile.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.plugins.casefile.model.AcmQueue;

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

import java.time.LocalDate;
import java.time.LocalDateTime;

import gov.foia.model.FOIARequest;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 31, 2016
 */
public class CaseFileRuleTest
{
    private Logger log = LogManager.getLogger(getClass());
    private StatelessKieSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-case-file-rules-foia.xlsx");
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
    public void foiaRequest_setCurrentDate_BillingQueueAndBillingEnterDateNull() throws Exception
    {
        FOIARequest request = new FOIARequest();

        AcmQueue queue = new AcmQueue();
        queue.setName("Billing");

        request.setQueue(queue);

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        request.setBillingEnterDate(null);

        assertNull(request.getBillingEnterDate());

        workingMemory.execute(request);

        LocalDateTime setByRule = request.getBillingEnterDate();

        assertTrue(setByRule.isAfter(yesterday));

    }

    @Test
    public void foiaRequest_setCurrentDate_BillingQueueAndBillingEnterDateNotNull() throws Exception
    {
        FOIARequest request = new FOIARequest();

        AcmQueue queue = new AcmQueue();
        queue.setName("Billing");

        request.setQueue(queue);

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        request.setBillingEnterDate(yesterday);

        assertNotNull(request.getBillingEnterDate());

        workingMemory.execute(request);

        LocalDateTime setByRule = request.getBillingEnterDate();

        assertTrue(setByRule.equals(yesterday));

    }

    @Test
    public void foiaRequest_setCurrentDateToNull_NotBillingQueue() throws Exception
    {
        FOIARequest request = new FOIARequest();

        AcmQueue queue = new AcmQueue();
        queue.setName("Hold");

        request.setQueue(queue);

        LocalDateTime now = LocalDateTime.now();
        request.setBillingEnterDate(now);

        assertTrue(request.getBillingEnterDate().equals(now));

        workingMemory.execute(request);

        LocalDateTime setByRule = request.getBillingEnterDate();

        assertNull(setByRule);

    }

    @Test
    public void foiaRequest_setCurrentDate_HoldQueueAndHoldEnterDateNull() throws Exception
    {
        FOIARequest request = new FOIARequest();

        AcmQueue queue = new AcmQueue();
        queue.setName("Hold");

        request.setQueue(queue);

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        request.setHoldEnterDate(null);

        assertNull(request.getHoldEnterDate());

        workingMemory.execute(request);

        LocalDateTime setByRule = request.getHoldEnterDate();

        assertTrue(setByRule.isAfter(yesterday));

    }

    @Test
    public void foiaRequest_setCurrentDate_HoldQueueAndHoldEnterDateNotNull() throws Exception
    {
        FOIARequest request = new FOIARequest();

        AcmQueue queue = new AcmQueue();
        queue.setName("Hold");

        request.setQueue(queue);

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        request.setHoldEnterDate(yesterday);

        assertNotNull(request.getHoldEnterDate());

        workingMemory.execute(request);

        LocalDateTime setByRule = request.getHoldEnterDate();

        assertTrue(setByRule.equals(yesterday));

    }

    @Test
    public void foiaRequest_setCurrentDateToNull_NotHoldQueue() throws Exception
    {
        FOIARequest request = new FOIARequest();

        AcmQueue queue = new AcmQueue();
        queue.setName("Billing");

        request.setQueue(queue);

        LocalDateTime now = LocalDateTime.now();
        request.setHoldEnterDate(now);

        assertTrue(request.getHoldEnterDate().equals(now));

        workingMemory.execute(request);

        LocalDateTime setByRule = request.getHoldEnterDate();

        assertNull(setByRule);

    }

}
