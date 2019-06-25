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

import org.apache.commons.vfs2.FileObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

/**
 * This listener handle FileEvent and process the file in the FileEvent object.
 *
 * Created by riste.tutureski on 10/08/2015.
 */
public abstract class FileEventListener implements ApplicationListener<FileEvent>
{
    private Logger LOG = LogManager.getLogger(getClass());

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
     * @param event
     *            - raised event
     * @return - true/false
     */
    public boolean isSupported(FileEvent event)
    {
        return event != null && event.getType().equalsIgnoreCase(getEventType()) ? true : false;
    }

    public boolean isFileAddedEvent(FileEvent event)
    {
        return event instanceof FileAddedEvent ? true : false;
    }

    public boolean isFileChangedEvent(FileEvent event)
    {
        return event instanceof FileChangedEvent ? true : false;
    }

    public boolean isFileDeletedEvent(FileEvent event)
    {
        return event instanceof FileDeletedEvent ? true : false;
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
