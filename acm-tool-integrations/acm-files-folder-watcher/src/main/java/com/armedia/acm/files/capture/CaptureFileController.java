package com.armedia.acm.files.capture;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaptureFileController {
    private FileObject captureFolder;
    private DefaultFileMonitor captureFolderMonitor;

    private Logger log = LoggerFactory.getLogger(getClass());

    public void initBean()
    {
        if ( log.isDebugEnabled() )
        {
            try
            {
                log.debug("Monitoring capture folder " + getCaptureFolder().getURL());
            }
            catch ( FileSystemException fse ) {}

        }
        getCaptureFolderMonitor().addFile(getCaptureFolder());
        getCaptureFolderMonitor().start();
    }

    public void stopBean()
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Stopping monitoring of capture folder " + getCaptureFolder().getName());
        }
        getCaptureFolderMonitor().stop();
    }

    public FileObject getCaptureFolder() {
        return captureFolder;
    }

    public void setCaptureFolder(FileObject captureFolder) {
        this.captureFolder = captureFolder;
    }

    public DefaultFileMonitor getCaptureFolderMonitor() {
        return captureFolderMonitor;
    }

    public void setCaptureFolderMonitor(DefaultFileMonitor captureFolderMonitor) {
        this.captureFolderMonitor = captureFolderMonitor;
    }
}
