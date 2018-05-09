package com.armedia.acm.activiti.services;

/*-
 * #%L
 * Tool Integrations: Activiti Configuration
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

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

/**
 * Created by riste.tutureski on 5/16/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/spring-library-test-activiti-process-definition-diagram.xml" })
public class WorkflowDiagramIT
{
    @Autowired
    AcmBpmnServiceImpl acmBpmnService;
    @Autowired
    private RepositoryService repo;

    @Before
    public void setUp() throws Exception
    {
        acmBpmnService = new AcmBpmnServiceImpl();
        acmBpmnService.setActivitiRepositoryService(repo);
    }

    @Test
    public void getDiagramTest() throws Exception
    {
        Deployment deployment = repo.createDeployment().addClasspathResource("activiti/Task_Buckets_Sample.bpmn20.xml").deploy();

        byte[] diagram = acmBpmnService.getDiagram(deployment.getId(), "TaskBucketsSample", 1);
        assertNotNull(diagram);

        MagicMatch match = Magic.getMagicMatch(diagram);
        String mimeType = match.getMimeType();

        assertEquals("image/png", mimeType);
    }
}
