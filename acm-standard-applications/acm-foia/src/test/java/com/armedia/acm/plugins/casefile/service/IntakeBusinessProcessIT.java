package com.armedia.acm.plugins.casefile.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.armedia.acm.objectchangestatus.service.ChangeObjectStatusService;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;
import gov.foia.service.AcknowledgementDocumentService;
import gov.foia.service.DocumentGenerator;

/**
 * Created by dmiller on 8/9/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(locations = { "classpath:/spring/spring-library-foia-activiti-test.xml" })
public class IntakeBusinessProcessIT
{
    private final String processName = "foia-extension-intake-process";
    @Autowired
    private ProcessEngine pe;
    @Autowired
    private RepositoryService repo;
    @Autowired
    private RuntimeService rt;
    @Autowired
    @Qualifier("changeObjectStatusService")
    private ChangeObjectStatusService changeObjectStatusService;

    @Autowired
    @Qualifier("queueCaseService")
    private QueueCaseService queueCaseService;

    @Autowired
    @Qualifier("acknowledgementDocumentService")
    private AcknowledgementDocumentService acknowledgementDocumentService;

    @Autowired
    @Qualifier("correspondenceDocumentGenerator")
    private DocumentGenerator documentGenerator;

    private FOIARequestDao mockRequestDao;

    @Before
    public void setUp() throws Exception
    {
        mockRequestDao = createMock(FOIARequestDao.class);

        // deploy
        repo.createDeployment()
                .addClasspathResource("activiti/foia-extension-intake-process_v2.bpmn20.xml")
                .deploy();
    }

    @After
    public void shutDown() throws Exception
    {
        pe.close();
    }

    @Test
    public void intake() throws Exception
    {
        Long foiaId = 500L;
        String objectType = CaseFileConstants.OBJECT_TYPE;

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("OBJECT_TYPE", objectType);
        processVariables.put("OBJECT_ID", foiaId);

        changeObjectStatusService.changeIfNoPermanentStatusIsSet(foiaId, objectType, "In Review", "Closed");
        expect(queueCaseService.enqueue(foiaId, "Intake")).andReturn(new FOIARequest());
        FOIARequest foiaRequest = new FOIARequest();
        foiaRequest.setId(foiaId);
        expect(mockRequestDao.find(foiaId)).andReturn(foiaRequest);
        acknowledgementDocumentService.generateAndUpload(objectType, foiaId);
        acknowledgementDocumentService.emailAcknowledgement(foiaId);

        replay(changeObjectStatusService, queueCaseService, acknowledgementDocumentService);

        rt.startProcessInstanceByKey(processName, processVariables);

        verify(changeObjectStatusService, queueCaseService, acknowledgementDocumentService);
    }
}
