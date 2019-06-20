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
import java.util.Arrays;
import java.util.List;

/**
 * Watch folder changes and raise events if there is some new files
 * <p/>
 * Created by riste.tutureski on 10/08/2015.
 */
public class FileWatcher implements FileListener, ApplicationContextAware, ApplicationEventPublisherAware
{
    private Logger LOG = LogManager.getLogger(getClass());

    private FileObject watchFolder;
    private String watchFolderPath;
    private String allowedFileExtensions;
    private List<String> allowedFileExtensionsList;
    private ApplicationEventPublisher applicationEventPublisher;
    private String type;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        LOG.debug("Looking for files in folder '{}'", getWatchFolder().getName());

        // This event just tells us when the whole application context is ready. We don't actually need the context.
        try
        {
            // All this because of FileTypeSelector(FileType.FILE_OR_FOLDER) returns 0 elements
            FileObject[] existingFiles = getWatchFolder().findFiles(new FileTypeSelector(FileType.FILE));
            FileObject[] existingFolders = getWatchFolder().findFiles(new FileTypeSelector(FileType.FOLDER));

            int fileCount = existingFiles == null ? 0 : existingFiles.length;
            int folderCount = existingFolders == null ? 0 : existingFolders.length;

            int totalCount = fileCount + folderCount;

            if (totalCount > 0)
            {
                FileObject[] existingFilesAndFolders = Arrays.copyOf(existingFiles, totalCount);
                System.arraycopy(existingFolders, 0, existingFilesAndFolders, fileCount, folderCount);

                for (FileObject fileObject : existingFilesAndFolders)
                {
                    fileCreated(new FileChangeEvent(fileObject));
                }
            }

        }
        catch (Exception e)
        {
            LOG.error("Could not find existing files: {}", e.getMessage(), e);
            throw new BeanCreationException("Could not find existing files: " + e.getMessage(), e);
        }
    }

    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) throws Exception
    {
        String fileExtension = null;

        if (fileChangeEvent != null && fileChangeEvent.getFile() != null)
        {
            fileExtension = getFileExtension(fileChangeEvent.getFile());
        }

        if (getAllowedFileExtensionsList().contains(fileExtension) && fileChangeEvent != null)
        {
            LOG.debug("File {} added", fileChangeEvent.getFile().getName());

            // Get file and file name
            File file = getFile(fileChangeEvent);
            String fileName = getFileName(file);

            // Create event
            FileAddedEvent event = new FileAddedEvent(fileChangeEvent);
            event.setFile(file);
            event.setFileName(fileName);
            event.setType(getType());

            LOG.debug("Raise File Added Event for file {} ", fileChangeEvent.getFile().getName());

            // Publish the event
            getApplicationEventPublisher().publishEvent(event);
        }
    }

    @Override
    public void fileDeleted(FileChangeEvent fileChangeEvent) throws Exception
    {
        // Do nothing so far
    }

    @Override
    public void fileChanged(FileChangeEvent fileChangeEvent) throws Exception
    {
        // Do nothing so far
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Get file from the event
     *
     * @param fileChangeEvent
     *            - changed event
     * @return - File object
     * @throws FileSystemException
     * @throws URISyntaxException
     */
    private File getFile(FileChangeEvent fileChangeEvent) throws FileSystemException, URISyntaxException
    {
        URL fileUrl = fileChangeEvent.getFile().getURL();
        return new File(new URI(fileUrl.toString().replace(" ", "%20")));
    }

    /**
     * Take file name for given file
     *
     * @param file
     *            - the file
     * @return - the file name
     * @throws IOException
     */
    private String getFileName(File file) throws IOException
    {
        String filePath = file.getCanonicalPath();
        String replaced = filePath.replace(getWatchFolderPath(), "");
        if (replaced.startsWith(File.separator) && replaced.length() > 1)
        {
            replaced = replaced.substring(1);
        }

        return replaced;
    }

    /**
     * Take file extension
     *
     * @param fileObject
     * @return
     * @throws Exception
     */
    private String getFileExtension(FileObject fileObject) throws Exception
    {
        return fileObject.getName().getExtension();
    }

    public FileObject getWatchFolder()
    {
        return watchFolder;
    }

    public void setWatchFolder(FileObject watchFolder)
    {
        this.watchFolder = watchFolder;

        if (watchFolder != null)
        {
            try
            {
                URL baseUrl = watchFolder.getURL();
                File baseFile = new File(new URI(baseUrl.toString().replace(" ", "%20")));
                setWatchFolderPath(baseFile.getCanonicalPath());

            }
            catch (URISyntaxException | IOException e)
            {
                LOG.error("Cannot take watch folder path.", e);
            }
        }
    }

    public String getWatchFolderPath()
    {
        return watchFolderPath;
    }

    public void setWatchFolderPath(String watchFolderPath)
    {
        this.watchFolderPath = watchFolderPath;
    }

    public String getAllowedFileExtensions()
    {
        return allowedFileExtensions;
    }

    public void setAllowedFileExtensions(String allowedFileExtensions)
    {
        this.allowedFileExtensions = allowedFileExtensions;

        if (allowedFileExtensions != null)
        {
            setAllowedFileExtensionsList(Arrays.asList(allowedFileExtensions.trim().split("\\s*,\\s*")));
        }
    }

    public List<String> getAllowedFileExtensionsList()
    {
        return allowedFileExtensionsList;
    }

    public void setAllowedFileExtensionsList(List<String> allowedFileExtensionsList)
    {
        this.allowedFileExtensionsList = allowedFileExtensionsList;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
