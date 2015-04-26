package com.armedia.acm.spring;


import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.files.ConfigurationFileDeletedEvent;
import com.armedia.acm.spring.events.ContextAddedEvent;
import com.armedia.acm.spring.events.ContextRemovedEvent;
import com.armedia.acm.spring.exceptions.AcmContextHolderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.util.StringUtils;

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

public class SpringContextHolder implements ApplicationContextAware, ApplicationListener<AbstractConfigurationFileEvent>, ApplicationEventPublisherAware {
    private ApplicationContext toplevelContext;
    private Map<String, AbstractApplicationContext> childContextMap =
            new ConcurrentHashMap<String, AbstractApplicationContext>();

    private Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent fileEvent) {
        if (log.isDebugEnabled()) {
            log.debug("Event type: " + fileEvent.getClass().getName());
            log.debug("event base filename: " + fileEvent.getBaseFileName());
        }

        File eventFile = fileEvent.getConfigFile();

        checkIfSpringConfigWasAdded(fileEvent, eventFile);

        checkIfSpringConfigWasDeleted(fileEvent, eventFile);

        checkIfSpringConfigWasModified(fileEvent, eventFile);

    }

    private void checkIfSpringConfigWasModified(AbstractConfigurationFileEvent fileEvent, File eventFile) {
        if (fileEvent instanceof ConfigurationFileChangedEvent && (isSpringConfigFile(eventFile) || isSpringConfigFolderModified(eventFile))) {
            try {
                removeContext(eventFile.getName());
                addContextFromFile(eventFile);
            } catch (IOException e) {
                log.error("Could not add context from file: " + e.getMessage(), e);
            }
        }
    }

    private void checkIfSpringConfigWasDeleted(AbstractConfigurationFileEvent fileEvent, File eventFile) {
        if (fileEvent instanceof ConfigurationFileDeletedEvent && (isSpringConfigFile(eventFile) || isSpringConfigFolderDeleted(eventFile))) {
            removeContext(eventFile.getName());
        }
    }

    private void checkIfSpringConfigWasAdded(AbstractConfigurationFileEvent fileEvent, File eventFile) {
        if (fileEvent instanceof ConfigurationFileAddedEvent && (isSpringConfigFile(eventFile) || isSpringConfigFolderAdded(eventFile))) {
            try {
                if (eventFile.isDirectory())
                    addContextFromFolder(eventFile);
                else
                    addContextFromFile(eventFile);
            } catch (IOException e) {
                log.error("Could not add context from file: " + e.getMessage(), e);
            }
        }
    }

    private boolean isSpringConfigFile(File eventFile) {
        return eventFile.getParentFile().getName().equals("spring") &&
                eventFile.getName().startsWith("spring-config") &&
                eventFile.getName().endsWith(".xml");
    }

    private boolean isSpringConfigFolderAdded(File eventFile) {
        return eventFile.isDirectory() && eventFile.getName().startsWith("spring-config");
    }

    private boolean isSpringConfigFolderDeleted(File eventFile) {
        return eventFile.getName().startsWith("spring-config");
    }

    private boolean isSpringConfigFolderModified(File eventFile) {
        if (eventFile.isFile()) {
            return eventFile.getParentFile().getName().startsWith("spring-config");
        } else {
            return eventFile.isDirectory() && eventFile.getName().startsWith("spring-config");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.debug("Got the application context");
        toplevelContext = applicationContext;
    }

    public <T> Map<String, T> getAllBeansOfType(Class<T> type) {
        log.debug("Looking for beans of type: " + type.getName());
        Map<String, T> beans = toplevelContext.getBeansOfType(type);

        for (AbstractApplicationContext childContext : childContextMap.values()) {
            Map<String, T> childBeans = childContext.getBeansOfType(type);
            beans.putAll(childBeans);
        }

        log.debug("Returning " + beans.size() + " beans of type " + type.getName());
        return Collections.unmodifiableMap(beans);
    }

    public void addContextFromFolder(File configFile) throws IOException, BeansException {
        log.info("Adding context from folder " + configFile.getCanonicalPath());

        // the canonical path will be an absolute path.  But it will start with a / on Linux,
        // which Spring will treat as a relative path.  Must start with file: to force an absolute path.
        List<String> configFiles = Files
                .walk(configFile.toPath(), 1)
                .filter(p -> p.toFile().isFile()
                        && p.toFile().getName().startsWith("spring-")
                        && p.toFile().getName().endsWith("xml"))
                .map(p -> {
                    try {
                        return "file:" + p.toFile().getCanonicalPath();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        if (configFiles.size() < 1)
            return;
        addContextFromFiles(configFile.getName(), configFiles.toArray(new String[configFiles.size()]));
    }

    public void addContextFromFile(File configFile) throws IOException, BeansException {
        log.info("Adding context from file " + configFile.getCanonicalPath());

        // the canonical path will be an absolute path.  But it will start with a / on Linux,
        // which Spring will treat as a relative path.  Must start with file: to force an absolute path.

        addContextFromFiles(configFile.getName(),"file:" + configFile.getCanonicalPath());
    }

    public void addContextFromFiles(String name, String... filesPaths){
        if (filesPaths == null || filesPaths.length < 1)
            throw new AcmContextHolderException("files must not be null or empty. Reason[" + (filesPaths == null ? "null" : "empty") + "]");
        log.info("Adding context with name" + name + " and files.length = " + filesPaths.length);

        try {
            AbstractApplicationContext child = new FileSystemXmlApplicationContext(
                    filesPaths, true, toplevelContext);
            childContextMap.put(name, child);
            applicationEventPublisher.publishEvent(new ContextAddedEvent(this, name));
        } catch (BeansException be) {
            log.error("Could not load Spring context from files '" + Arrays.toString(filesPaths) + "' due to " +
                    "error '" + be.getMessage() + "'", be);
            //throw be;
        }
    }

    public void removeContext(String configFileName) {
        if (childContextMap.containsKey(configFileName)) {
            log.info("Removing child context created from file " + configFileName);
            AbstractApplicationContext child = childContextMap.get(configFileName);
            childContextMap.remove(configFileName);
            child.close();
            applicationEventPublisher.publishEvent(new ContextRemovedEvent(this, configFileName));
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        log.debug("The application event publisher has been set!");
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
