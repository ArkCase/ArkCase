package com.armedia.acm.activiti.services;

import com.armedia.acm.activiti.exceptions.AcmBpmnException;
import com.armedia.acm.activiti.exceptions.NotValidBpmnFileException;
import com.armedia.acm.activiti.model.AcmProcessDefinition;
import com.armedia.acm.activiti.services.dao.AcmBpmnDao;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by nebojsha on 13.04.2015.
 * <p>
 * <p>
 * Service for managing process definitions files
 */
public class AcmBpmnServiceImpl implements AcmBpmnService {
    private transient Logger log = LoggerFactory.getLogger(getClass());

    private String processDefinitionsFolder;
    private RepositoryService activitiRepositoryService;
    private AcmBpmnDao acmBpmnDao;


    @Override
    public List<AcmProcessDefinition> listPage(int start, int length, String orderBy, boolean isAsc) {
        return acmBpmnDao.listPage(start, length, orderBy, isAsc);
    }

    @Override
    public InputStream getBpmnFileStream(AcmProcessDefinition processDefinition) {
        String filePath = processDefinitionsFolder + "/" + processDefinition.getFileName();
        try {
            FileInputStream fis = new FileInputStream(new File(filePath));
            return fis;
        } catch (FileNotFoundException e) {
            //this should not happen
            log.error("File for Process definition id = ({}) not found. Should be on path = ({})", processDefinition.getId(), filePath);
            throw new AcmBpmnException("Internal application error, file for AcmProcessDefinition id = " + processDefinition.getId() + " should exists!");
        }
    }

    @Override
    @Transactional
    public void remove(AcmProcessDefinition processDefinition, boolean cascade) {
        String filePath = processDefinitionsFolder + "/" + processDefinition.getFileName();
        File file = new File(filePath);
        if (file.exists())
            file.delete();
        else {
            //this should not happen
            log.error("File for Process definition id = ({}) not found. Should be on path = ({})", processDefinition.getId(), filePath);
            throw new AcmBpmnException("Internal application error, file for AcmProcessDefinition id = " + processDefinition.getId() + " should exists!");
        }
        activitiRepositoryService.deleteDeployment(processDefinition.getDeploymentId(), cascade);
        acmBpmnDao.remove(processDefinition);
    }

    @Override
    @Transactional
    public void makeActive(AcmProcessDefinition processDefinition) {
        AcmProcessDefinition activeVersion = acmBpmnDao.getActive(processDefinition.getKey());
        if (activeVersion != null) {
            activeVersion.setActive(false);
            acmBpmnDao.save(activeVersion);
        }
        processDefinition.setActive(true);
        acmBpmnDao.save(processDefinition);
    }

    @Override
    public List<AcmProcessDefinition> getVersionHistory(AcmProcessDefinition processDefinition) {
        return acmBpmnDao.listAllVersions(processDefinition);
    }

    @Override
    public long count() {
        return acmBpmnDao.count();
    }

    @Override
    @Transactional
    public AcmProcessDefinition deploy(File processDefinitionFile, boolean makeActive, boolean deleteFileAfterDeploy) {
        //verify that folder for keeping process definition files exists before deployment
        verifyFolderExists();
        String digest = getDigest(processDefinitionFile);
        log.info("Digest [{}] from Bpmn file", digest);
        String bpmnId = getProcessDefinitionKey(processDefinitionFile);
        String name = bpmnId + ".bpmn20.xml";
        log.info("extracted id[{}] from Bpmn file", bpmnId);
        AcmProcessDefinition acmProcessDefinitionExisting = acmBpmnDao.getByKeyAndDigest(bpmnId, digest);
        String deploymentId = null;
        File dest = null;
        if (acmProcessDefinitionExisting != null)
        {
            log.info("not deploying, since process already exists [{}]", bpmnId);
            return acmProcessDefinitionExisting;
        }
        try {
            FileInputStream fis = new FileInputStream(processDefinitionFile);
            DeploymentBuilder deploymentBuilder = activitiRepositoryService.createDeployment();

            Deployment deployment = deploymentBuilder.enableDuplicateFiltering().
                    addInputStream(name, fis).
                    name(name).
                    category("ACM Workflow").
                    deploy();
            //close the stream if not already closed
            closeStream(fis);
            deploymentId = deployment.getId();
            ProcessDefinition pd = activitiRepositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();

            //move file to process definitions with versions folder
            String fileName = pd.getKey() + "_v" + pd.getVersion() + ".bpmn20.xml";


            dest = new File(processDefinitionsFolder + "/" + fileName);

            if (dest.exists()) {
                //this version is already saved and is not new
                acmProcessDefinitionExisting = acmBpmnDao.getByKeyAndVersion(pd.getKey(), pd.getVersion());
                if (acmProcessDefinitionExisting == null) {
                    throw new AcmBpmnException("Internal error, this record should exists in database");
                }
                return acmProcessDefinitionExisting;
            }
            if (deleteFileAfterDeploy) {
                processDefinitionFile.renameTo(dest);
            } else {
                FileUtils.copyFile(processDefinitionFile, dest, false);
            }

            //create entry to our database
            AcmProcessDefinition acmProcessDefinition = new AcmProcessDefinition();
            acmProcessDefinition.setDeploymentId(pd.getDeploymentId());
            acmProcessDefinition.setDescription(pd.getDescription());
            acmProcessDefinition.setKey(pd.getKey());
            acmProcessDefinition.setName(pd.getName());
            acmProcessDefinition.setVersion(pd.getVersion());
            acmProcessDefinition.setFileName(fileName);
            acmProcessDefinition.setMd5Hash(digest);

            acmProcessDefinition = acmBpmnDao.save(acmProcessDefinition);
            if (makeActive) {
                makeActive(acmProcessDefinition);
            }
            return acmProcessDefinition;
        } catch (Exception e) {
            //this should not happen
            AcmBpmnException runtimeException = new AcmBpmnException("Internal application error", e);
            log.error("Error deploying file!");
            //rollback
            if (deploymentId != null)
                activitiRepositoryService.deleteDeployment(deploymentId, true);
            if (dest != null && dest.exists())
                dest.delete();
            throw runtimeException;
        }
    }

    private String getDigest(File processDefinitionFile) {
        try {
            FileInputStream stream = new FileInputStream(processDefinitionFile);
            String md5Hex = DigestUtils.md5Hex(stream);
            closeStream(stream);
            return md5Hex;
        } catch (IOException e) {
            throw new AcmBpmnException("Error performing file digest!", e);
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

    @Override
    public AcmProcessDefinition getActive(String processDefinitionKey) {
        return acmBpmnDao.getActive(processDefinitionKey);
    }

    public void setActivitiRepositoryService(RepositoryService activitiRepositoryService) {
        this.activitiRepositoryService = activitiRepositoryService;
    }

    public void setProcessDefinitionsFolder(String processDefinitionsFolder) {
        this.processDefinitionsFolder = processDefinitionsFolder;
    }

    public void setAcmBpmnDao(AcmBpmnDao acmBpmnDao) {
        this.acmBpmnDao = acmBpmnDao;
    }

    private void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                log.warn("Could not close deployment file: " + e.getMessage(), e);
            }
        }
    }

    private void verifyFolderExists() {
        if (processDefinitionsFolder != null && processDefinitionsFolder.length() > 0) {
            File pdFolder = new File(processDefinitionsFolder);
            if (!pdFolder.exists())
                pdFolder.mkdirs();
        } else {
            throw new AcmBpmnException("Process definition folder must not be empty");
        }
    }
}
