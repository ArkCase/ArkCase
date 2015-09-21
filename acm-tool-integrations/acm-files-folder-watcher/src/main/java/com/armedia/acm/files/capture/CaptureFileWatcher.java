package com.armedia.acm.files.capture;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.FileTypeSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CaptureFileWatcher implements FileListener, ApplicationEventPublisherAware, ApplicationContextAware
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private FileObject baseFolder;
    private String fileExtensions;

    private String baseFolderPath;
    private List<String> fileExtensionsList;
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        if (log.isDebugEnabled())
        {
            log.debug("The application context has been set!");
            log.debug("Looking for files in: " + getBaseFolder().getName());
        }

        // this event just tells us when the whole application context is ready.  we don't actually need the context.
        try
        {
            //all this because of FileTypeSelector(FileType.FILE_OR_FOLDER) returns 0 elements
            FileObject[] existingFiles = getBaseFolder().findFiles(new FileTypeSelector(FileType.FILE));
            FileObject[] existingFolders = getBaseFolder().findFiles(new FileTypeSelector(FileType.FOLDER));

            int fileCount = existingFiles == null ? 0 : existingFiles.length;
            int folderCount = existingFolders == null ? 0 : existingFolders.length;

            int totalCount = fileCount + folderCount;

            if (totalCount > 0)
            {
                FileObject[] existingFilesAndFolders = new FileObject[totalCount];
                existingFilesAndFolders = Arrays.copyOf(existingFiles, totalCount);
                System.arraycopy(existingFolders, 0, existingFilesAndFolders, fileCount, folderCount);

                for (FileObject current : existingFilesAndFolders)
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Raising event for file " + current.getName());
                    }
                    fileCreated(new FileChangeEvent(current));
                }
            }

        } catch (Exception e)
        {
            log.error("Could not find existing files: " + e.getMessage(), e);
            throw new BeanCreationException("Could not find existing files: " + e.getMessage(), e);
        }
    }

    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("file added: " + fileChangeEvent.getFile().getName());
        }

        File eventFile = getEventFile(fileChangeEvent);
        String baseFilePath = getEventFileBasePath(eventFile);
        String fileExtension = getFileExtension(fileChangeEvent);

        // only publish event if in list of allowed file extensions
        if (getFileExtensionsList().contains(fileExtension))
        {
            CaptureFileAddedEvent event = new CaptureFileAddedEvent(fileChangeEvent);
            event.setBaseFileName(baseFilePath);
            event.setCaptureFile(eventFile);

            getApplicationEventPublisher().publishEvent(event);
        }
    }

    @Override
    public void fileChanged(FileChangeEvent arg0) throws Exception
    {
        // do nothing

    }

    @Override
    public void fileDeleted(FileChangeEvent arg0) throws Exception
    {
        // do nothing

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
        return new File(fileUrl.toURI());
    }

    private String getFileExtension(FileChangeEvent fileChangeEvent) throws Exception
    {
        FileObject fileObject = fileChangeEvent.getFile();
        return fileObject.getName().getExtension();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        log.debug("The application event publisher has been set!");
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
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
                File baseFile = new File(baseUrl.toURI());
                setBaseFolderPath(baseFile.getCanonicalPath());

            } catch (URISyntaxException | IOException e)
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
    }

    public String getFileExtensions()
    {
        return fileExtensions;
    }

    public void setFileExtensions(String fileExtensions)
    {
        this.fileExtensions = fileExtensions;

        // set file extensions list
        this.fileExtensionsList = new ArrayList<String>();
        setFileExtensionsList(Arrays.asList(fileExtensions.trim().split("\\s*,\\s*")));
    }

    public List<String> getFileExtensionsList()
    {
        return fileExtensionsList;
    }

    public void setFileExtensionsList(List<String> fileExtensionsList)
    {
        this.fileExtensionsList = fileExtensionsList;
    }


}
