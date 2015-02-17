package com.armedia.acm.dataaccess;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.services.dataaccess.model.DataAccessControlConstants;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantPrivilege;
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

import static org.junit.Assert.*;

/**
 * Created by armdev on 1/5/15.
 */
public class DataAccessControlRulesIT
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private StatelessKnowledgeSession workingMemory;

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

        if ( kbuilder.hasErrors() )
        {
            for (KnowledgeBuilderError error : kbuilder.getErrors() )
            {
                log.error("Error building rules: " + error);
            }

            throw new RuntimeException("Could not build rules from " + xls.getFile().getAbsolutePath());
        }

        workingMemory = kbuilder.newKnowledgeBase().newStatelessKnowledgeSession();
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

        assertEquals(1, assignee.getPrivileges().size());

        AcmParticipantPrivilege priv = assignee.getPrivileges().get(0);

        assertEquals(DataAccessControlConstants.ACCESS_GRANT, priv.getAccessType());
        assertEquals(DataAccessControlConstants.ACCESS_LEVEL_READ, priv.getObjectAction());
        assertEquals(DataAccessControlConstants.ACCESS_REASON_POLICY, priv.getAccessReason());

        // since we have privileges now, if we run the rule again, it should not add any more
        workingMemory.execute(c);
        assertEquals(1, c.getParticipants().get(0).getPrivileges().size());

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

        assertEquals(1, assignee.getPrivileges().size());

        AcmParticipantPrivilege priv = assignee.getPrivileges().get(0);

        assertEquals(DataAccessControlConstants.ACCESS_GRANT, priv.getAccessType());
        assertEquals(DataAccessControlConstants.ACCESS_LEVEL_READ, priv.getObjectAction());
        assertEquals(DataAccessControlConstants.ACCESS_REASON_POLICY, priv.getAccessReason());

        // since we have privileges now, if we run the rule again, it should not add any more
        workingMemory.execute(c);
        assertEquals(1, c.getParticipants().get(0).getPrivileges().size());

    }

}
