package com.armedia.acm.objectdiff.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public abstract class AcmChange implements Serializable
{
    private String action;
    private String path;
    private ObjectMapper om;

    public AcmChange()
    {
        om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    @JsonIgnore
    public String getChangesAsJson() throws JsonProcessingException
    {
        return om.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}
