package com.armedia.acm.plugins.person.model;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dragan.simonovski on 5/25/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadImageRequest
{
    @JsonProperty
    private boolean isDefault;

    private String description;

    private EcmFile ecmFile;

    public boolean isDefault()
    {
        return isDefault;
    }

    public void setDefault(boolean isDefault)
    {
        this.isDefault = isDefault;
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
