package com.armedia.acm.dataaccess;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.services.participants.model.AcmParticipantPrivilege;
import com.armedia.acm.services.participants.model.AcmParticipant;
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.compiler.compiler.PackageBuilderErrors;
import org.drools.core.RuleBase;
import org.drools.core.RuleBaseFactory;
import org.drools.core.StatelessSession;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.StringReader;

import static org.junit.Assert.*;

/**
 * Created by armdev on 1/5/15.
 */
public class DataAccessControlRulesIT
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private StatelessSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-access-control-rules.xlsx");
        assertTrue(xls.exists());

        String drl = sc.compile(xls.getInputStream(), InputType.XLS);
        log.info("DRL: " + drl);

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl(new StringReader(drl));

        if ( builder.hasErrors() )
        {
            PackageBuilderErrors errors = builder.getErrors();
            for (DroolsError de : errors.getErrors() )
            {
                log.error(de.getMessage());
            }
        }

        assertFalse(builder.hasErrors());

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(builder.getPackage());

        workingMemory = ruleBase.newStatelessSession();
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

        assertEquals("grant", priv.getAccessType());
        assertEquals("read", priv.getObjectAction());
        assertEquals("policy", priv.getAccessReason());

        // since we have privileges now, if we run the rule again, it should not add any more
        workingMemory.execute(c);
        assertEquals(1, c.getParticipants().get(0).getPrivileges().size());

    }

    @Test
    public void complaint_draft_defaultReadDenied() throws Exception
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

        assertEquals("deny", priv.getAccessType());
        assertEquals("read", priv.getObjectAction());
        assertEquals("policy", priv.getAccessReason());

        // since we have privileges now, if we run the rule again, it should not add any more
        workingMemory.execute(c);
        assertEquals(1, c.getParticipants().get(0).getPrivileges().size());

    }

}
