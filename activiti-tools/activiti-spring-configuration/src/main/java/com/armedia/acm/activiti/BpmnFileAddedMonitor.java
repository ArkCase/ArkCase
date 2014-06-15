package com.armedia.acm.activiti;

import com.armedia.acm.files.ConfigurationFileAddedEvent;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by armdev on 4/11/14.
 */
public class BpmnFileAddedMonitor implements ApplicationListener<ConfigurationFileAddedEvent>
{
    private RepositoryService repositoryService;

    private transient Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(ConfigurationFileAddedEvent configurationFileAddedEvent)
    {
        File eventFile = configurationFileAddedEvent.getConfigFile();
        if ( eventFile.getParentFile().getName().equals("activiti") && eventFile.getName().endsWith("bpmn20.xml") )
        {
            InputStream bpmnInputStream = null;
            try
            {
                bpmnInputStream = new FileInputStream(eventFile);
                if (log.isDebugEnabled())
                {
                    log.debug("Deploying new Activiti file: " + eventFile.getCanonicalPath());
                }
                DeploymentBuilder deploymentBuilder = getRepositoryService().createDeployment();
                deploymentBuilder.
                        enableDuplicateFiltering().
                        addInputStream(eventFile.getName(), bpmnInputStream).
                        name(eventFile.getName()).
                        category("ACM Workflow").
                        deploy();
                if (log.isDebugEnabled())
                {
                    log.debug("... finished deploying from: " + eventFile.getCanonicalPath());
                }
            }
            catch (IOException | ActivitiException e)
            {
                log.error("Could not deploy Activiti definition file: " + e.getMessage(), e);
            }
            finally
            {
                closeInputStream(bpmnInputStream);
            }
        }
    }

    private void closeInputStream(InputStream bpmnInputStream)
    {
        if ( bpmnInputStream != null )
        {
            try
            {
                bpmnInputStream.close();
            } catch (IOException e)
            {
                log.warn("Could not close BPMN deployment file: " + e.getMessage(), e);
            }
        }
    }

    public RepositoryService getRepositoryService()
    {
        return repositoryService;
    }

    public void setRepositoryService(RepositoryService repositoryService)
    {
        this.repositoryService = repositoryService;
    }
}
