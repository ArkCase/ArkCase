package com.armedia.acm.activiti.services;

import com.armedia.acm.activiti.model.AcmProcessDefinition;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class ProcessDefinitionIT {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ProcessDefinitionManagementService processDefinitionManagementService;


    @Test
    @Transactional
    public void deployProcessDefinitionAndMakeActive() throws Exception {
        File f = new File(getClass().getResource("/activiti/TestActivitiSpringProcess.bpmn20.xml").toURI());
        AcmProcessDefinition apd = processDefinitionManagementService.deployProcessDefinition(f, false, false);
        log.info("AcmProcessDefinition deployed: " + apd);
        assertNull(processDefinitionManagementService.getActive(apd.getKey()));
        processDefinitionManagementService.makeActive(apd);
        assertNotNull(processDefinitionManagementService.getActive(apd.getKey()));

        processDefinitionManagementService.removeProcessDefinition(apd);

    }

    @Test
    @Transactional
    public void deployExistingProcessDefinition() throws Exception {
        File f = new File(getClass().getResource("/activiti/TestActivitiSpringProcess.bpmn20.xml").toURI());
        AcmProcessDefinition apd = processDefinitionManagementService.deployProcessDefinition(f, false, false);
        log.info("AcmProcessDefinition deployed: " + apd);
        File f1 = new File(getClass().getResource("/activiti/TestActivitiSpringProcessNotChanged.bpmn20.xml").toURI());
        AcmProcessDefinition apd1 = processDefinitionManagementService.deployProcessDefinition(f1, false, false);

        assertEquals(apd.getId(), apd1.getId());

        processDefinitionManagementService.removeProcessDefinition(apd);

    }


    @Test
    @Transactional
    public void deployNotExistingProcessDefinition() throws Exception {
        File f = new File(getClass().getResource("/activiti/TestActivitiSpringProcess.bpmn20.xml").toURI());
        AcmProcessDefinition apd = processDefinitionManagementService.deployProcessDefinition(f, false, false);
        log.info("AcmProcessDefinition deployed: " + apd);
        File f1 = new File(getClass().getResource("/activiti/TestActivitiSpringProcessChanged.bpmn20.xml").toURI());
        AcmProcessDefinition apd1 = processDefinitionManagementService.deployProcessDefinition(f1, false, false);
        log.info("AcmProcessDefinition deployed: " + apd1);
        assertNotEquals(apd.getId(), apd1.getId());

        processDefinitionManagementService.removeProcessDefinition(apd);
        processDefinitionManagementService.removeProcessDefinition(apd1);
    }

    @Test
    @Transactional
    public void getPageOfProcessDefinition() throws Exception {
        File f = new File(getClass().getResource("/activiti/TestActivitiSpringProcess.bpmn20.xml").toURI());
        AcmProcessDefinition apd = processDefinitionManagementService.deployProcessDefinition(f, false, false);
        log.info("AcmProcessDefinition deployed: " + apd);
        File f1 = new File(getClass().getResource("/activiti/TestActivitiSpringProcessChanged.bpmn20.xml").toURI());
        AcmProcessDefinition apd1 = processDefinitionManagementService.deployProcessDefinition(f1, false, false);
        log.info("AcmProcessDefinition deployed: " + apd1);

        List<AcmProcessDefinition> acmProcessDefinitionList = processDefinitionManagementService.listPage(0, 100, "name", true);


        assertEquals(1, acmProcessDefinitionList.size());
        assertEquals(3, acmProcessDefinitionList.get(0).getVersion());


        processDefinitionManagementService.removeProcessDefinition(apd);
        processDefinitionManagementService.removeProcessDefinition(apd1);
    }

    @Test
    @Transactional
    public void getHistoryOfProcessDefinition() throws Exception {
        File f = new File(getClass().getResource("/activiti/TestActivitiSpringProcess.bpmn20.xml").toURI());
        AcmProcessDefinition apd = processDefinitionManagementService.deployProcessDefinition(f, false, false);
        log.info("AcmProcessDefinition deployed: " + apd);
        File f1 = new File(getClass().getResource("/activiti/TestActivitiSpringProcessChanged.bpmn20.xml").toURI());
        AcmProcessDefinition apd1 = processDefinitionManagementService.deployProcessDefinition(f1, false, false);
        log.info("AcmProcessDefinition deployed: " + apd1);

        List<AcmProcessDefinition> acmProcessDefinitionList = processDefinitionManagementService.getVersionHistory(apd);


        assertEquals(1, acmProcessDefinitionList.size());
        assertEquals(4, acmProcessDefinitionList.get(0).getVersion());


        processDefinitionManagementService.removeProcessDefinition(apd);
        processDefinitionManagementService.removeProcessDefinition(apd1);
    }

    @After
    public void cleanActivitiVersionFolder(){

    }
}
