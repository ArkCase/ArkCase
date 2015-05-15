package com.armedia.acm.activiti.services;

import com.armedia.acm.activiti.exceptions.AcmBpmnException;
import com.armedia.acm.activiti.exceptions.NotValidBpmnFileException;
import com.armedia.acm.activiti.model.AcmProcessDefinition;
import com.armedia.acm.activiti.services.dao.AcmBpmnDao;
import org.activiti.engine.RepositoryService;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
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
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
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
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class AcmBpmnServiceIT {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    AcmBpmnService acmBpmnService;

    @Autowired
    AcmBpmnDao acmBpmnDao;

    @Autowired
    RepositoryService activitiRepositoryService;
    Set<String> filesToDelete = null;
    Set<String> deploymentsIdToDelete = null;


    @Before
    public void setUp() throws Exception
    {
        filesToDelete = new HashSet<>();
        deploymentsIdToDelete = new HashSet<>();

        cleanup();
    }

    public void cleanup() throws Exception
    {
        // cleanup any test processes that might still be in our Activiti
        cleanup("/activiti/TestActivitiSpringProcess.bpmn20.xml");
        cleanup("/activiti/TestActivitiSpringProcessChanged.bpmn20.xml");
    }

    public void cleanup(String filename) throws Exception
    {
        File f = new File(getClass().getResource(filename).toURI());
        String digest = getDigest(f);
        log.info("Digest [{}] from Bpmn file", digest);
        String bpmnId = getProcessDefinitionKey(f);
        AcmProcessDefinition acmProcessDefinitionExisting = acmBpmnDao.getByKeyAndDigest(bpmnId, digest);
        if ( acmProcessDefinitionExisting != null )
        {
            activitiRepositoryService.deleteDeployment(acmProcessDefinitionExisting.getDeploymentId(), true);
            acmBpmnDao.remove(acmProcessDefinitionExisting);
        }
    }

    private String getProcessDefinitionKey(File processDefinitionFile) {
        try {
            DocumentBuilderFactory domFactory =
                    DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(false);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(processDefinitionFile);
            XPath xpath = XPathFactory.newInstance().newXPath();
            // XPath Query for showing all nodes value
            XPathExpression expr = xpath.compile("/definitions/process/@id");

            String attributeValue = "" + expr.evaluate(doc, XPathConstants.STRING);
            if (attributeValue == null || attributeValue.length() < 1)
                throw new NotValidBpmnFileException("attribute id not found in process tag");
            return attributeValue;
        } catch (ParserConfigurationException e) {
            throw new NotValidBpmnFileException("Not valid file!", e);
        } catch (SAXException e) {
            throw new NotValidBpmnFileException("Not valid file!", e);
        } catch (IOException e) {
            throw new NotValidBpmnFileException("Not valid file!", e);
        } catch (XPathExpressionException e) {
            throw new NotValidBpmnFileException("Not valid file!", e);
        }
    }

    private String getDigest(File processDefinitionFile) throws Exception
    {
        FileInputStream stream = null;
        try
        {
            stream = new FileInputStream(processDefinitionFile);
            String md5Hex = DigestUtils.md5Hex(stream);
            return md5Hex;
        }
        catch (IOException e)
        {
            throw new AcmBpmnException("Error performing file digest!", e);
        }
        finally
        {
            if ( stream != null )
            {
                stream.close();
            }
        }
    }

    @Test
    @Transactional
    public void deployProcessDefinitionAndMakeActive() throws Exception {
        File f = new File(getClass().getResource("/activiti/TestActivitiSpringProcess.bpmn20.xml").toURI());

        assertTrue(f.exists());

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
