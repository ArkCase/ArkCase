package com.armedia.acm.plugins.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.beans.factory.annotation.Value;

@JsonSerialize(as= EcmFileDeDuplicationConfig.class)
public class EcmFileDeDuplicationConfig
{
    @JsonProperty("enableDeDuplication")
    @Value("${enableDeDuplication}")
    private Boolean enableDeDuplication;

    public Boolean getEnableDeDuplication() {
        return enableDeDuplication;
    }

    public void setEnableDeDuplication(Boolean enableDeDuplication) {
        this.enableDeDuplication = enableDeDuplication;
    }
}
