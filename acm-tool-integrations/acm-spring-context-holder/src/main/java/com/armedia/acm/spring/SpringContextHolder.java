package com.armedia.acm.spring;

/*-
 * #%L
 * Tool Integrations: Spring Child Context Holder
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

import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.files.ConfigurationFileDeletedEvent;
import com.armedia.acm.spring.events.ContextAddedEvent;
import com.armedia.acm.spring.events.ContextRemovedEvent;
import com.armedia.acm.spring.events.ContextReplacedEvent;
import com.armedia.acm.spring.exceptions.AcmContextHolderException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SpringContextHolder
        implements ApplicationContextAware, ApplicationListener<AbstractConfigurationFileEvent>, ApplicationEventPublisherAware
{
    private ApplicationContext toplevelContext;
    private Map<String, AbstractApplicationContext> childContextMap = new ConcurrentHashMap<>();

    private Logger log = LogManager.getLogger(getClass());
    private ApplicationEventPublisher applicationEventPublisher;

    // parent folder is "spring" and name is like spring-config-*[-*].xml
    private Pattern pattern = Pattern.compile(".*\\.arkcase/acm/spring/spring-config(-\\w+)+\\.xml");

    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent fileEvent)
    {
        log.debug("Event type: [{}]. Event base filename: [{}]", fileEvent.getClass().getName(), fileEvent.getBaseFileName());

        File eventFile = fileEvent.getConfigFile();

        checkIfSpringConfigWasAdded(fileEvent, eventFile);

        checkIfSpringConfigWasDeleted(fileEvent, eventFile);

        checkIfSpringConfigWasModified(fileEvent, eventFile);

    }

    private void checkIfSpringConfigWasModified(AbstractConfigurationFileEvent fileEvent, File eventFile)
    {
        if (fileEvent instanceof ConfigurationFileChangedEvent)
        {
            if (isSpringConfigFile(eventFile))
            {
                try
                {
                    AbstractApplicationContext context = createContextFromFile(eventFile);
                    replaceContext(eventFile.getName(), context);
                }
                catch (IOException e)
                {
                    log.error("Could not add context from file: {}. Error message:{}", eventFile.getName(), e.getMessage(), e);
                }
            }
            else if (isSpringConfigFolderModified(eventFile))
            {
                try
                {
                    AbstractApplicationContext context = createContextFromFolder(eventFile.getParentFile());
                    replaceContext(eventFile.getParentFile().getName(), context);
                }
                catch (IOException e)
                {
                    log.error("Could not add context from folder: {}. Error message: {}", eventFile.getParentFile().getName(),
                            e.getMessage(), e);
                }
            }
        }
    }

    private AbstractApplicationContext createContextFromFiles(String name, String... filesPaths) throws IOException
    {
        if (filesPaths == null || filesPaths.length < 1)
        {
            throw new AcmContextHolderException("files must not be null or empty. Reason[" + (filesPaths == null ? "null" : "empty") + "]");
        }
        log.info("Creating context with name {} and files.length = {}", name, filesPaths.length);

        try
        {
            return new FileSystemXmlApplicationContext(filesPaths, true, toplevelContext);
        }
        catch (BeansException be)
        {
            log.error("Could not load Spring context from files '{}' due to '{}'", Arrays.toString(filesPaths), be.getMessage(), be);
            return null;
            // throw be;
        }
    }

    private AbstractApplicationContext createContextFromFolder(File configFile) throws IOException
    {
        log.info("Creating context from folder: {}", configFile.getCanonicalPath());

        List<String> configFiles = Files.walk(configFile.toPath(), 1)
                .filter(p -> p.toFile().isFile() && p.toFile().getName().startsWith("spring-") && p.toFile().getName().endsWith("xml"))
                .map(p -> {
                    try
                    {
                        // the canonical path will be an absolute path. But it will start with a / on Linux,
                        // which Spring will treat as a relative path. Must start with file: to force an absolute path.
                        return "file:" + p.toFile().getCanonicalPath();
                    }
                    catch (IOException e)
                    {
                        throw new UncheckedIOException(e);
                    }
                }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        if (configFiles.size() < 1)
        {
            return null;
        }
        return createContextFromFiles(configFile.getName(), configFiles.toArray(new String[configFiles.size()]));
    }

    private void replaceContext(String contextName, AbstractApplicationContext context)
    {
        if (context == null)
        {
            log.warn("Will not replace existing context with name {} with null! Use removeContext method to remove a context!",
                    contextName);
            return;
        }

        log.info("Replacing context: {}", contextName);

        AbstractApplicationContext oldContext = childContextMap.get(contextName);
        childContextMap.put(contextName, context);
        if (oldContext != null)
        {
            oldContext.close();
        }
        applicationEventPublisher.publishEvent(new ContextReplacedEvent(this, contextName));
    }

    private AbstractApplicationContext createContextFromFile(File configFile) throws BeansException, IOException
    {
        log.info("Creating context from file: {}", configFile.getCanonicalPath());

        // the canonical path will be an absolute path. But it will start with a / on Linux,
        // which Spring will treat as a relative path. Must start with file: to force an absolute path.
        return createContextFromFiles(configFile.getName(), "file:" + configFile.getCanonicalPath());
    }

    private void checkIfSpringConfigWasDeleted(AbstractConfigurationFileEvent fileEvent, File eventFile)
    {
        if (fileEvent instanceof ConfigurationFileDeletedEvent && (isSpringConfigFile(eventFile) || isSpringConfigFolderDeleted(eventFile)))
        {
            removeContext(eventFile.getName());
        }
    }

    private void checkIfSpringConfigWasAdded(AbstractConfigurationFileEvent fileEvent, File eventFile)
    {
        if (fileEvent instanceof ConfigurationFileAddedEvent && (isSpringConfigFile(eventFile) || isSpringConfigFolderAdded(eventFile)))
        {
            try
            {
                if (eventFile.isDirectory())
                {
                    addContextFromFolder(eventFile);
                }
                else
                {
                    addContextFromFile(eventFile);
                }
            }
            catch (IOException e)
            {
                log.error("Could not add context from file: {}", e.getMessage(), e);
            }
        }
    }

    private boolean isSpringConfigFile(File eventFile)
    {
        Matcher matcher = pattern.matcher(eventFile.toURI().getPath());
        return matcher.matches();
    }

    private boolean isSpringConfigFolderAdded(File eventFile)
    {
        return eventFile.isDirectory() && eventFile.getName().startsWith("spring-config");
    }

    private boolean isSpringConfigFolderDeleted(File eventFile)
    {
        return eventFile.getName().startsWith("spring-config");
    }

    private boolean isSpringConfigFolderModified(File eventFile)
    {
        if (eventFile.isFile())
        {
            return eventFile.getParentFile().getName().startsWith("spring-config");
        }
        else
        {
            return eventFile.isDirectory() && eventFile.getName().startsWith("spring-config");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        log.debug("Got the application context");
        toplevelContext = applicationContext;
    }

    public <T> Map<String, T> getAllBeansOfType(Class<T> type)
    {
        log.debug("Looking for beans of type: {}", type.getName());
        Map<String, T> beans = toplevelContext.getBeansOfType(type);

        for (AbstractApplicationContext childContext : childContextMap.values())
        {
            Map<String, T> childBeans = childContext.getBeansOfType(type);
            beans.putAll(childBeans);
        }

        log.debug("Returning {} beans of type {}", beans.size(), type.getName());
        beans = beans.entrySet()
                .stream()
                .filter(it -> !ScopedProxyUtils.isScopedTarget(it.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return Collections.unmodifiableMap(beans);
    }

    public void replaceContextFromFile(File configFile) throws IOException, BeansException
    {
        AbstractApplicationContext context = createContextFromFile(configFile);
        replaceContext(configFile.getName(), context);
    }

    public void addContextFromFolder(File configFile) throws IOException, BeansException
    {
        log.info("Adding context from folder: {}", configFile.getCanonicalPath());

        List<String> configFiles = Files.walk(configFile.toPath(), 1)
                .filter(p -> p.toFile().isFile() && p.toFile().getName().startsWith("spring-") && p.toFile().getName().endsWith("xml"))
                .map(p -> {
                    try
                    {
                        // the canonical path will be an absolute path. But it will start with a / on Linux,
                        // which Spring will treat as a relative path. Must start with file: to force an absolute path.
                        return "file:" + p.toFile().getCanonicalPath();
                    }
                    catch (IOException e)
                    {
                        throw new UncheckedIOException(e);
                    }
                }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        if (configFiles.size() < 1)
        {
            return;
        }
        addContextFromFiles(configFile.getName(), configFiles.toArray(new String[configFiles.size()]));
    }

    public void addContextFromFile(File configFile) throws IOException, BeansException
    {
        log.info("Adding context from file: {}", configFile.getCanonicalPath());

        // the canonical path will be an absolute path. But it will start with a / on Linux,
        // which Spring will treat as a relative path. Must start with file: to force an absolute path.
        addContextFromFiles(configFile.getName(), "file:" + configFile.getCanonicalPath());
    }

    public void addContextFromFiles(String name, String... filesPaths)
    {
        if (filesPaths == null || filesPaths.length < 1)
        {
            throw new AcmContextHolderException("files must not be null or empty. Reason[" + (filesPaths == null ? "null" : "empty") + "]");
        }
        log.info("Adding context with name: {} and files.length = {}", name, filesPaths.length);

        try
        {
            AbstractApplicationContext child = new FileSystemXmlApplicationContext(filesPaths, true, toplevelContext);
            childContextMap.put(name, child);
            applicationEventPublisher.publishEvent(new ContextAddedEvent(this, name));
        }
        catch (BeansException be)
        {
            log.error("Could not load Spring context from files: '{}' due to error '{}'", Arrays.toString(filesPaths), be.getMessage(), be);
            // throw be;
        }
    }

    public void removeContext(String configFileName)
    {
        if (childContextMap.containsKey(configFileName))
        {
            log.info("Removing child context created from file: {}", configFileName);
            AbstractApplicationContext child = childContextMap.get(configFileName);
            childContextMap.remove(configFileName);
            child.close();
            applicationEventPublisher.publishEvent(new ContextRemovedEvent(this, configFileName));
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        log.debug("The application event publisher has been set!");
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public <T> T getBeanByName(String name, Class<T> type)
    {
        return toplevelContext.getBean(name, type);
    }

    public <T> T getBeanByNameIncludingChildContexts(String name, Class<T> type)
    {
        if (toplevelContext.containsBean(name))
        {
            return toplevelContext.getBean(name, type);
        }

        for (Map.Entry<String, AbstractApplicationContext> c : childContextMap.entrySet())
        {
            log.debug("context name: {}", c.getKey());
            log.debug("bean names: {}", Arrays.asList(c.getValue().getBeanDefinitionNames()));
            if (c.getValue().containsBean(name))
            {
                return c.getValue().getBean(name, type);
            }
        }

        throw new NoSuchBeanDefinitionException(name);
    }
}
