package com.armedia.acm.plugins.person.model;

import com.armedia.acm.plugins.ecm.model.EcmFile;

/**
 * Created by dragan.simonovski on 5/25/2017.
 */
public class UploadImageRequest
{
    private boolean isDefault;

    private String description;

    private EcmFile ecmFile;

    public boolean isDefault()
    {
        return isDefault;
    }

    public void setDefault(boolean aDefault)
    {
        isDefault = aDefault;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public EcmFile getEcmFile()
    {
        return ecmFile;
    }

    public void setEcmFile(EcmFile ecmFile)
    {
        this.ecmFile = ecmFile;
    }
}
