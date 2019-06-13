package com.armedia.acm.spring;

/*-
 * #%L
 * Tool Integrations: Copy from Classpath to Working Folder
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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

    private transient Logger log = LogManager.getLogger(getClass());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        log.info("Scanning for resources matching [{}]", getResourcePattern());

        try
        {
            if (!getDeployFolder().exists())
            {
                log.info("Creating folder [{}]", getDeployFolder().getCanonicalPath());
                getDeployFolder().mkdirs();
            }

            String[] patterns = getResourcePattern().split(",");
            for (String pattern : patterns)
            {
                Resource[] matchingResources = getResolver().getResources(pattern);
                for (Resource resource : matchingResources)
                {
                    String resourceFilename = resource.getFilename();
                    log.info("Found resource [{}]", resourceFilename);

                    File target = new File(getDeployFolder() + File.separator + resourceFilename);
                    if (!target.exists())
                    {
                        log.debug("Copying resource [{}] to deploy folder.", resourceFilename);

                        // NOTE: FileCopyUtils will close both the input and the output streams.
                        FileCopyUtils.copy(resource.getInputStream(), new FileOutputStream(target));
                    }
                }
            }
        }
        catch (IOException e)
        {
            log.error("Could not copy resource: [{}]", e.getMessage(), e);
        }

        log.info("Done scanning for resources matching [{}]", getResourcePattern());
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
