package com.armedia.acm.files;

/*-
 * #%L
 * Tool Integrations: Folder Watcher
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


import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.FileTypeSelector;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Watch for file events in the ACM configuration folder. Raise application events for each such event.
 */
public class ConfigFileWatcher implements FileListener, ApplicationEventPublisherAware, ApplicationContextAware
{
    private Logger log = LogManager.getLogger(getClass());
    private FileObject baseFolder;
    private String baseFolderPath;
    private ApplicationEventPublisher applicationEventPublisher;
    private List<String> ignoreFolders;
    private String ignoreFolderPath;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        if (log.isDebugEnabled())
        {
            log.debug("The application context has been set!");
            log.debug("Looking for files in: " + getBaseFolder().getName());
        }

        // this event just tells us when the whole application context is ready. we don't actually need the context.
        try
        {
            // all this because of FileTypeSelector(FileType.FILE_OR_FOLDER) returns 0 elements
            FileObject[] existingFiles = getBaseFolder().findFiles(new FileTypeSelector(FileType.FILE));
            FileObject[] existingFolders = getBaseFolder().findFiles(new FileTypeSelector(FileType.FOLDER));
            FileObject[] existingFilesAndFolders = new FileObject[existingFiles.length + existingFolders.length];
            existingFilesAndFolders = Arrays.copyOf(existingFiles, existingFiles.length + existingFolders.length);
            System.arraycopy(existingFolders, 0, existingFilesAndFolders, existingFiles.length, existingFolders.length);

            for (FileObject current : existingFilesAndFolders)
            {
                log.trace("Raising event for file [{}]", current.getName());
                fileCreated(new FileChangeEvent(current));
            }
        }
        catch (Exception e)
        {
            log.error("Could not find existing files: " + e.getMessage(), e);
            throw new BeanCreationException("Could not find existing files: " + e.getMessage(), e);
        }
    }

    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) throws Exception
    {
        if (ignoreThisFile(fileChangeEvent.getFile().getURL()))
        {
            return;
        }

        if (log.isDebugEnabled())
        {
            log.debug("file added: " + fileChangeEvent.getFile().getName());
        }

        File eventFile = getEventFile(fileChangeEvent);
        String baseFilePath = getEventFileBasePath(eventFile);

        ConfigurationFileAddedEvent event = new ConfigurationFileAddedEvent(fileChangeEvent);
        event.setBaseFileName(baseFilePath);
        event.setConfigFile(eventFile);

        getApplicationEventPublisher().publishEvent(event);
    }

    private String getEventFileBasePath(File eventFile) throws IOException
    {
        String filePath = eventFile.getCanonicalPath();
        String replaced = filePath.replace(getBaseFolderPath(), "");
        if (replaced.startsWith(File.separator) && replaced.length() > 1)
            replaced = replaced.substring(1);
        return replaced;
    }

    private File getEventFile(FileChangeEvent fileChangeEvent) throws FileSystemException, URISyntaxException
    {

        URL fileUrl = fileChangeEvent.getFile().getURL();
        return new File(new URI(fileUrl.toString().replace(" ", "%20")));
    }

    @Override
    public void fileDeleted(FileChangeEvent fileChangeEvent) throws Exception
    {
        if (ignoreThisFile(fileChangeEvent.getFile().getURL()))
        {
            return;
        }

        if (log.isDebugEnabled())
        {
            log.debug("file deleted: " + fileChangeEvent.getFile().getName());
        }

        File eventFile = getEventFile(fileChangeEvent);
        String baseFilePath = getEventFileBasePath(eventFile);

        ConfigurationFileDeletedEvent event = new ConfigurationFileDeletedEvent(fileChangeEvent);
        event.setBaseFileName(baseFilePath);
        event.setConfigFile(eventFile);

        getApplicationEventPublisher().publishEvent(event);

    }

    @Override
    public void fileChanged(FileChangeEvent fileChangeEvent) throws Exception
    {
        if (ignoreThisFile(fileChangeEvent.getFile().getURL()))
        {
            return;
        }

        if (log.isDebugEnabled())
        {
            log.debug("file changed: " + fileChangeEvent.getFile().getName());
        }

        File eventFile = getEventFile(fileChangeEvent);
        String baseFilePath = getEventFileBasePath(eventFile);

        ConfigurationFileChangedEvent event = new ConfigurationFileChangedEvent(fileChangeEvent);
        event.setBaseFileName(baseFilePath);
        event.setConfigFile(eventFile);

        getApplicationEventPublisher().publishEvent(event);
    }

    public boolean ignoreThisFile(URL fileUrl)
    {
        boolean retval = false;
        for (String ignoreFolder : getIgnoreFolders())
        {
            String ignoreFolderPath = getIgnoreFolderPath() + ignoreFolder;

            if (log.isTraceEnabled())
            {
                log.trace("checking " + fileUrl.toString() + " for " + ignoreFolderPath);
            }

            if (fileUrl.toString().contains(ignoreFolderPath))
            {
                if (log.isTraceEnabled())
                {
                    log.trace("this file will be ignored");
                }
                retval = true;
            }
        }

        return retval;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        log.debug("The application event publisher has been set!");
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public FileObject getBaseFolder()
    {
        return baseFolder;
    }

    public void setBaseFolder(FileObject baseFolder)
    {
        this.baseFolder = baseFolder;

        if (baseFolder != null)
        {
            try
            {
                URL baseUrl = baseFolder.getURL();
                File baseFile = new File(new URI(baseUrl.toString().replace(" ", "%20")));
                setBaseFolderPath(baseFile.getCanonicalPath());

            }
            catch (URISyntaxException | IOException e)
            {
                log.error("Something is wrong with the base folder url: " + e.getMessage(), e);
            }
        }
    }

    public String getBaseFolderPath()
    {
        return baseFolderPath;
    }

    public void setBaseFolderPath(String baseFolderPath)
    {
        this.baseFolderPath = baseFolderPath;

        this.ignoreFolderPath = baseFolderPath.contains("\\") ? baseFolderPath.replaceAll("\\\\", "/") : baseFolderPath;
    }

    public List<String> getIgnoreFolders()
    {
        return ignoreFolders;
    }

    public void setIgnoreFolders(List<String> ignoreFolders)
    {
        List<String> ignore = new ArrayList<>(ignoreFolders.size());
        for (String ignoreFolder : ignoreFolders)
        {
            ignore.add(ignoreFolder.replaceAll("\\\\", "/"));
        }
        this.ignoreFolders = ignore;
    }

    public String getIgnoreFolderPath()
    {
        return ignoreFolderPath;
    }
}
