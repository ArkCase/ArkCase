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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.plugins.businessprocess.model.NextPossibleQueuesModel;
import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;

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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import gov.foia.model.FOIARequest;

public class CaseFileNextPossibleQueuesBusinessRuleTest
{

    private Logger log = LogManager.getLogger(getClass());
    private StatelessKnowledgeSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-next-possible-queues-rules-foia.xlsx");
        assertTrue(xls.exists());

        String drl = sc.compile(xls.getInputStream(), InputType.XLS);
        System.out.println("DRL: " + drl);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        kbuilder.add(ResourceFactory.newInputStreamResource(xls.getInputStream()), ResourceType.DTABLE, dtconf);

        if (kbuilder.hasErrors())
        {
            for (KnowledgeBuilderError error : kbuilder.getErrors())
            {
                log.error("Error building rules: {}", error);
            }

            throw new RuntimeException("Could not build rules from " + xls.getFile().getAbsolutePath());
        }

        workingMemory = kbuilder.newKnowledgeBase().newStatelessKnowledgeSession();

        assertNotNull(workingMemory);
    }

    public FOIARequest buildFOIARequest(String currentQueue)
    {
        FOIARequest foiaRequest = new FOIARequest();
        foiaRequest.setCaseType("Investigative");
        AcmQueue queue = new AcmQueue();
        queue.setName(currentQueue);
        foiaRequest.setQueue(queue);

        return foiaRequest;
    }

    @Test
    public void nextQueuesAfterIntakeQueue() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Intake");
        verifyNextQueues(fr, "Fulfill,Hold,Approve", "Fulfill", null);
    }

    @Test
    public void nextQueuesAfterFulfillQueue() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Fulfill");
        verifyNextQueues(fr, "Hold,Approve", "Approve", null);
    }

    @Test
    public void nextQueuesAfterHoldQueue() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Hold");
        verifyNextQueues(fr, "Fulfill,Approve", "Fulfill", null);
    }

    @Test
    public void nextQueuesAfterAppealQueue() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Appeal");
        verifyNextQueues(fr, "Fulfill,Approve", "Fulfill", null);
    }

    @Test
    public void nextQueuesAfterApproveQueue_litigationAndNotDeniedFlag_generalCounsel() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Approve");

        fr.setLitigationFlag(Boolean.TRUE);
        fr.setDeniedFlag(Boolean.FALSE);

        verifyNextQueues(fr, "General Counsel,Hold,Fulfill", "General Counsel", "Fulfill");
    }

    @Test
    public void nextQueuesAfterApproveQueue_deniedFlag_release() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Approve");

        fr.setDeniedFlag(Boolean.TRUE);

        verifyNextQueues(fr, "Release,Fulfill", "Release", "Fulfill");
    }

    @Test
    public void nextQueuesAfterApproveQueue_notLitigationAndFeeWaiverAndNotDeniedFlag_release() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Approve");

        fr.setLitigationFlag(Boolean.FALSE);
        fr.setFeeWaiverFlag(Boolean.TRUE);
        fr.setDeniedFlag(Boolean.FALSE);

        verifyNextQueues(fr, "Release,Hold,Fulfill", "Release", "Fulfill");
    }

    @Test
    public void nextQueuesAfterApproveQueue_notLitigationAndNotFeeWaiverAndNotDeniedFlag_billing() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Approve");

        fr.setLitigationFlag(Boolean.FALSE);
        fr.setFeeWaiverFlag(Boolean.FALSE);
        fr.setDeniedFlag(Boolean.FALSE);
        verifyNextQueues(fr, "Billing,Hold,Fulfill", "Billing", "Fulfill");
    }

    @Test
    public void nextQueuesAfterGeneralCounselQueue_deniedFlag_approve() throws Exception
    {
        FOIARequest fr = buildFOIARequest("General Counsel");

        fr.setDeniedFlag(Boolean.TRUE);

        verifyNextQueues(fr, "Approve,Fulfill", "Approve", "Fulfill");
    }

    @Test
    public void nextQueuesAfterGeneralCounselQueue_notFeeWaiverAndNotDeniedFlag_billing() throws Exception
    {
        FOIARequest fr = buildFOIARequest("General Counsel");

        fr.setFeeWaiverFlag(Boolean.FALSE);
        fr.setDeniedFlag(Boolean.FALSE);

        verifyNextQueues(fr, "Billing,Hold,Fulfill,Approve", "Billing", "Fulfill");
    }

    @Test
    public void nextQueuesAfterGeneralCounselQueue_FeeWaiverAndNotDeniedFlag_release() throws Exception
    {
        FOIARequest fr = buildFOIARequest("General Counsel");

        fr.setFeeWaiverFlag(Boolean.TRUE);
        fr.setDeniedFlag(Boolean.FALSE);

        verifyNextQueues(fr, "Release,Hold,Fulfill,Approve", "Release", "Fulfill");
    }

    @Test
    public void nextQueuesAfterBillingQueue_deniedFlag_approve() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Billing");

        fr.setDeniedFlag(Boolean.TRUE);

        verifyNextQueues(fr, "Approve,Fulfill", "Approve", "Fulfill");
    }

    @Test
    public void nextQueuesAfterBillingQueue_notDeniedFlag_release() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Billing");
        fr.setDeniedFlag(Boolean.FALSE);

        verifyNextQueues(fr, "Release,Fulfill,Approve", "Release", "Fulfill");
    }


    private void verifyNextQueues(FOIARequest foiaRequest, String expectedNextQueues, String defaultNextQueue, String defaultReturnQueue)
    {
        CaseFilePipelineContext context = new CaseFilePipelineContext();
        context.setQueueName(foiaRequest.getQueue().getName());

        NextPossibleQueuesModel<FOIARequest, CaseFilePipelineContext> businessProcessModel = new NextPossibleQueuesModel<>();
        businessProcessModel.setBusinessObject(foiaRequest);
        businessProcessModel.setPipelineContext(context);

        workingMemory.execute(businessProcessModel);

        String[] nextQueues = expectedNextQueues == null ? new String[0] : expectedNextQueues.split(",");

        boolean isEmpty = nextQueues.length == 0;
        assertThat(isEmpty ? "Should not have any queues" : "Should have some queues",
                businessProcessModel.getNextPossibleQueues().isEmpty(), is(isEmpty));

        assertEquals(nextQueues.length, businessProcessModel.getNextPossibleQueues().size());

        for (String next : nextQueues)
        {
            assertTrue("Queue list should contain '" + next + "'", businessProcessModel.getNextPossibleQueues().contains(next));
        }

        if (defaultNextQueue != null)
        {
            assertTrue(businessProcessModel.getDefaultNextQueue().equals(defaultNextQueue));
        }
        else
        {
            assertThat(businessProcessModel.getDefaultNextQueue(), nullValue());
        }

        if (defaultReturnQueue != null)
        {
            assertTrue(businessProcessModel.getDefaultReturnQueue().equals(defaultReturnQueue));
        }
        else
        {
            assertThat(businessProcessModel.getDefaultReturnQueue(), nullValue());
        }

    }
}
