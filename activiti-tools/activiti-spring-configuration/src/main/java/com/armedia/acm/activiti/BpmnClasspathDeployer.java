package com.armedia.acm.activiti;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;

public class BpmnClasspathDeployer implements ApplicationContextAware
{
    private File deployFolder;

    private String activitiProcessDefinitionPattern;

    private transient Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Scanning for Activiti process definitions");
        }
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try
        {
            Resource[] processDefinitions = resolver.getResources(getActivitiProcessDefinitionPattern());
            for ( Resource processDefinition : processDefinitions )
            {
                String resourceFilename = processDefinition.getFilename();
                if ( log.isDebugEnabled() )
                {
                    log.debug("Found activiti resource '" + resourceFilename + "'");
                }

                File existing = new File(getDeployFolder() + File.separator + resourceFilename);
                if ( !existing.exists() )
                {
                    if ( log.isDebugEnabled() )
                    {
                        log.debug("Copying resource '" + resourceFilename + "' to deploy folder.");
                    }
                    FileCopyUtils.copy(processDefinition.getFile(), existing);
                }
            }
        } catch (IOException e)
        {
            log.error("Could not copy process definition: " + e.getMessage(), e);
        }

        if ( log.isInfoEnabled() )
        {
            log.info("Done scanning for Activiti process definitions");
        }

    }

    public String getActivitiProcessDefinitionPattern()
    {
        return activitiProcessDefinitionPattern;
    }

    public void setActivitiProcessDefinitionPattern(String activitiProcessDefinitionPattern)
    {
        this.activitiProcessDefinitionPattern = activitiProcessDefinitionPattern;
    }

    public File getDeployFolder()
    {
        return deployFolder;
    }

    public void setDeployFolder(File deployFolder)
    {
        this.deployFolder = deployFolder;
    }
}
