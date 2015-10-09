package com.armedia.acm.files.capture;

import org.apache.commons.vfs2.FileChangeEvent;
import org.springframework.context.ApplicationEvent;

import java.io.File;

public class AbstractConvertFileEvent extends ApplicationEvent
{
    private File convertedFile;
    private String baseFileName;

    public AbstractConvertFileEvent(FileChangeEvent source)
    {
        super(source);

    }

    public File getConvertedFile()
    {
        return convertedFile;
    }

    public void setConvertedFile(File convertedFile)
    {
        this.convertedFile = convertedFile;
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
