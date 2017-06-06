package com.armedia.acm.activiti.services;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by riste.tutureski on 5/16/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/spring-library-test-activiti-process-definition-diagram.xml"})
public class WorkflowDiagramIT
{
    @Autowired
    private RepositoryService repo;

    @Autowired
    AcmBpmnServiceImpl acmBpmnService;

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
