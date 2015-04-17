package com.armedia.acm.activiti.services;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

/**
 * Created by nebojsha on 14.04.2015.
 */
public class ActivitiTest {
    @Test
    public void startBookOrder() {
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration()
                .buildProcessEngine();

        RuntimeService runtimeService = processEngine.getRuntimeService();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        InputStream is = AcmBpmnServiceTest.class.getResourceAsStream("/activiti/bookorder.simple.bpmn20.xml");
        repositoryService.createDeployment()
                .addInputStream("/activiti/bookorder.simple.bpmn20.xml", is)
                .deploy();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "simplebookorder");
        assertNotNull(processInstance.getId());
        System.out.println("id " + processInstance.getId() + " "
                + processInstance.getProcessDefinitionId());
    }
}
