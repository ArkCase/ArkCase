package com.armedia.acm.files;

import org.apache.commons.vfs2.FileChangeEvent;
import org.springframework.context.ApplicationEvent;

import java.io.File;

/**
 * Created by riste.tutureski on 10/08/2015.
 */
public class FileEvent extends ApplicationEvent
{
    private File file;
    private String fileName;
    private String type;

    /**
     * Create a new FileEvent.
     *
     * @param source file change event
     */
    public FileEvent(FileChangeEvent source)
    {
        super(source);
    }

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
