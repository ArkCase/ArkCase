package com.armedia.acm.activiti.services;

import com.armedia.acm.activiti.model.AcmProcessDefinition;
import org.activiti.engine.RepositoryService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class AcmBpmnServiceIT {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    AcmBpmnService acmBpmnService;

    @Autowired
    RepositoryService activitiRepositoryService;
    Set<String> filesToDelete = null;
    Set<String> deploymentsIdToDelete = null;

    @Before
    public void setUp() {
        filesToDelete = new HashSet<>();
        deploymentsIdToDelete = new HashSet<>();
    }

    @Test
    @Transactional
    public void deployProcessDefinitionAndMakeActive() throws Exception {
        File f = new File(getClass().getResource("/activiti/TestActivitiSpringProcess.bpmn20.xml").toURI());
        AcmProcessDefinition apd = acmBpmnService.deploy(f, false, false);
        filesToDelete.add(apd.getFileName());
        deploymentsIdToDelete.add(apd.getDeploymentId());
        log.info("AcmProcessDefinition deployed: " + apd);
        assertNull(acmBpmnService.getActive(apd.getKey()));
        acmBpmnService.makeActive(apd);
        assertNotNull(acmBpmnService.getActive(apd.getKey()));

        acmBpmnService.remove(apd, true);

    }

    @Test
    @Transactional
    public void deployExistingProcessDefinition() throws Exception {
        File f = new File(getClass().getResource("/activiti/TestActivitiSpringProcess.bpmn20.xml").toURI());
        AcmProcessDefinition apd = acmBpmnService.deploy(f, false, false);
        filesToDelete.add(apd.getFileName());
        deploymentsIdToDelete.add(apd.getDeploymentId());
        log.info("AcmProcessDefinition deployed: " + apd);
        File f1 = new File(getClass().getResource("/activiti/TestActivitiSpringProcessNotChanged.bpmn20.xml").toURI());
        AcmProcessDefinition apd1 = acmBpmnService.deploy(f1, false, false);
        filesToDelete.add(apd1.getFileName());
        deploymentsIdToDelete.add(apd1.getDeploymentId());
        assertEquals(apd.getId(), apd1.getId());

        acmBpmnService.remove(apd, true);

    }


    @Test
    @Transactional
    public void deployNotExistingProcessDefinition() throws Exception {
        File f = new File(getClass().getResource("/activiti/TestActivitiSpringProcess.bpmn20.xml").toURI());
        AcmProcessDefinition apd = acmBpmnService.deploy(f, false, false);
        filesToDelete.add(apd.getFileName());
        deploymentsIdToDelete.add(apd.getDeploymentId());
        log.info("AcmProcessDefinition deployed: " + apd);
        File f1 = new File(getClass().getResource("/activiti/TestActivitiSpringProcessChanged.bpmn20.xml").toURI());
        AcmProcessDefinition apd1 = acmBpmnService.deploy(f1, false, false);
        filesToDelete.add(apd1.getFileName());
        deploymentsIdToDelete.add(apd1.getDeploymentId());
        log.info("AcmProcessDefinition deployed: " + apd1);
        assertNotEquals(apd.getId(), apd1.getId());

        acmBpmnService.remove(apd, true);
        acmBpmnService.remove(apd1, true);
    }

    @Test
    @Transactional
    public void getHistoryOfProcessDefinition() throws Exception {
        File f = new File(getClass().getResource("/activiti/TestActivitiSpringProcess.bpmn20.xml").toURI());
        AcmProcessDefinition apd = acmBpmnService.deploy(f, false, false);
        filesToDelete.add(apd.getFileName());
        deploymentsIdToDelete.add(apd.getDeploymentId());
        log.info("AcmProcessDefinition deployed: " + apd);
        File f1 = new File(getClass().getResource("/activiti/TestActivitiSpringProcessChanged.bpmn20.xml").toURI());
        AcmProcessDefinition apd1 = acmBpmnService.deploy(f1, false, false);
        deploymentsIdToDelete.add(apd1.getDeploymentId());
        filesToDelete.add(apd1.getFileName());
        log.info("AcmProcessDefinition deployed: " + apd1);

        List<AcmProcessDefinition> acmProcessDefinitionList = acmBpmnService.getVersionHistory(apd);


        assertEquals(1, acmProcessDefinitionList.size());
        assertEquals(2, acmProcessDefinitionList.get(0).getVersion());


        acmBpmnService.remove(apd, true);
        acmBpmnService.remove(apd1, true);
    }

    @Test
    @Transactional
    public void deployProcessDefinitionAndDownloadFile() throws Exception {
        File f = new File(getClass().getResource("/activiti/TestActivitiSpringProcess.bpmn20.xml").toURI());
        AcmProcessDefinition apd = acmBpmnService.deploy(f, false, false);
        filesToDelete.add(apd.getFileName());
        deploymentsIdToDelete.add(apd.getDeploymentId());
        log.info("AcmProcessDefinition deployed: " + apd);

        String tmpFolder = System.getProperty("java.io.tmpdir");
        InputStream is = acmBpmnService.getBpmnFileStream(apd);
        File downloadedFile = new File(tmpFolder + "/" + apd.getFileName());
        FileUtils.copyStreamToFile(is, downloadedFile);

        try {
            is.close();
        } catch (IOException e) {
            log.debug("file already closed");
        }


        assertTrue(FileUtils.contentEquals(f, downloadedFile));

        acmBpmnService.remove(apd, true);

    }

    @After
    public void cleanUp() {
        //in case of failed test or exception, database will rollback, and files and deployments are cleaned manually
        String userHome = System.getProperty("user.home");
        String processDefinitionsFolder = userHome + "/.acm/activiti/versions";
        //delete created files
        for (String file : filesToDelete) {
            File toBeDeleted = new File(processDefinitionsFolder + "/" + file);
            if (toBeDeleted.exists())
                toBeDeleted.delete();
        }
        //delete created deployments
        for (String d : deploymentsIdToDelete) {
            if (activitiRepositoryService.createDeploymentQuery().deploymentId(d).singleResult() != null)
                activitiRepositoryService.deleteDeployment(d, true);
        }
    }

}
