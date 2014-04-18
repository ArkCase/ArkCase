package com.armedia.acm.plugins.complaint;

import com.armedia.acm.plugins.complaint.model.Complaint;
import org.drools.compiler.compiler.PackageBuilder;
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
 * Created by armdev on 4/17/14.
 */
public class SaveComplaintRulesIT
{

    private Logger log = LoggerFactory.getLogger(getClass());
    private StatelessSession workingMemory;

    @Before
    public void nullEcmFolderId() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-complaint-number-rules.xlsx");
        assertTrue(xls.exists());

        String drl = sc.compile(xls.getInputStream(), InputType.XLS);
        log.info("DRL: " + drl);

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl(new StringReader(drl));

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(builder.getPackage());

        workingMemory = ruleBase.newStatelessSession();
    }

    @Test
    public void nullComplaintNumber() throws Exception
    {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(12345L);

        workingMemory.execute(complaint);

        assertNotNull(complaint.getComplaintNumber());

        log.info("complaint number: " + complaint.getComplaintNumber());

        complaint.setComplaintNumber("A Number");
        workingMemory.execute(complaint);
        assertEquals("A Number", complaint.getComplaintNumber());
    }

    @Test
    public void nullFolderId() throws Exception
    {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(12345L);

        workingMemory.execute(complaint);

        assertNotNull(complaint.getEcmFolderPath());

        log.info("folder path: " + complaint.getEcmFolderPath());

        complaint.setEcmFolderId("An ID");
        complaint.setEcmFolderPath(null);

        workingMemory.execute(complaint);
        assertNull(complaint.getEcmFolderPath());
        assertEquals("An ID", complaint.getEcmFolderId());
    }
}
