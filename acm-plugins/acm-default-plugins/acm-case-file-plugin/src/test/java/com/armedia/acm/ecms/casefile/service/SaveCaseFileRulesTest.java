package com.armedia.acm.ecms.casefile.service;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
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
public class SaveCaseFileRulesTest
{

    private Logger log = LoggerFactory.getLogger(getClass());
    private StatelessSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-case-file-rules.xlsx");
        assertTrue(xls.exists());

        String drl = sc.compile(xls.getInputStream(), InputType.XLS);
        log.info("DRL: " + drl);

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl(new StringReader(drl));

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        assertNotNull(builder.getPackage());

        ruleBase.addPackage(builder.getPackage());

        workingMemory = ruleBase.newStatelessSession();

        assertNotNull(workingMemory);
    }

    @Test
    public void nullCaseNumber() throws Exception
    {
        CaseFile caseFile = new CaseFile();
        caseFile.setId(12345L);

        workingMemory.execute(caseFile);

        assertNotNull(caseFile.getCaseNumber());

        log.info("Case number: " + caseFile.getCaseNumber());
    }

    @Test
    public void caseNumberExists() throws Exception
    {
        CaseFile caseFile = new CaseFile();
        caseFile.setId(12345L);
        caseFile.setCaseNumber("A Number");

        workingMemory.execute(caseFile);
        assertEquals("A Number", caseFile.getCaseNumber());
    }

    @Test
    public void nullCasePriority() throws Exception
    {
        CaseFile caseFile = new CaseFile();
        caseFile.setId(12345L);

        workingMemory.execute(caseFile);

        assertNotNull(caseFile.getPriority());

        log.info("Case priority: " + caseFile.getPriority());
    }

}
