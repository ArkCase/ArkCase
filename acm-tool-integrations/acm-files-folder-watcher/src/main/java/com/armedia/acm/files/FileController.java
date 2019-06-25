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
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This controller control starting and stopping monitoring folder
 *
 * Created by riste.tutureski on 10/08/2015.
 */
public class FileController
{
    private Logger LOG = LogManager.getLogger(getClass());

    private FileObject watchFolder;
    private DefaultFileMonitor fileMonitor;

    /**
     * Start monitoring folder
     */
    public void init()
    {

        LOG.debug("Start monitoring watch folder '{}'", getWatchFolder().getName());

        getFileMonitor().addFile(getWatchFolder());
        getFileMonitor().start();
    }

    /**
     * Stop monitoring folder
     */
    public void destroy()
    {
        LOG.debug("Stop monitoring watch folder '{}'", getWatchFolder().getName());
        getFileMonitor().stop();
    }

    public FileObject getWatchFolder()
    {
        return watchFolder;
    }

    public void setWatchFolder(FileObject watchFolder)
    {
        this.watchFolder = watchFolder;
    }

    public DefaultFileMonitor getFileMonitor()
    {
        return fileMonitor;
    }

    public void setFileMonitor(DefaultFileMonitor fileMonitor)
    {
        this.fileMonitor = fileMonitor;
    }
}
