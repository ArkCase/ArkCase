package com.armedia.acm.files;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This controller control starting and stopping monitoring folder
 *
 * Created by riste.tutureski on 10/08/2015.
 */
public class FileController
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

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
