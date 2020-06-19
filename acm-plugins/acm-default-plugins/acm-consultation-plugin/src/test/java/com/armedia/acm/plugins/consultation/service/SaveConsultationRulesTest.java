package com.armedia.acm.plugins.consultation.service;

/*-
 * #%L
 * ACM Default Plugin: Consultation
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;

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

import java.util.Date;

public class SaveConsultationRulesTest
{

    private Logger log = LogManager.getLogger(getClass());
    private StatelessKieSession workingMemory;

    @Before
    public void setUp() throws Exception
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new ClassPathResource("/rules/drools-consultation-rules.xlsx");
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

        assertNotNull(workingMemory);
    }

    @Test
    public void nullConsultationNumber() throws Exception
    {
        Consultation consultation = new Consultation();
        consultation.setId(12345L);

        workingMemory.execute(consultation);

        assertNotNull(consultation.getConsultationNumber());

        log.info("Consultation number: " + consultation.getConsultationNumber());
    }

    @Test
    public void consultationNumberExists() throws Exception
    {
        Consultation consultation = new Consultation();
        consultation.setId(12345L);
        consultation.setConsultationNumber("Set Number");

        workingMemory.execute(consultation);
        assertEquals("Set Number", consultation.getConsultationNumber());
    }

    @Test
    public void nullCmisFolderId() throws Exception
    {
        Consultation consultation = new Consultation();
        consultation.setId(12345L);

        AcmContainer container = new AcmContainer();
        AcmFolder folder = new AcmFolder();

        container.setFolder(folder);
        consultation.setContainer(container);

        workingMemory.execute(consultation);

        assertNotNull(consultation.getEcmFolderPath());

        log.info("Consultation cmis folder path: " + consultation.getEcmFolderPath());
    }

    @Test
    public void consultationCmisFolderIdExists() throws Exception
    {
        Consultation consultation = new Consultation();
        consultation.setId(12345L);

        AcmContainer container = new AcmContainer();
        AcmFolder folder = new AcmFolder();

        folder.setCmisFolderId("randomCmisId");

        container.setFolder(folder);
        consultation.setContainer(container);

        workingMemory.execute(consultation);
        assertEquals("randomCmisId", consultation.getContainer().getFolder().getCmisFolderId());
        assertNull(consultation.getEcmFolderPath());
    }

    @Test
    public void nullStatus() throws Exception
    {
        Consultation consultation = new Consultation();
        consultation.setId(12345L);

        workingMemory.execute(consultation);

        assertNotNull(consultation.getStatus());
        assertEquals("DRAFT", consultation.getStatus());

        log.info("Consultation status: " + consultation.getStatus());
    }

    @Test
    public void consultationStatusExists() throws Exception
    {
        Consultation consultation = new Consultation();
        consultation.setId(12345L);
        consultation.setStatus("Closed");

        workingMemory.execute(consultation);
        assertEquals("Closed", consultation.getStatus());
    }

    @Test
    public void nullConsultationType() throws Exception
    {
        Consultation consultation = new Consultation();
        consultation.setId(12345L);

        workingMemory.execute(consultation);

        assertNotNull(consultation.getConsultationType());
        assertEquals("Consultation", consultation.getConsultationType());

        log.info("Consultation status: " + consultation.getConsultationType());
    }

    @Test
    public void consultationTypeExists() throws Exception
    {
        Consultation consultation = new Consultation();
        consultation.setId(12345L);
        consultation.setConsultationType("Consultation");

        workingMemory.execute(consultation);
        assertEquals("Consultation", consultation.getConsultationType());
    }

    @Test
    public void nullConsultationPriority() throws Exception
    {
        Consultation consultation = new Consultation();
        consultation.setId(12345L);

        workingMemory.execute(consultation);

        assertNotNull(consultation.getPriority());

        log.info("Consultation priority: " + consultation.getPriority());
    }

    @Test
    public void consultationPriorityExists() throws Exception
    {
        Consultation consultation = new Consultation();
        consultation.setId(12345L);
        consultation.setPriority("Expedite");

        workingMemory.execute(consultation);
        assertEquals("Expedite", consultation.getPriority());
    }

    @Test
    public void nullDueDate() throws Exception
    {
        Consultation consultation = new Consultation();
        consultation.setId(12345L);

        workingMemory.execute(consultation);

        assertNotNull(consultation.getDueDate());

        log.debug("Consultation due date: " + consultation.getDueDate());
    }

    @Test
    public void consultationDueDateExists() throws Exception
    {
        Consultation consultation = new Consultation();
        consultation.setId(12345L);

        Date date = new Date();

        consultation.setDueDate(date);

        workingMemory.execute(consultation);
        assertEquals(date, consultation.getDueDate());
    }

}
