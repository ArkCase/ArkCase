package com.armedia.acm.spring;


import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringContextHolder implements ApplicationContextAware
{
    private ApplicationContext toplevelContext;
    private Map<String, AbstractApplicationContext> childContextMap =
            new ConcurrentHashMap<String, AbstractApplicationContext>();

    private Logger log = LoggerFactory.getLogger(getClass());

    private FileObject springFolder;
    private DefaultFileMonitor springFolderMonitor;

    public void startWatchingSpringFolder()
    {
        getSpringFolderMonitor().addFile(getSpringFolder());
        getSpringFolderMonitor().start();
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
        log.debug("Adding context from file " + configFile.getCanonicalPath());
        AbstractApplicationContext child = new FileSystemXmlApplicationContext(
                new String[] {configFile.getCanonicalPath()}, true, toplevelContext);
        childContextMap.put(configFile.getName(), child);
    }

    public void removeContext(String configFileName)
    {
        if ( childContextMap.containsKey(configFileName) )
        {
            log.debug("Removing child context created from file " + configFileName);
            AbstractApplicationContext child = childContextMap.get(configFileName);
            childContextMap.remove(configFileName);
            child.close();
        }
    }

    public FileObject getSpringFolder() {
        return springFolder;
    }

    public void setSpringFolder(FileObject springFolder)
    {
        this.springFolder = springFolder;
    }

    public DefaultFileMonitor getSpringFolderMonitor()
    {
        return springFolderMonitor;
    }

    public void setSpringFolderMonitor(DefaultFileMonitor springFolderMonitor)
    {
        this.springFolderMonitor = springFolderMonitor;
    }
}
