package com.armedia.acm.files;

import org.apache.commons.vfs2.FileChangeEvent;
import org.springframework.context.ApplicationEvent;

import java.io.File;

/**
 * Created by dmiller on 2/20/14.
 */
public class AbstractConfigurationFileEvent extends ApplicationEvent
{
    private File configFile;
    private String baseFileName;

    public AbstractConfigurationFileEvent(FileChangeEvent source)
    {
        super(source);

    }

    public File getConfigFile()
    {
        return configFile;
    }

    public void setConfigFile(File configFile)
    {
        this.configFile = configFile;
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
