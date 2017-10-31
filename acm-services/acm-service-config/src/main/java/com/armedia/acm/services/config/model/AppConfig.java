package com.armedia.acm.services.config.model;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.objectonverter.ObjectConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class AppConfig implements AcmConfig, Serializable
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private static final long serialVersionUID = -1L;

    private String configName;
    private AcmApplication acmApplication;
    private String configDescription;
    private ObjectConverter objectConverter;

    @Override
    public String getConfigAsJson()
    {
        String json = getObjectConverter().getJsonMarshaller().marshal(getAcmApplication());
        return json == null ? "[]" : json;
    }

    @Override
    public String getConfigName()
    {
        return configName;
    }

    public void setConfigName(String configName)
    {
        this.configName = configName;
    }

    public AcmApplication getAcmApplication()
    {
        return acmApplication;
    }

    public void setAcmApplication(AcmApplication acmApplication)
    {
        this.acmApplication = acmApplication;
    }

    @Override
    public String getConfigDescription()
    {
        return configDescription;
    }

    public void setConfigDescription(String configDescription)
    {
        this.configDescription = configDescription;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }
}
