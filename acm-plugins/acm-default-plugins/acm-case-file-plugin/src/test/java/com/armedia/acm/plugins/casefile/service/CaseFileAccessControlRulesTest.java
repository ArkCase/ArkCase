package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.model.CaseFile;
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

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by armdev on 4/17/14.
 */
public class CaseFileAccessControlRulesTest
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

        assertNotNull(workingMemory);
    }

    @Test
    public void restricted() throws Exception
    {
        CaseFile caseFile = new CaseFile();
        caseFile.setId(12345L);
        caseFile.setRestricted(true);

        AcmParticipant defUser = new AcmParticipant();
        defUser.setParticipantLdapId("*");
        defUser.setParticipantType("*");
        caseFile.getParticipants().add(defUser);

        workingMemory.execute(caseFile);

        assertEquals(1, caseFile.getParticipants().get(0).getPrivileges().size());

        AcmParticipantPrivilege priv = caseFile.getParticipants().get(0).getPrivileges().get(0);

        assertEquals("deny", priv.getAccessType());
        assertEquals("read", priv.getObjectAction());
    }

    @Test
    public void unrestricted() throws Exception
    {
        CaseFile caseFile = new CaseFile();
        caseFile.setId(12345L);
        caseFile.setRestricted(false);

        AcmParticipant defUser = new AcmParticipant();
        defUser.setParticipantLdapId("*");
        defUser.setParticipantType("*");
        caseFile.getParticipants().add(defUser);

        workingMemory.execute(caseFile);

        assertEquals(1, caseFile.getParticipants().get(0).getPrivileges().size());

        AcmParticipantPrivilege priv = caseFile.getParticipants().get(0).getPrivileges().get(0);

        assertEquals("grant", priv.getAccessType());
        assertEquals("read", priv.getObjectAction());
    }


}
