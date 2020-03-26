package com.armedia.acm.assignment;

/*-
 * #%L
 * ACM Standard Application: Law Enforcement
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
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.services.participants.model.AcmParticipant;

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
 * Created by armdev on 4/17/14.
 */
public class AssignmentRulesIT
{

    private Logger log = LogManager.getLogger(getClass());
    private StatelessKieSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-assignment-rules.xlsx");
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

    }

    @Test
    public void complaint_defaultAssignee() throws Exception
    {
        assertNotNull(workingMemory);

        Complaint c = new Complaint();
        c.setStatus("DRAFT");
        c.setCreator("testUser");

        log.debug("object type: " + c.getObjectType());

        assertTrue(c.getParticipants().isEmpty());

        workingMemory.execute(c);

        for (AcmParticipant p : c.getParticipants())
        {
            log.error("name: {}, type: {}", p.getParticipantLdapId(), p.getParticipantType());
        }

        int numExpectedParticipants = 4;
        assertEquals(numExpectedParticipants, c.getParticipants().size());

        assertEquals("samuel-acm", c.getParticipants().get(0).getParticipantLdapId());
        assertEquals("assignee", c.getParticipants().get(0).getParticipantType());
        assertEquals("*", c.getParticipants().get(1).getParticipantLdapId());
        assertEquals("*", c.getParticipants().get(1).getParticipantType());
        assertEquals("owning group", c.getParticipants().get(2).getParticipantType());
        assertEquals("ACM_INVESTIGATOR_DEV", c.getParticipants().get(2).getParticipantLdapId());
        assertEquals("reader", c.getParticipants().get(3).getParticipantType());
        assertEquals("testUser", c.getParticipants().get(3).getParticipantLdapId());

        // since we have participants now, if we run the rule again, it should not add any more
        workingMemory.execute(c);
        assertEquals(numExpectedParticipants, c.getParticipants().size());

    }

    @Test
    public void caseFile_creatorHasReader() throws Exception
    {
        assertNotNull(workingMemory);

        CaseFile cf = new CaseFile();
        cf.setCreator("test-creator");

        workingMemory.execute(cf);

        for (AcmParticipant ap : cf.getParticipants())
        {
            System.out.println("id: " + ap.getParticipantLdapId());
        }

        assertEquals(1, cf.getParticipants().stream()
                .filter(ap -> ap.getParticipantLdapId().equals(cf.getCreator()) && ap.getParticipantType().equals("reader")).count());
    }

    @Test
    public void caseFile_doNotAddAnotherCreator() throws Exception
    {
        assertNotNull(workingMemory);

        CaseFile cf = new CaseFile();
        cf.setCreator("test-creator");

        AcmParticipant creator = new AcmParticipant();
        creator.setParticipantLdapId(cf.getCreator());
        creator.setParticipantType("reader");
        creator.setCreator(cf.getCreator());
        cf.getParticipants().add(creator);

        workingMemory.execute(cf);

        for (AcmParticipant ap : cf.getParticipants())
        {
            System.out.println("id: " + ap.getParticipantLdapId());
        }

        // should still be 1
        assertEquals(1, cf.getParticipants().stream()
                .filter(ap -> ap.getParticipantLdapId().equals(cf.getCreator()) && ap.getParticipantType().equals("reader")).count());
    }

}
