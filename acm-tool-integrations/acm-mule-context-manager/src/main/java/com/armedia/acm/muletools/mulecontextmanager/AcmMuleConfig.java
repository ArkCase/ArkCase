package com.armedia.acm.muletools.mulecontextmanager;

import org.springframework.beans.factory.annotation.Value;

public class AcmMuleConfig
{
    @Value("${mule.workingDirectory}")
    private String workingDirectory;

    public String getWorkingDirectory()
    {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory)
    {
        this.workingDirectory = workingDirectory;
    }
}
