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

import com.armedia.acm.activiti.exceptions.AcmBpmnException;
import com.armedia.acm.activiti.exceptions.NotValidBpmnFileException;
import com.armedia.acm.activiti.model.AcmProcessDefinition;
import com.armedia.acm.activiti.services.dao.AcmBpmnDao;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by nebojsha on 13.04.2015.
 * <p>
 * <p>
 * Service for managing process definitions files
 */
public class AcmBpmnServiceImpl implements AcmBpmnService
{
    private transient Logger log = LogManager.getLogger(getClass());

    private RepositoryService activitiRepositoryService;
    private AcmBpmnDao acmBpmnDao;

    @Override
    public List<AcmProcessDefinition> list(String orderBy, boolean isAsc)
    {
        return acmBpmnDao.list(orderBy, isAsc);
    }

    @Override
    public List<AcmProcessDefinition> listPage(String orderBy, boolean isAsc)
    {
        return acmBpmnDao.listPage(orderBy, isAsc);
    }

    @Override
    public InputStream getBpmnFileStream(AcmProcessDefinition acmProcessDefinition)
    {
        ProcessDefinition processDefinition = getProcessDefinition(acmProcessDefinition.getDeploymentId(), acmProcessDefinition.getKey(),
                acmProcessDefinition.getVersion());

        return activitiRepositoryService.getProcessModel(processDefinition.getId());
    }

    @Override
    @Transactional
    public void remove(AcmProcessDefinition processDefinition, boolean cascade)
    {
        activitiRepositoryService.deleteDeployment(processDefinition.getDeploymentId(), cascade);
        acmBpmnDao.remove(processDefinition);
    }

    @Override
    @Transactional
    public void makeActive(AcmProcessDefinition processDefinition)
    {
        AcmProcessDefinition activeVersion = acmBpmnDao.getActive(processDefinition.getKey());
        if (activeVersion != null)
        {
            activeVersion.setActive(false);
            acmBpmnDao.save(activeVersion);
        }
        processDefinition.setActive(true);
        acmBpmnDao.save(processDefinition);
    }

    @Override
    @Transactional
    public void makeInactive(AcmProcessDefinition processDefinition)
    {
        AcmProcessDefinition activeVersion = acmBpmnDao.getActive(processDefinition.getKey());
        if (activeVersion != null)
        {
            activeVersion.setActive(false);
            acmBpmnDao.save(activeVersion);
        }
        processDefinition.setActive(false);
        acmBpmnDao.save(processDefinition);
    }

    @Override
    public List<AcmProcessDefinition> getVersionHistory(AcmProcessDefinition processDefinition)
    {
        return acmBpmnDao.listAllVersions(processDefinition);
    }

    @Override
    public long count()
    {
        return acmBpmnDao.count();
    }

    @Override
    @Transactional
    public AcmProcessDefinition deploy(File processDefinitionFile, String fileDescription, boolean makeActive,
            boolean deleteFileAfterDeploy)
    {

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
        try (InputStream fis = new FileInputStream(processDefinitionFile))
        {
            DeploymentBuilder deploymentBuilder = activitiRepositoryService.createDeployment();

            Deployment deployment = deploymentBuilder.enableDuplicateFiltering().addInputStream(name, fis).name(name)
                    .category("ACM Workflow").deploy();
            // close the stream if not already closed
            closeStream(fis);
            deploymentId = deployment.getId();
            ProcessDefinition pd = activitiRepositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();

            // move file to process definitions with versions folder
            String fileName = pd.getKey() + "_v" + pd.getVersion() + ".bpmn20.xml";

            if (deleteFileAfterDeploy)
            {
                FileUtils.deleteQuietly(processDefinitionFile);
            }

            // create entry to our database
            AcmProcessDefinition acmProcessDefinition = new AcmProcessDefinition();
            acmProcessDefinition.setDeploymentId(pd.getDeploymentId());
            acmProcessDefinition.setDescription(fileDescription);
            acmProcessDefinition.setKey(pd.getKey());
            acmProcessDefinition.setName(pd.getName());
            acmProcessDefinition.setVersion(pd.getVersion());
            acmProcessDefinition.setFileName(fileName);
            acmProcessDefinition.setSha256Hash(digest);

            acmProcessDefinition = acmBpmnDao.save(acmProcessDefinition);
            if (makeActive)
            {
                makeActive(acmProcessDefinition);
            }
            return acmProcessDefinition;
        }
        catch (Throwable e)
        {
            // this should not happen
            AcmBpmnException runtimeException = new AcmBpmnException("Internal application error", e);
            log.error("Error deploying file!", e);
            // rollback
            if (deploymentId != null)
                activitiRepositoryService.deleteDeployment(deploymentId, true);
            if (dest != null && dest.exists())
                dest.delete();
            throw runtimeException;
        }
    }

    private String getDigest(File processDefinitionFile)
    {
        try
        {
            FileInputStream stream = new FileInputStream(processDefinitionFile);
            String sha256Hex = DigestUtils.sha256Hex(stream);
            closeStream(stream);
            return sha256Hex;
        }
        catch (IOException e)
        {
            throw new AcmBpmnException("Error performing file digest!", e);
        }
    }

    private String getProcessDefinitionKey(File processDefinitionFile)
    {
        try
        {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setFeature( "http://apache.org/xml/features/disallow-doctype-decl", true);
            domFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            domFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            domFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
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
        }
        catch (ParserConfigurationException e)
        {
            throw new NotValidBpmnFileException("Not valid file!", e);
        }
        catch (SAXException e)
        {
            throw new NotValidBpmnFileException("Not valid file!", e);
        }
        catch (IOException e)
        {
            throw new NotValidBpmnFileException("Not valid file!", e);
        }
        catch (XPathExpressionException e)
        {
            throw new NotValidBpmnFileException("Not valid file!", e);
        }
    }

    @Override
    public AcmProcessDefinition getActive(String processDefinitionKey)
    {
        return acmBpmnDao.getActive(processDefinitionKey);
    }

    @Override
    public AcmProcessDefinition getByKeyAndVersion(String processDefinitionKey, int version)
    {
        return acmBpmnDao.getByKeyAndVersion(processDefinitionKey, version);
    }

    @Override
    public byte[] getDiagram(String deploymentId, String key, Integer version) throws AcmBpmnException
    {
        byte[] diagram = null;
        ProcessDefinition processDefinition = getProcessDefinition(deploymentId, key, version);

        if (processDefinition != null)
        {
            InputStream inputStream = null;
            try
            {
                inputStream = activitiRepositoryService.getProcessDiagram(processDefinition.getId());
                diagram = IOUtils.toByteArray(inputStream);
            }
            catch (Exception e)
            {
                log.warn("Cannot take diagram for deploymentId=[{}], key=[{}] and version=[{}]", deploymentId, key, version);
            }
            finally
            {
                if (inputStream != null)
                {
                    try
                    {
                        inputStream.close();
                    }
                    catch (IOException e)
                    {
                        log.error("Can't close input stream after generating workflow diagram image.", e);
                    }
                }
            }
        }

        if (diagram == null)
        {
            log.debug("Diagram for deploymentId=[{}], key=[{}] and version=[{}] cannot be retrieved", deploymentId, key, version);
            throw new AcmBpmnException(
                    "Diagram for deploymentId=[" + deploymentId + "], key=[" + key + "] and version=[" + version + "] cannot be retrieved");
        }

        return diagram;
    }

    @Override
    public List<AcmProcessDefinition> listAllDeactivatedVersions()
    {
        return acmBpmnDao.listAllDeactivatedVersions();
    }

    private ProcessDefinition getProcessDefinition(String deploymentId, String key, int version)
    {
        ProcessDefinition processDefinition = null;

        try
        {
            processDefinition = activitiRepositoryService.createProcessDefinitionQuery().deploymentId(deploymentId)
                    .processDefinitionKey(key).processDefinitionVersion(version).singleResult();
        }
        catch (ActivitiException e)
        {
            log.warn("Cannot find process definition for deploymentId=[{}], key=[{}] and version=[{}]", deploymentId, key, version);
        }

        return processDefinition;
    }

    public void setActivitiRepositoryService(RepositoryService activitiRepositoryService)
    {
        this.activitiRepositoryService = activitiRepositoryService;
    }

    public void setAcmBpmnDao(AcmBpmnDao acmBpmnDao)
    {
        this.acmBpmnDao = acmBpmnDao;
    }

    private void closeStream(Closeable stream)
    {
        if (stream != null)
        {
            try
            {
                stream.close();
            }
            catch (IOException e)
            {
                log.warn("Could not close deployment file: " + e.getMessage(), e);
            }
        }
    }

}
