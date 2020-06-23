package com.armedia.acm.activiti.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ana.serafimoska
 */
public class ActivityClassPathBpmnDeployer implements ApplicationContextAware
{
    private AcmBpmnService acmBpmnService;
    private String resourcePattern;
    private PathMatchingResourcePatternResolver resolver;

    private transient Logger log = LogManager.getLogger(getClass());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {

        log.info("Scanning for resources matching [{}]", getResourcePattern());
        try
        {
            String[] patterns = getResourcePattern().split(",");
            for (String pattern : patterns)
            {
                Resource[] matchingResources = getResolver().getResources(pattern);
                for (Resource resource : matchingResources)
                {
                    String resourceFilename = resource.getFilename();
                    log.info("Found resource [{}]", resourceFilename);
                    File file = new File(resourceFilename).getAbsoluteFile();
                    FileCopyUtils.copy(resource.getInputStream(), new FileOutputStream(file));
                    acmBpmnService.deploy(file, "", false, false);
                }
            }
        }
        catch (IOException e)
        {
            log.error("Could not copy resource: [{}]", e.getMessage(), e);
        }

        log.info("Done scanning for resources matching [{}]", getResourcePattern());
    }

    public AcmBpmnService getAcmBpmnService()
    {
        return acmBpmnService;
    }

    public void setAcmBpmnService(AcmBpmnService acmBpmnService)
    {
        this.acmBpmnService = acmBpmnService;
    }

    public String getResourcePattern()
    {
        return resourcePattern;
    }

    public void setResourcePattern(String resourcePattern)
    {
        this.resourcePattern = resourcePattern;
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