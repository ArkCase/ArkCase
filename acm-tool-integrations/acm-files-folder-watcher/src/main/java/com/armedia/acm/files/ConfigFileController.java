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
 * Created by dmiller on 2/20/14.
 */
public class ConfigFileController
{
    private FileObject configFolder;
    private DefaultFileMonitor configFolderMonitor;

    private Logger log = LogManager.getLogger(getClass());

    public void initBean()
    {

        log.info("Monitoring config folder [{}]", getConfigFolder().getName());

        getConfigFolderMonitor().addFile(getConfigFolder());
        getConfigFolderMonitor().start();

    }

    public void stopBean()
    {

        log.info("Stopping monitoring of folder [{}]", getConfigFolder().getName());

        getConfigFolderMonitor().stop();
    }

    public FileObject getConfigFolder()
    {
        return configFolder;
    }

    public void setConfigFolder(FileObject configFolder)
    {
        this.configFolder = configFolder;
    }

    public DefaultFileMonitor getConfigFolderMonitor()
    {
        return configFolderMonitor;
    }

    public void setConfigFolderMonitor(DefaultFileMonitor configFolderMonitor)
    {
        this.configFolderMonitor = configFolderMonitor;
    }
}
