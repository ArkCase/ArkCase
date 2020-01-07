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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.objectonverter.json.JSONUnmarshaller;
import com.armedia.acm.plugins.businessprocess.model.EnterQueueModel;
import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;

import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.junit.Before;
import org.junit.Ignore;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.foia.model.FOIARequest;

/**
 * Created by dmiller on 8/8/16.
 */
public class CaseFileEnterQueueBusinessRuleTest
{

    private Logger log = LogManager.getLogger(getClass());
    private StatelessKnowledgeSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-enter-queue-rules-foia.xlsx");
        assertTrue(xls.exists());

        String drl = sc.compile(xls.getInputStream(), InputType.XLS);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        kbuilder.add(ResourceFactory.newInputStreamResource(xls.getInputStream()), ResourceType.DTABLE, dtconf);

        if (kbuilder.hasErrors())
        {
            for (KnowledgeBuilderError error : kbuilder.getErrors())
            {
                System.out.println("DRL: " + drl);
                log.error("Error building rules: " + error);
            }

            throw new RuntimeException("Could not build rules from " + xls.getFile().getAbsolutePath());
        }

        workingMemory = kbuilder.newKnowledgeBase().newStatelessKnowledgeSession();

        assertNotNull(workingMemory);
    }

    private FOIARequest buildFoiaRequest(String currentQueueName)
    {
        FOIARequest foiaRequest = new FOIARequest();
        foiaRequest.setQueue(new AcmQueue());
        foiaRequest.getQueue().setName(currentQueueName);
        return foiaRequest;
    }

    @Test
    @Ignore
    public void fulfill_requiredFieldsMissing() throws Exception
    {
        verifyStandardRequiredFields_missing("Intake", "Fulfill");
    }

    @Test
    @Ignore
    public void approve_requiredFieldsMissing() throws Exception
    {
        verifyStandardRequiredFields_missing("Fulfill", "Approve");
    }

    @Test
    @Ignore
    public void fulfill_requiredFieldsPresent() throws Exception
    {
        verifyStandardRequiredFields_present("Intake", "Fulfill");
    }

    @Test
    @Ignore
    public void generalCounselQueue_noRequiredFields_notAppeal_notLigitation() throws Exception
    {
        FOIARequest fr = buildFoiaRequest("Approve");

        String[] standardErrors = errorsForStandardRequiredFields();

        String[] gcErrors = new String[] { "Litigation flag must be checked" };

        List<String> errors = new ArrayList<>();
        errors.addAll(Arrays.asList(standardErrors));
        errors.addAll(Arrays.asList(gcErrors));

        verifyErrorMessages(fr, "General Counsel", errors.toArray(new String[errors.size()]));
    }

    @Test
    public void generalCounselQueue_withRequiredFields_notAppeal_notLigitation() throws Exception
    {
        FOIARequest fr = buildFoiaRequest("Approve");

        populateStandardRequiredFields(fr);

        fr.setRequestType("New Request");
        fr.setLitigationFlag(Boolean.FALSE);

        String[] gcErrors = new String[] { "Litigation flag must be checked" };

        verifyErrorMessages(fr, "General Counsel", gcErrors);
    }

    @Test
    @Ignore
    public void generalCounselQueue_withRequiredFields_appeal_notLigitation() throws Exception
    {
        FOIARequest fr = buildFoiaRequest("Approve");

        populateStandardRequiredFields(fr);

        fr.setRequestType("Appeal");
        fr.setLitigationFlag(Boolean.FALSE);

        String[] gcErrors = new String[] {};

        verifyErrorMessages(fr, "General Counsel", gcErrors);
    }

    @Test
    @Ignore
    public void generalCounselQueue_withRequiredFields_notAppeal_ligitation() throws Exception
    {
        FOIARequest fr = buildFoiaRequest("Approve");

        populateStandardRequiredFields(fr);

        fr.setRequestType("New Request");
        fr.setLitigationFlag(Boolean.TRUE);

        String[] gcErrors = new String[] {};

        verifyErrorMessages(fr, "General Counsel", gcErrors);
    }

    @Test
    @Ignore
    public void generalCounselQueue_withRequiredFields_appeal_ligitation() throws Exception
    {
        FOIARequest fr = buildFoiaRequest("Approve");

        populateStandardRequiredFields(fr);

        fr.setRequestType("Appeal");
        fr.setLitigationFlag(Boolean.TRUE);

        String[] gcErrors = new String[] {};

        verifyErrorMessages(fr, "General Counsel", gcErrors);
    }

    @Test
    @Ignore
    public void billingCounselQueue_noRequiredFields_feeWaiver() throws Exception
    {
        FOIARequest fr = buildFoiaRequest("General Counsel");

        fr.setFeeWaiverFlag(Boolean.TRUE);

        String[] standardErrors = errorsForStandardRequiredFields();

        String[] billingErrors = new String[] { "The fee waiver flag must not be checked" };

        List<String> errors = new ArrayList<>();
        errors.addAll(Arrays.asList(standardErrors));
        errors.addAll(Arrays.asList(billingErrors));

        // we have set the Fee Waiver Flag, which is one of the required fields; so we should remove the fee waiver
        // error from the expected list
        errors.remove("Fee waiver flag is required");

        verifyErrorMessages(fr, "Billing", errors.toArray(new String[errors.size()]));
    }

    @Test
    public void billingQueue_withRequiredFields_feeWaiver() throws Exception
    {
        FOIARequest fr = buildFoiaRequest("General Counsel");

        populateStandardRequiredFields(fr);

        fr.setFeeWaiverFlag(Boolean.TRUE);

        String[] billingErrors = new String[] { "The fee waiver flag must not be checked" };

        verifyErrorMessages(fr, "Billing", billingErrors);
    }

    @Test
    @Ignore
    public void billingQueueToReleaseQueue_noRequiredFields_notPaid() throws Exception
    {
        FOIARequest fr = buildFoiaRequest("Billing");

        fr.setPaidFlag(Boolean.FALSE);

        String[] standardErrors = errorsForStandardRequiredFields();

        String[] releaseErrors = new String[] { "The paid flag must be checked" };

        List<String> errors = new ArrayList<>();
        errors.addAll(Arrays.asList(standardErrors));
        errors.addAll(Arrays.asList(releaseErrors));

        verifyErrorMessages(fr, "Release", errors.toArray(new String[errors.size()]));
    }

    @Test
    @Ignore
    public void billingQueueToReleaseQueue_noRequiredFields_paid() throws Exception
    {
        FOIARequest fr = buildFoiaRequest("Billing");

        fr.setPaidFlag(Boolean.TRUE);

        String[] standardErrors = errorsForStandardRequiredFields();

        String[] releaseErrors = new String[] {};

        List<String> errors = new ArrayList<>();
        errors.addAll(Arrays.asList(standardErrors));
        errors.addAll(Arrays.asList(releaseErrors));

        verifyErrorMessages(fr, "Release", errors.toArray(new String[errors.size()]));
    }

    @Test
    @Ignore
    public void billingQueueToReleaseQueue_withRequiredFields_notPaid() throws Exception
    {
        FOIARequest fr = buildFoiaRequest("Billing");

        populateStandardRequiredFields(fr);

        fr.setPaidFlag(Boolean.FALSE);

        String[] releaseErrors = new String[] { "The paid flag must be checked" };

        List<String> errors = new ArrayList<>();
        errors.addAll(Arrays.asList(releaseErrors));

        verifyErrorMessages(fr, "Release", errors.toArray(new String[errors.size()]));
    }

    @Test
    public void billingQueueToReleaseQueue_withRequiredFields_paid() throws Exception
    {
        FOIARequest fr = buildFoiaRequest("Billing");

        populateStandardRequiredFields(fr);

        fr.setPaidFlag(Boolean.TRUE);

        String[] releaseErrors = new String[] {};

        List<String> errors = new ArrayList<>();
        errors.addAll(Arrays.asList(releaseErrors));

        verifyErrorMessages(fr, "Release", errors.toArray(new String[errors.size()]));
    }

    @Test
    public void billingQueueToHoldQueue_noRequiredFields_notPaid()
    {
        FOIARequest fr = buildFoiaRequest("Billing");

        fr.setPaidFlag(Boolean.FALSE);

        // the hold queue doesn't have any required fields so we won't have any required field errors.

        String[] standardErrors = new String[] {};

        String[] holdErrors = new String[] {};

        List<String> errors = new ArrayList<>();
        errors.addAll(Arrays.asList(standardErrors));
        errors.addAll(Arrays.asList(holdErrors));

        verifyErrorMessages(fr, "Hold", errors.toArray(new String[errors.size()]));
    }

    @Test
    @Ignore
    public void billingQueueToHoldQueue_noRequiredFields_paid()
    {
        FOIARequest fr = buildFoiaRequest("Billing");

        fr.setPaidFlag(Boolean.TRUE);

        // the hold queue doesn't have any required fields so we won't have any required field errors.

        String[] standardErrors = new String[] {};

        String[] holdErrors = new String[] { "The paid flag must not be checked" };

        List<String> errors = new ArrayList<>();
        errors.addAll(Arrays.asList(standardErrors));
        errors.addAll(Arrays.asList(holdErrors));

        verifyErrorMessages(fr, "Hold", errors.toArray(new String[errors.size()]));
    }

    @Test
    public void billingQueueToHoldQueue_withRequiredFields_notPaid() throws Exception
    {
        FOIARequest fr = buildFoiaRequest("Billing");

        populateStandardRequiredFields(fr);

        fr.setPaidFlag(Boolean.FALSE);

        String[] holdErrors = new String[] {};

        List<String> errors = new ArrayList<>();
        errors.addAll(Arrays.asList(holdErrors));

        verifyErrorMessages(fr, "Hold", errors.toArray(new String[errors.size()]));
    }

    @Test
    @Ignore
    public void billingQueueToHoldQueue_withRequiredFields_paid() throws Exception
    {
        FOIARequest fr = buildFoiaRequest("Billing");

        populateStandardRequiredFields(fr);

        fr.setPaidFlag(Boolean.TRUE);

        String[] holdErrors = new String[] { "The paid flag must not be checked" };

        List<String> errors = new ArrayList<>();
        errors.addAll(Arrays.asList(holdErrors));

        verifyErrorMessages(fr, "Hold", errors.toArray(new String[errors.size()]));
    }

    private void verifyStandardRequiredFields_missing(String currentQueue, String nextQueue)
    {
        FOIARequest fr = buildFoiaRequest(currentQueue);

        String[] expectedErrors = errorsForStandardRequiredFields();

        verifyErrorMessages(fr, nextQueue, expectedErrors);
    }

    private String[] errorsForStandardRequiredFields()
    {
        return new String[] { "Request type is required",
                "Expedite flag is required",
                "Fee waiver flag is required",
                "Litigation flag is required",
                "Litigation flag must be checked",
                "The fee waiver flag must not be checked",
                "Disposition Type is required",
                "Disposition SubType is required",
                "Disposition \"Other\" reason is required",
                "Disposition Type is required",
                "Disposition SubType is required",
                "Disposition \"Other\" reason is required",
                "Executive Group is required",
                "Received date is required"
        };
    }

    private void verifyStandardRequiredFields_present(String currentQueue, String nextQueue) throws Exception
    {
        FOIARequest fr = buildFoiaRequest(currentQueue);

        populateStandardRequiredFields(fr);

        String[] expectedErrors = new String[] {};

        verifyErrorMessages(fr, nextQueue, expectedErrors);
    }

    private void populateStandardRequiredFields(FOIARequest fr) throws Exception
    {
        // This JSON was collected from the webapp on 2016-08-15
        String foiaRequestJson = "{\"className\":\"gov.foia.model.FOIARequest\",\"title\":\"Angular 7\","
                + "\"requestCategory\":\"Official Personnel File\",\"details\":\"detail\",\"requestSubType\":"
                + "\"FOIA\",\"requestType\":\"New Request\",\"personAssociations\":[{\"className\":"
                + "\"gov.foia.model.FOIARequesterAssociation\",\"personType\":\"Requester\",\"parentType\":"
                + "\"CASE_FILE\",\"person\":{\"className\":\"com.armedia.acm.plugins.person.model.Person\","
                + "\"givenName\":\"Bill\",\"familyName\":\"Kreutzmann\",\"addresses\":[{\"className\":\"com.armedia.acm.plugins.addressable.model.PostalAddress\",\"type\":\"Business\","
                + "\"country\":\"USA\",\"state\":\"CA\",\"zip\":\"94512\",\"city\":\"San Francisco\",\"streetAddress\":"
                + "\"710 Haight Ashbury St.\"}],\"contactMethods\":[{\"className\":\"com.armedia.acm.plugins.addressable.model.ContactMethod\",\"type\":\"Phone\",\"value\":\"\"},"
                + "{\"className\":\"com.armedia.acm.plugins.addressable.model.ContactMethod\",\"type\":\"Fax\",\"value\":\"\"},"
                + "{\"className\":\"com.armedia.acm.plugins.addressable.model.ContactMethod\",\"type\":\"Email\",\"value\":\"\"}],\"organizations\":[{\"className\":"
                + "\"com.armedia.acm.plugins.person.model.Organization\",\"organizationType\":\"Corporation\","
                + "\"organizationValue\":\"Grateful Dead\",\"addresses\":[{\"className\":\"com.armedia.acm.plugins.addressable.model.PostalAddress\",\"type\":\"Business\",\"country\":\"USA\","
                + "\"state\":\"CA\",\"zip\":\"94134\",\"city\":\"San Francisco\",\"streetAddress\":\"123 Main St.\"}]}]},"
                + "\"requesterSource\":\"self\"}],\"feeWaiverFlag\":true,\"expediteFlag\":true,\"litigationFlag\":true}";

        JSONUnmarshaller unmarshaller = ObjectConverter.createJSONUnmarshallerForTests();
        ;
        FOIARequest request = unmarshaller.unmarshall(foiaRequestJson, FOIARequest.class);

        fr.getPersonAssociations().addAll(request.getPersonAssociations());
        fr.setRequestCategory(request.getRequestCategory());
        fr.setRequestType(request.getRequestType());
        fr.setRequestSubType(request.getRequestSubType());
        fr.setDetails(request.getDetails());
        fr.setFeeWaiverFlag(request.getFeeWaiverFlag());
        fr.setExpediteFlag(request.getExpediteFlag());
        fr.setLitigationFlag(request.getLitigationFlag());

        // ((FOIARequesterAssociation) request.getPersonAssociations().get(0)).setRequesterSource("Self");
        //
        // PostalAddress orgAddress = new PostalAddress();
        // orgAddress.setStreetAddress("123 Main St.");
        // orgAddress.setCity("El Paso");
        // orgAddress.setState("TX");
        // orgAddress.setZip("75414");
        // fr.getPersonAssociations().get(0).getPerson().getOrganizations().get(0).getAddresses().add(orgAddress);
        //
        // fr.setExpediteFlag(Boolean.TRUE);
        // fr.setFeeWaiverFlag(Boolean.FALSE);
        // fr.setLitigationFlag(Boolean.TRUE);
    }

    private void verifyErrorMessages(FOIARequest foiaRequest, String enqueueName, String[] expectedErrors)
    {
        CaseFilePipelineContext context = new CaseFilePipelineContext();
        context.setQueueName(foiaRequest.getQueue().getName());
        context.setEnqueueName(enqueueName);

        EnterQueueModel<FOIARequest, CaseFilePipelineContext> enterQueueModel = new EnterQueueModel<>();
        enterQueueModel.setBusinessObject(foiaRequest);
        enterQueueModel.setPipelineContext(context);

        workingMemory.execute(enterQueueModel);

        boolean isEmpty = expectedErrors.length == 0;
        assertThat(isEmpty ? "Should not have any messages" : "Should have some messages",
                enterQueueModel.getCannotEnterReasons().isEmpty(), is(isEmpty));

        // enterQueueModel.getCannotEnterReasons().stream().forEach(r -> System.out.println(r));

        assertEquals(expectedErrors.length, enterQueueModel.getCannotEnterReasons().size());

        for (String next : expectedErrors)
        {
            assertTrue("Error messages should contain '" + next + "'", enterQueueModel.getCannotEnterReasons().contains(next));
        }

    }
}
