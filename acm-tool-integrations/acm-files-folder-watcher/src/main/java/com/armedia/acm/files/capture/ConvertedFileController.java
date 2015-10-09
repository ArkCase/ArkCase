package com.armedia.acm.files.capture;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConvertedFileController
{
    private FileObject convertedFolder;
    private DefaultFileMonitor convertedFileMonitor;

    private Logger log = LoggerFactory.getLogger(getClass());

    public void initBean()
    {
        if (log.isDebugEnabled())
        {
            try
            {
                log.debug("Monitoring converted files folder " + getConvertedFolder().getURL());
            } catch (FileSystemException fse)
            {
            }

        }
        getConvertedFileMonitor().addFile(getConvertedFolder());
        getConvertedFileMonitor().start();
    }

    public void stopBean()
    {
        if (log.isDebugEnabled())
        {
            log.debug("Stopping monitoring of converted files folder " + getConvertedFolder().getName());
        }
        getConvertedFileMonitor().stop();
    }

    public FileObject getConvertedFolder()
    {
        return convertedFolder;
    }

    public void setConvertedFolder(FileObject convertedFolder)
    {
        this.convertedFolder = convertedFolder;
    }

    public DefaultFileMonitor getConvertedFileMonitor()
    {
        return convertedFileMonitor;
    }

    public void setConvertedFileMonitor(DefaultFileMonitor convertedFileMonitor)
    {
        this.convertedFileMonitor = convertedFileMonitor;
    }
}
