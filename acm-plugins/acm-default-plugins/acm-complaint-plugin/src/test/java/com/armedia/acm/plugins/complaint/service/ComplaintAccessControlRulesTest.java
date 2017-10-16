package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.services.participants.model.AcmParticipant;
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
 * Created by armdev on 4/17/14.
 */
public class ComplaintAccessControlRulesTest
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        kbuilder.add(ResourceFactory.newInputStreamResource(xls.getInputStream()), ResourceType.DTABLE, dtconf);

        if (kbuilder.hasErrors())
        {
            log.info("DRL: " + drl);
            for (KnowledgeBuilderError error : kbuilder.getErrors())
            {
                log.error("Error building rules: " + error);
            }

            throw new RuntimeException("Could not build rules from " + xls.getFile().getAbsolutePath());
        }

        workingMemory = kbuilder.newKnowledgeBase().newStatelessKnowledgeSession();

        assertNotNull(workingMemory);
    }

    @Test
    public void restricted() throws Exception
    {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(12345L);
        complaint.setRestricted(true);

        AcmParticipant defUser = new AcmParticipant();
        defUser.setParticipantLdapId("*");
        defUser.setParticipantType("*");
        complaint.getParticipants().add(defUser);

        workingMemory.execute(complaint);

        assertEquals(4, complaint.getParticipants().get(0).getPrivileges().size());

        assertEquals(1, complaint.getParticipants().get(0).getPrivileges().stream().
                filter(app -> app.getAccessType().equals("deny") && app.getObjectAction().equals("read")).count());

        assertEquals(1, complaint.getParticipants().get(0).getPrivileges().stream().
                filter(app -> app.getAccessType().equals("grant") && app.getObjectAction().equals("subscribe")).count());

        assertEquals(1, complaint.getParticipants().get(0).getPrivileges().stream().
                filter(app -> app.getAccessType().equals("grant") && app.getObjectAction().equals("addComment")).count());

        assertEquals(1, complaint.getParticipants().get(0).getPrivileges().stream().
                filter(app -> app.getAccessType().equals("grant") && app.getObjectAction().equals("addTag")).count());
    }

    @Test
    public void unrestricted() throws Exception
    {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(12345L);
        complaint.setRestricted(false);

        AcmParticipant defUser = new AcmParticipant();
        defUser.setParticipantLdapId("*");
        defUser.setParticipantType("*");
        complaint.getParticipants().add(defUser);

        workingMemory.execute(complaint);

        assertEquals(4, complaint.getParticipants().get(0).getPrivileges().size());

        assertEquals(1, complaint.getParticipants().get(0).getPrivileges().stream().
                filter(app -> app.getAccessType().equals("grant") && app.getObjectAction().equals("read")).count());

        assertEquals(1, complaint.getParticipants().get(0).getPrivileges().stream().
                filter(app -> app.getAccessType().equals("grant") && app.getObjectAction().equals("subscribe")).count());

        assertEquals(1, complaint.getParticipants().get(0).getPrivileges().stream().
                filter(app -> app.getAccessType().equals("grant") && app.getObjectAction().equals("addComment")).count());

        assertEquals(1, complaint.getParticipants().get(0).getPrivileges().stream().
                filter(app -> app.getAccessType().equals("grant") && app.getObjectAction().equals("addTag")).count());
    }


}
