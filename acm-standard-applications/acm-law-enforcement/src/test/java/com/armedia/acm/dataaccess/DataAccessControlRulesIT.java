package com.armedia.acm.dataaccess;

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

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.services.dataaccess.model.DataAccessControlConstants;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantPrivilege;

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
 * Created by armdev on 1/5/15.
 */
public class DataAccessControlRulesIT
{
    private Logger log = LogManager.getLogger(getClass());
    private StatelessKieSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-access-control-rules.xlsx");
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
    public void complaint_assigneeReadAccess() throws Exception
    {
        assertNotNull(workingMemory);

        Complaint c = new Complaint();
        c.setStatus("DRAFT");

        log.debug("object type: " + c.getObjectType());

        AcmParticipant assignee = new AcmParticipant();
        assignee.setParticipantType("assignee");
        assignee.setParticipantLdapId("garcia");

        c.getParticipants().add(assignee);

        workingMemory.execute(c);

        assertEquals(1, c.getParticipants().size());

        assertEquals(assignee.getParticipantLdapId(), c.getParticipants().get(0).getParticipantLdapId());

        // 3 privileges: add file, save, read, subscribe
        assertEquals(4, assignee.getPrivileges().size());

        AcmParticipantPrivilege priv = assignee.getPrivileges().get(0);

        assertEquals(DataAccessControlConstants.ACCESS_GRANT, priv.getAccessType());
        assertEquals(DataAccessControlConstants.ACCESS_LEVEL_READ, priv.getObjectAction());
        assertEquals(DataAccessControlConstants.ACCESS_REASON_POLICY, priv.getAccessReason());

        // since we have privileges now, if we run the rule again, it should not add any more
        workingMemory.execute(c);
        assertEquals(4, c.getParticipants().get(0).getPrivileges().size());

    }

    @Test
    public void complaint_draft_defaultReadPolicy() throws Exception
    {
        assertNotNull(workingMemory);

        Complaint c = new Complaint();
        c.setStatus("DRAFT");

        log.debug("object type: " + c.getObjectType());

        AcmParticipant assignee = new AcmParticipant();
        assignee.setParticipantType("*");
        assignee.setParticipantLdapId("*");

        c.getParticipants().add(assignee);

        workingMemory.execute(c);

        assertEquals(1, c.getParticipants().size());

        assertEquals(assignee.getParticipantLdapId(), c.getParticipants().get(0).getParticipantLdapId());

        assertEquals(4, assignee.getPrivileges().size());

        AcmParticipantPrivilege priv = assignee.getPrivileges().get(0);

        assertEquals(DataAccessControlConstants.ACCESS_GRANT, priv.getAccessType());
        assertEquals(DataAccessControlConstants.ACCESS_LEVEL_READ, priv.getObjectAction());
        assertEquals(DataAccessControlConstants.ACCESS_REASON_POLICY, priv.getAccessReason());

        // since we have privileges now, if we run the rule again, it should not add any more
        workingMemory.execute(c);
        assertEquals(4, c.getParticipants().get(0).getPrivileges().size());

    }

}
