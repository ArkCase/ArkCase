package com.armedia.acm.services.config.lookups.model;

import com.armedia.acm.configuration.annotations.MapValue;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mario.gjurcheski
 *
 *
 */
public class AcmLookupConfig
{

    public static final String LOOKUPS_PROP_KEY = "lookupsConfiguration";

    private Map<String, Object> lookups = new HashMap<>();

    @MapValue(value = LOOKUPS_PROP_KEY, convertFromTheRootKey = true, configurationName = "lookupsConfiguration")
    public Map<String, Object> getLookups()
    {
        return lookups;
    }

    public void setLookups(Map<String, Object> lookups)
    {
        this.lookups = lookups;
    }
}
