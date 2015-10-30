package com.armedia.acm.files;

import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * This listener handle FileEvent and process the file in the FileEvent object.
 *
 * Created by riste.tutureski on 10/08/2015.
 */
public abstract class FileEventListener implements ApplicationListener<FileEvent>
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private FileObject watchFolder;
    private FileObject workingFolder;
    private FileObject completedFolder;
    private FileObject errorFolder;

    @Override
    public void onApplicationEvent(FileEvent event)
    {

    }

    /**
     * Check if the event is supported for the listener.
     *
     * @param event - raised event
     * @return - true/false
     */
    public boolean isSupported(FileEvent event)
    {
        return event != null && event.getType().equalsIgnoreCase(getEventType()) ? true : false;
    }

    public boolean isFileAddedEvent(FileEvent event)
    {
        return event instanceof FileAddedEvent ? true: false;
    }

    public boolean isFileChangedEvent(FileEvent event)
    {
        return event instanceof FileChangedEvent ? true: false;
    }

    public boolean isFileDeletedEvent(FileEvent event)
    {
        return event instanceof FileDeletedEvent ? true: false;
    }

    public abstract String getEventType();

    public FileObject getWatchFolder()
    {
        return watchFolder;
    }

    public void setWatchFolder(FileObject watchFolder)
    {
        this.watchFolder = watchFolder;
    }

    public FileObject getWorkingFolder()
    {
        return workingFolder;
    }

    public void setWorkingFolder(FileObject workingFolder)
    {
        this.workingFolder = workingFolder;
    }

    public FileObject getCompletedFolder()
    {
        return completedFolder;
    }

    public void setCompletedFolder(FileObject completedFolder)
    {
        this.completedFolder = completedFolder;
    }

    public FileObject getErrorFolder()
    {
        return errorFolder;
    }

    public void setErrorFolder(FileObject errorFolder)
    {
        this.errorFolder = errorFolder;
    }
}
