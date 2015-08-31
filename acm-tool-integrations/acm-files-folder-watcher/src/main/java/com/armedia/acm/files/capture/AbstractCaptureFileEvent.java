package com.armedia.acm.files.capture;

import java.io.File;

import org.apache.commons.vfs2.FileChangeEvent;
import org.springframework.context.ApplicationEvent;

public class AbstractCaptureFileEvent extends ApplicationEvent
{
    private File captureFile;
    private String baseFileName;

    public AbstractCaptureFileEvent(FileChangeEvent source)
    {
        super(source);

    }

    public File getCaptureFile() {
        return captureFile;
    }

    public void setCaptureFile(File captureFile) {
        this.captureFile = captureFile;
    }

    public String getBaseFileName()
    {
        return baseFileName;
    }

    public void setBaseFileName(String baseFileName)
    {
        this.baseFileName = baseFileName;
    }
}
