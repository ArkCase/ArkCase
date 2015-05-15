package com.armedia.acm.plugins.complaint;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
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
public class SaveComplaintRulesIT
{

    private Logger log = LoggerFactory.getLogger(getClass());
    private StatelessKnowledgeSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-complaint-number-rules.xlsx");
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
    public void nullFolderPath() throws Exception
    {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(12345L);

        workingMemory.execute(complaint);

        assertNotNull(complaint.getEcmFolderPath());

        log.info("folder path: " + complaint.getEcmFolderPath());

        AcmContainer container = new AcmContainer();
        AcmFolder folder = new AcmFolder();
        folder.setCmisFolderId("cmisFolderId");
        container.setFolder(folder);
        complaint.setContainer(container);
        complaint.setEcmFolderPath(null);

        workingMemory.execute(complaint);
        assertNull(complaint.getEcmFolderPath());
        assertEquals("cmisFolderId", complaint.getContainer().getFolder().getCmisFolderId());
    }
}
