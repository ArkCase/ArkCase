package com.armedia.acm.files;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dmiller on 2/20/14.
 */
public class ConfigFileController
{
    private FileObject configFolder;
    private DefaultFileMonitor configFolderMonitor;

    private Logger log = LoggerFactory.getLogger(getClass());

    public void initBean()
    {
        if ( log.isDebugEnabled() )
        {
            try
            {
                log.debug("Monitoring config folder " + getConfigFolder().getURL());
            }
            catch ( FileSystemException fse ) {}

        }
        getConfigFolderMonitor().addFile(getConfigFolder());
        getConfigFolderMonitor().start();
    }

    public void stopBean()
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Stopping monitoring of folder " + getConfigFolder().getName());
        }
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
