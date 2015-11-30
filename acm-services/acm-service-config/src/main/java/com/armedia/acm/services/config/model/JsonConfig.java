package com.armedia.acm.services.config.model;

import java.io.Serializable;

/**
 * This class implement AcmConfig interface.
 * It is used for reading json files under .acm/lookups folder
 *
 * Created by Bojan Mickoski on 26-Nov-15.
 */
public class JsonConfig implements AcmConfig, Serializable
{
    private String configName;
    private String configDescription;
    private String json;


    @Override
    public String getConfigAsJson() {
        return getJson();
    }

    @Override
    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    @Override
    public String getConfigDescription() {
        return configDescription;
    }

    public void setConfigDescription(String configDescription) {
        this.configDescription = configDescription;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
