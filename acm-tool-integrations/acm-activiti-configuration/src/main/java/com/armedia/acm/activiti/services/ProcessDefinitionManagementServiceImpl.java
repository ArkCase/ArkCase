package com.armedia.acm.activiti.services;

import com.armedia.acm.activiti.exceptions.AcmProcessDefinitionException;
import com.armedia.acm.activiti.model.AcmProcessDefinition;
import com.armedia.acm.activiti.services.dao.AcmProcessDefinitionDao;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
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
public class ProcessDefinitionManagementServiceImpl implements ProcessDefinitionManagementService {
    private transient Logger log = LoggerFactory.getLogger(getClass());

    private String processDefinitionsFolder;
    private RepositoryService activitiRepositoryService;
    private AcmProcessDefinitionDao acmProcessDefinitionDao;


    @Override
    public List<AcmProcessDefinition> listPage(int start, int length, String orderBy, boolean isAsc) {
        return acmProcessDefinitionDao.listPage(start, length, orderBy, isAsc);
    }

    @Override
    public InputStream getProcessDefinitionFile(AcmProcessDefinition processDefinition) {
        String filePath = processDefinitionsFolder + "/" + processDefinition.getFileName();
        try {
            FileInputStream fis = new FileInputStream(new File(filePath));
            return fis;
        } catch (FileNotFoundException e) {
            //this should not happen
            AcmProcessDefinitionException runtimeException = new AcmProcessDefinitionException("Internal application error", e);
            log.error("File for Process definition id = ({}) not found. Should be on path = ({})", processDefinition.getId(), filePath);
            throw runtimeException;
        }
    }

    @Override
    @Transactional
    public void removeProcessDefinition(AcmProcessDefinition processDefinition) {
        String filePath = processDefinitionsFolder + "/" + processDefinition.getFileName();
        File file = new File(filePath);
        if (file.exists())
            file.delete();
        else {
            //this should not happen
            AcmProcessDefinitionException runtimeException = new AcmProcessDefinitionException("Internal application error");
            log.error("File for Process definition id = ({}) not found. Should be on path = ({})", processDefinition.getId(), filePath);
            throw runtimeException;
        }
        acmProcessDefinitionDao.remove(processDefinition);
    }

    @Override
    @Transactional
    public void makeActive(AcmProcessDefinition processDefinition) {
        AcmProcessDefinition activeVersion = acmProcessDefinitionDao.getActive(processDefinition.getKey());
        if (activeVersion != null) {
            activeVersion.setActive(false);
            acmProcessDefinitionDao.save(activeVersion);
        }
        processDefinition.setActive(true);
        acmProcessDefinitionDao.save(processDefinition);
    }

    @Override
    public List<AcmProcessDefinition> getVersionHistory(AcmProcessDefinition processDefinition) {
        return acmProcessDefinitionDao.listAllVersions(processDefinition);
    }

    @Override
    public long count() {
        return acmProcessDefinitionDao.count();
    }

    @Override
    @Transactional
    public AcmProcessDefinition deployProcessDefinition(File processDefinitionFile, boolean makeWorkingVersion, boolean deleteFileAfterDeploy) {
        //verify that folder for keeping process definition files exists before deployment
        verifyFolderExists();
        String name = getProcessDefinitionKey(processDefinitionFile) + ".bpmn20.xml";
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

            ProcessDefinition pd = activitiRepositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();

            //move file to process definitions with versions folder
            String fileName = pd.getKey() + "_v" + pd.getVersion() + ".bpmn20.xml";


            File dest = new File(processDefinitionsFolder + "/" + fileName);

            if (dest.exists()) {
                //this version is already saved and is not new
                AcmProcessDefinition acmProcessDefinitionExisting = acmProcessDefinitionDao.getByKeyAndVersion(pd.getKey(), pd.getVersion());
                if (acmProcessDefinitionExisting == null) {
                    throw new AcmProcessDefinitionException("Internal error, this record should exits in database");
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

            acmProcessDefinition = acmProcessDefinitionDao.save(acmProcessDefinition);
            if (makeWorkingVersion) {
                makeActive(acmProcessDefinition);
            }
            return acmProcessDefinition;
        } catch (FileNotFoundException e) {
            //this should not happen
            AcmProcessDefinitionException runtimeException = new AcmProcessDefinitionException("Internal application error", e);
            log.error("Uploaded file doesn't exits");
            throw runtimeException;
        } catch (IOException e) {
            log.error("Error copying/moving file");
            throw new AcmProcessDefinitionException("Error copying/moving file", e);
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

            return attributeValue;
        } catch (ParserConfigurationException e) {

            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AcmProcessDefinition getActive(String processDefinitionKey) {
        return acmProcessDefinitionDao.getActive(processDefinitionKey);
    }

    public void setActivitiRepositoryService(RepositoryService activitiRepositoryService) {
        this.activitiRepositoryService = activitiRepositoryService;
    }

    public void setProcessDefinitionsFolder(String processDefinitionsFolder) {
        this.processDefinitionsFolder = processDefinitionsFolder;
    }

    public void setAcmProcessDefinitionDao(AcmProcessDefinitionDao acmProcessDefinitionDao) {
        this.acmProcessDefinitionDao = acmProcessDefinitionDao;
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
            throw new AcmProcessDefinitionException("Process definition folder must not be empty");
        }
    }
}
