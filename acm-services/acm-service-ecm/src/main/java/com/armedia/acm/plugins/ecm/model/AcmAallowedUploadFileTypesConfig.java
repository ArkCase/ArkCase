package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.configuration.annotations.MapValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mario Gjurcheski 5/29/2020
 */
public class AcmAallowedUploadFileTypesConfig
{
    private Map<String, List<String>> allowedUploadFileTypes = new HashMap<>();

    @MapValue(value = "allowedUploadFileTypes")
    public Map<String, List<String>> getAllowedUploadFileTypes()
    {
        return allowedUploadFileTypes;
    }

    public void setAllowedUploadFileTypes(Map<String, List<String>> allowedUploadFileTypes)
    {
        this.allowedUploadFileTypes = allowedUploadFileTypes;
    }
}
