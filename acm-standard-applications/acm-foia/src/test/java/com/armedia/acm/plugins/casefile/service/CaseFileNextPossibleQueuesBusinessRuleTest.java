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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import gov.foia.model.FOIARequest;

public class CaseFileNextPossibleQueuesBusinessRuleTest
{

    private Logger log = LoggerFactory.getLogger(getClass());
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
        verifyNextQueues(fr, "Fulfill,Suspend,Hold,Delete", "Fulfill", null);
    }

    @Test
    public void nextQueuesAfterFulfillQueue() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Fulfill");
        verifyNextQueues(fr, "Suspend,Approve,Hold", "Approve", null);
    }

    @Test
    public void nextQueuesAfterHoldQueue() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Hold");
        verifyNextQueues(fr, "Fulfill", "Fulfill", null);
    }

    @Test
    public void nextQueuesAfterSuspendQueue() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Suspend");
        verifyNextQueues(fr, "Fulfill", "Fulfill", null);
    }

    @Test
    public void nextQueuesAfterApproveQueue_litigationAndNotAppealAndNotFeeWaiver_generalCounsel() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Approve");

        fr.setLitigationFlag(Boolean.TRUE);
        fr.setRequestSubType("New Request");
        fr.setFeeWaiverFlag(Boolean.TRUE);

        verifyNextQueues(fr, "General Counsel,Hold", "General Counsel", "Fulfill");
    }

    @Test
    public void nextQueuesAfterApproveQueue_notLitigationAndAppealAndNotFeeWaiver_generalCounsel() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Approve");

        fr.setLitigationFlag(Boolean.FALSE);
        fr.setRequestSubType("Appeal");
        fr.setFeeWaiverFlag(Boolean.TRUE);

        verifyNextQueues(fr, "General Counsel,Hold", "General Counsel", "Fulfill");
    }

    @Test
    public void nextQueuesAfterApproveQueue_litigationAndAppealAndFeeWaiver_generalCounsel() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Approve");

        fr.setLitigationFlag(Boolean.TRUE);
        fr.setRequestSubType("Appeal");
        fr.setFeeWaiverFlag(Boolean.FALSE);

        verifyNextQueues(fr, "General Counsel,Hold", "General Counsel", "Fulfill");
    }

    @Test
    public void nextQueuesAfterApproveQueue_notLitigationAndNotAppealAndFeeWaiver_billing() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Approve");

        fr.setLitigationFlag(Boolean.FALSE);
        fr.setRequestSubType("New Request");
        fr.setFeeWaiverFlag(Boolean.FALSE);

        verifyNextQueues(fr, "Billing,Hold", "Billing", "Fulfill");
    }

    @Test
    public void nextQueuesAfterApproveQueue_straightToRelease() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Approve");

        fr.setLitigationFlag(Boolean.FALSE);
        fr.setRequestSubType("New Request");
        fr.setFeeWaiverFlag(Boolean.TRUE);

        verifyNextQueues(fr, "Release,Hold", "Release", "Fulfill");
    }

    @Test
    public void nextQueuesAfterGeneralCounselQueue_billing() throws Exception
    {
        FOIARequest fr = buildFOIARequest("General Counsel");

        fr.setFeeWaiverFlag(Boolean.FALSE);

        verifyNextQueues(fr, "Billing,Hold", "Billing", "Fulfill");
    }

    @Test
    public void nextQueuesAfterGeneralCounselQueue_release() throws Exception
    {
        FOIARequest fr = buildFOIARequest("General Counsel");

        fr.setFeeWaiverFlag(Boolean.TRUE);

        verifyNextQueues(fr, "Release,Hold", "Release", "Fulfill");
    }

    @Test
    public void nextQueuesAfterBillingQueue_paid_release() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Billing");

        fr.setPaidFlag(Boolean.TRUE);

        verifyNextQueues(fr, "Release", "Release", "Fulfill");
    }

    @Test
    public void nextQueuesAfterBillingQueue_notPaid_hold() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Billing");

        fr.setPaidFlag(Boolean.FALSE);

        verifyNextQueues(fr, "Hold", "Hold", "Fulfill");
    }

    @Test
    public void nextQueuesAfterBillingQueue_release_noQueues() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Release");

        verifyNextQueues(fr, null, null, null);
    }

    @Test
    public void nextQueuesAfterBillingQueue_delete_release() throws Exception
    {
        FOIARequest fr = buildFOIARequest("Delete");

        verifyNextQueues(fr, "Release", "Release", null);
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
