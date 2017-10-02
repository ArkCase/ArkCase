package com.armedia.acm.services.config.service;

import com.armedia.acm.services.config.model.AcmConfig;
import com.armedia.acm.services.config.model.JsonConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Created by bojan.milenkoski on 12.9.2017
 */
public class ConfigService
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private String lookupsFileLocation;
    private List<AcmConfig> configList;

    public List<Map<String, String>> getInfo()
    {
        List<Map<String, String>> retval = new ArrayList<>();

        if (configList != null)
        {
            // With Java 8
            retval = configList.stream().map(acmConfig -> createMap(acmConfig)).filter(element -> element != null)
                    .collect(Collectors.toList());
        }

        return retval;
    }

    private Map<String, String> createMap(AcmConfig acmConfig)
    {
        Map<String, String> retval = null;

        if (acmConfig != null)
        {
            retval = new HashMap<>();
            retval.put("name", acmConfig.getConfigName());
            retval.put("description", acmConfig.getConfigDescription());
        }

        return retval;
    }

    public String getConfigAsJson(String name)
    {
        String rc = "{}";
        try
        {
            rc = configList.stream().filter(x -> x.getConfigName().equals(name)).findFirst().get().getConfigAsJson();
        }
        catch (NoSuchElementException e)
        {
            log.error(e.getMessage());
        }
        return rc;
    }

    public String getLookupsAsJson()
    {
        return getConfigAsJson("lookups");
    }

    public void saveLookups(String updatedLookupsAsJson) throws JSONException, IOException
    {
        Files.write(Paths.get(lookupsFileLocation), new JSONObject(updatedLookupsAsJson).toString(2).getBytes());

        // replace the lookups value in configList
        configList.stream().filter(config -> config.getConfigName().equals("lookups"))
                .forEach(config -> ((JsonConfig) config).setJson(updatedLookupsAsJson));
    }

    public List<AcmConfig> getConfigList()
    {
        return configList;
    }

    public void setConfigList(List<AcmConfig> configList)
    {
        this.configList = configList;
    }

    public String getLookupsFileLocation()
    {
        return lookupsFileLocation;
    }

    public void setLookupsFileLocation(String lookupsFileLocation)
    {
        this.lookupsFileLocation = lookupsFileLocation;
    }
}
