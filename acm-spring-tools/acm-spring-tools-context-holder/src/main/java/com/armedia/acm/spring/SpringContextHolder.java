package com.armedia.acm.spring;


import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.files.ConfigurationFileDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringContextHolder implements ApplicationContextAware, ApplicationListener<AbstractConfigurationFileEvent>
{
    private ApplicationContext toplevelContext;
    private Map<String, AbstractApplicationContext> childContextMap =
            new ConcurrentHashMap<String, AbstractApplicationContext>();

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent fileEvent)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Event type: " + fileEvent.getClass().getName());
            log.debug("event base filename: " + fileEvent.getBaseFileName());
        }

        File eventFile = fileEvent.getConfigFile();

        checkIfSpringConfigWasAdded(fileEvent, eventFile);

        checkIfSpringConfigWasDeleted(fileEvent, eventFile);

        checkIfSpringConfigWasModified(fileEvent, eventFile);

    }

    private void checkIfSpringConfigWasModified(AbstractConfigurationFileEvent fileEvent, File eventFile) {
        if (fileEvent instanceof ConfigurationFileChangedEvent && isSpringConfigFile(eventFile))
        {
            try
            {
                removeContext(eventFile.getName());
                addContextFromFile(eventFile);
            }
            catch (IOException e)
            {
                log.error("Could not add context from file: " + e.getMessage(), e);
            }
        }
    }

    private void checkIfSpringConfigWasDeleted(AbstractConfigurationFileEvent fileEvent, File eventFile) {
        if (fileEvent instanceof ConfigurationFileDeletedEvent && isSpringConfigFile(eventFile))
        {
            removeContext(eventFile.getName());
        }
    }

    private void checkIfSpringConfigWasAdded(AbstractConfigurationFileEvent fileEvent, File eventFile) {
        if (fileEvent instanceof ConfigurationFileAddedEvent && isSpringConfigFile(eventFile))
        {
            try
            {
                addContextFromFile(eventFile);
            }
            catch (IOException e)
            {
                log.error("Could not add context from file: " + e.getMessage(), e);
            }
        }
    }

    private boolean isSpringConfigFile(File eventFile)
    {
        return eventFile.getParentFile().getName().equals("spring") &&
                eventFile.getName().startsWith("spring-config");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        log.debug("Got the application context");
        toplevelContext = applicationContext;
    }

    public <T> Map<String, T> getAllBeansOfType(Class<T> type)
    {
        log.debug("Looking for beans of type: " + type.getName());
        Map<String, T> beans = toplevelContext.getBeansOfType(type);

        for (AbstractApplicationContext childContext : childContextMap.values() )
        {
            Map<String, T> childBeans = childContext.getBeansOfType(type);
            beans.putAll(childBeans);
        }

        log.debug("Returning " + beans.size() + " beans of type " + type.getName());
        return Collections.unmodifiableMap(beans);
    }

    public void addContextFromFile(File configFile) throws IOException, BeansException
    {
        log.info("Adding context from file " + configFile.getCanonicalPath());
        AbstractApplicationContext child = new FileSystemXmlApplicationContext(
                new String[] {configFile.getCanonicalPath()}, true, toplevelContext);
        childContextMap.put(configFile.getName(), child);
    }

    public void removeContext(String configFileName)
    {
        if ( childContextMap.containsKey(configFileName) )
        {
            log.info("Removing child context created from file " + configFileName);
            AbstractApplicationContext child = childContextMap.get(configFileName);
            childContextMap.remove(configFileName);
            child.close();
        }
    }

}
