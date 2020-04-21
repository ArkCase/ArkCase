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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.plugins.businessprocess.model.OnEnterQueueModel;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;

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

import gov.foia.model.FOIARequest;

/**
 * Created by dmiller on 8/9/16.
 */
public class CaseFileOnEnterQueueBusinessRuleTest
{
    private Logger log = LogManager.getLogger(getClass());
    private StatelessKieSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-on-enter-queue-rules-foia.xlsx");
        assertTrue(xls.exists());

        String drl = sc.compile(xls.getInputStream(), InputType.XLS);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        kbuilder.add(ResourceFactory.newInputStreamResource(xls.getInputStream()), ResourceType.DTABLE, dtconf);

        if (kbuilder.hasErrors())
        {

            System.out.println("DRL: " + drl);

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
    public void enterFulfillQueue() throws Exception
    {
        OnEnterQueueModel<FOIARequest, CaseFilePipelineContext> model = buildEnterQueueModel("Fulfill");

        workingMemory.execute(model);

        assertEquals("foia-extension-fulfill-process", model.getBusinessProcessName());
    }

    @Test
    public void enterHoldQueue() throws Exception
    {
        OnEnterQueueModel<FOIARequest, CaseFilePipelineContext> model = buildEnterQueueModel("Hold");

        workingMemory.execute(model);

        assertEquals("foia-extension-hold-process", model.getBusinessProcessName());
    }

    @Test
    public void enterApproveQueue() throws Exception
    {
        OnEnterQueueModel<FOIARequest, CaseFilePipelineContext> model = buildEnterQueueModel("Approve");

        workingMemory.execute(model);

        assertEquals("foia-extension-approve-process", model.getBusinessProcessName());
    }

    @Test
    public void enterBillingQueue() throws Exception
    {
        OnEnterQueueModel<FOIARequest, CaseFilePipelineContext> model = buildEnterQueueModel("Billing");

        workingMemory.execute(model);

        assertEquals("foia-extension-billing-process", model.getBusinessProcessName());
    }

    @Test
    public void enterGeneralCounselQueue() throws Exception
    {
        OnEnterQueueModel<FOIARequest, CaseFilePipelineContext> model = buildEnterQueueModel("General Counsel");

        workingMemory.execute(model);

        assertEquals("foia-extension-generalcounsel-process", model.getBusinessProcessName());
    }

    @Test
    public void enterReleaseQueue() throws Exception
    {
        OnEnterQueueModel<FOIARequest, CaseFilePipelineContext> model = buildEnterQueueModel("Release");

        workingMemory.execute(model);

        assertEquals("foia-extension-release-process", model.getBusinessProcessName());
    }

    @Test
    public void enterDeleteQueue() throws Exception
    {
        OnEnterQueueModel<FOIARequest, CaseFilePipelineContext> model = buildEnterQueueModel("Delete");

        workingMemory.execute(model);

        assertEquals("foia-extension-delete-process", model.getBusinessProcessName());
    }

    @Test
    public void enterIntakeQueue() throws Exception
    {
        OnEnterQueueModel<FOIARequest, CaseFilePipelineContext> model = buildEnterQueueModel("Intake");

        workingMemory.execute(model);

        assertEquals("foia-extension-intake-process", model.getBusinessProcessName());
    }

    private OnEnterQueueModel<FOIARequest, CaseFilePipelineContext> buildEnterQueueModel(String enqueueName)
    {
        CaseFilePipelineContext ctx = new CaseFilePipelineContext();
        ctx.setEnqueueName(enqueueName);

        OnEnterQueueModel<FOIARequest, CaseFilePipelineContext> enterQueueModel = new OnEnterQueueModel<>();
        enterQueueModel.setBusinessObject(new FOIARequest());
        enterQueueModel.setPipelineContext(ctx);

        return enterQueueModel;
    }
}
