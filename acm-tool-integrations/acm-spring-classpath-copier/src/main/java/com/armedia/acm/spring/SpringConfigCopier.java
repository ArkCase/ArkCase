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

import org.apache.commons.io.FileUtils;
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
import java.util.List;

/**
 * Created by armdev on 4/17/14.
 */
public class SpringConfigCopier implements ApplicationContextAware
{
    /**
     * Built-in Spring configuration files are copied to this folder
     */
    String builtinFolderPath;
    /**
     * Custom Spring configuration files are copied to this folder
     */
    String customFolderPath;
    /**
     * Root custom folder, mapped to web application root (see context.xml)
     */
    String customRoot;
    private List<String> resourcePatterns;
    private PathMatchingResourcePatternResolver resolver;
    private Logger log = LogManager.getLogger(getClass());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        File builtinFolder = new File(builtinFolderPath);
        File customFolder = new File(customFolderPath);

        try
        {
            // create built-in folder if it don't exist
            if (!builtinFolder.exists())
            {
                log.info("Creating folder '{}'", builtinFolderPath);
                builtinFolder.mkdirs();
            }
            // clean-up the folder from potential residue
            FileUtils.cleanDirectory(builtinFolder);

            // create custom folder if it don't exist
            if (!customFolder.exists())
            {
                log.info("Creating folder '{}'", customFolderPath);
                customFolder.mkdirs();
            }
            // clean-up the folder from potential residue
            FileUtils.cleanDirectory(customFolder);

            // copy all the Spring configurations provided with the WAR into the built-in folder
            for (String resourcePattern : resourcePatterns)
            {
                log.debug("Scanning for resources matching '{}'", resourcePattern);
                Resource[] matchingResources = getResolver().getResources(resourcePattern);
                for (Resource resource : matchingResources)
                {
                    String resourceFilename = resource.getFilename();
                    log.debug("Found resource '{}'", resourceFilename);

                    if (resource.getURL().getPath().contains(customRoot))
                    {
                        // this file is a custom Spring configuration or belongs to extension module
                        File target = new File(customFolderPath + File.separator + resourceFilename);
                        log.debug("Copying resource '{}' to custom folder.", resourceFilename);
                        // NOTE: FileCopyUtils will close both the input and the output streams.
                        FileCopyUtils.copy(resource.getInputStream(), new FileOutputStream(target));

                    }
                    else
                    {
                        // this file belongs to the WAR we are providing
                        File target = new File(builtinFolderPath + File.separator + resourceFilename);
                        log.debug("Copying resource '{}' to builtin folder.", resourceFilename);
                        // NOTE: FileCopyUtils will close both the input and the output streams.
                        FileCopyUtils.copy(resource.getInputStream(), new FileOutputStream(target));
                    }
                    log.info("Copied resource '{}' to builtin folder.", resourceFilename);
                    log.debug("Done scanning for resources matching '{}'", resourcePattern);
                }
            }

            log.info("Done copying resources to builtin folder [{}].", builtinFolderPath);
        }
        catch (IOException e)
        {
            log.error("Could not copy resource: {}", e.getMessage(), e);
        }
    }

    public List<String> getResourcePatterns()
    {
        return resourcePatterns;
    }

    public void setResourcePatterns(List<String> resourcePatterns)
    {
        this.resourcePatterns = resourcePatterns;
    }

    public PathMatchingResourcePatternResolver getResolver()
    {
        return resolver;
    }

    public void setResolver(PathMatchingResourcePatternResolver resolver)
    {
        this.resolver = resolver;
    }

    public String getBuiltinFolderPath()
    {
        return builtinFolderPath;
    }

    public void setBuiltinFolderPath(String builtinFolderPath)
    {
        this.builtinFolderPath = builtinFolderPath;
    }

    public String getCustomFolderPath()
    {
        return customFolderPath;
    }

    public void setCustomFolderPath(String customFolderPath)
    {
        this.customFolderPath = customFolderPath;
    }

    public String getCustomRoot()
    {
        return customRoot;
    }

    public void setCustomRoot(String customRoot)
    {
        this.customRoot = customRoot;
    }
}
