package com.armedia.acm.spring;

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

/**
 * Created by armdev on 4/17/14.
 */
public class SpringClasspathCopier implements ApplicationContextAware
{
    private File deployFolder;

    private String resourcePattern;

    private PathMatchingResourcePatternResolver resolver;

    private transient Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Scanning for resources matching '" + getResourcePattern() + "'");
        }

        try
        {
            if ( ! getDeployFolder().exists() )
            {
                if ( log.isInfoEnabled() )
                {
                    log.info("Creating folder '" + getDeployFolder().getCanonicalPath() + "'");
                }
                getDeployFolder().mkdirs();
            }

            Resource[] matchingResources = getResolver().getResources(getResourcePattern());
            for ( Resource resource : matchingResources )
            {
                String resourceFilename = resource.getFilename();
                if ( log.isInfoEnabled() )
                {
                    log.info("Found resource '" + resourceFilename + "'");
                }

                File target = new File(getDeployFolder() + File.separator + resourceFilename);
                if ( !target.exists() )
                {
                    if ( log.isDebugEnabled() )
                    {
                        log.debug("Copying resource '" + resourceFilename + "' to deploy folder.");
                    }
                    // NOTE: FileCopyUtils will close both the input and the output streams.
                    FileCopyUtils.copy(resource.getInputStream(), new FileOutputStream(target));
                }
            }
        } catch (IOException e)
        {
            log.error("Could not copy resource: " + e.getMessage(), e);
        }

        if ( log.isInfoEnabled() )
        {
            log.info("Done scanning for resources matching " + getResourcePattern() + "'");
        }
    }

    public File getDeployFolder()
    {
        return deployFolder;
    }

    public void setDeployFolder(File deployFolder)
    {
        this.deployFolder = deployFolder;
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
