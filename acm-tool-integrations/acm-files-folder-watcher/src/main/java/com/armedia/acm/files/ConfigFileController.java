package com.armedia.acm.files;

import org.apache.commons.vfs2.FileObject;
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

        log.info("Monitoring config folder: {}", getConfigFolder().getName());

        getConfigFolderMonitor().addFile(getConfigFolder());
        getConfigFolderMonitor().start();

    }

    public void stopBean()
    {

        log.info("Stopping monitoring of folder " + getConfigFolder().getName());

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
