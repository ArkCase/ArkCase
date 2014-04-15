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
import java.io.FileOutputStream;
import java.io.IOException;

public class BpmnClasspathDeployer implements ApplicationContextAware
{
    private File deployFolder;

    private String activitiProcessDefinitionPattern;

    private PathMatchingResourcePatternResolver resolver;

    private transient Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Scanning for Activiti process definitions");
        }

        try
        {
            Resource[] processDefinitions = getResolver().getResources(getActivitiProcessDefinitionPattern());
            for ( Resource processDefinition : processDefinitions )
            {
                String resourceFilename = processDefinition.getFilename();
                if ( log.isInfoEnabled() )
                {
                    log.info("Found activiti resource '" + resourceFilename + "'");
                }

                File target = new File(getDeployFolder() + File.separator + resourceFilename);
                if ( !target.exists() )
                {
                    if ( log.isDebugEnabled() )
                    {
                        log.debug("Copying resource '" + resourceFilename + "' to deploy folder.");
                    }
                    // NOTE: FileCopyUtils will close both the input and the output streams.
                    FileCopyUtils.copy(processDefinition.getInputStream(), new FileOutputStream(target));
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

    public PathMatchingResourcePatternResolver getResolver()
    {
        return resolver;
    }

    public void setResolver(PathMatchingResourcePatternResolver resolver)
    {
        this.resolver = resolver;
    }
}
