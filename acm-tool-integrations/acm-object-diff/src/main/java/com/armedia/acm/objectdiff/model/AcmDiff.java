package com.armedia.acm.objectdiff.model;

import com.armedia.acm.services.search.service.ObjectMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.List;

public abstract class AcmDiff
{
    private ObjectMapper mapper;

    AcmDiff()
    {
        ObjectMapperFactory factory = new ObjectMapperFactory();
        mapper = factory.createObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public abstract AcmChange getChangesAsTree();

    public abstract List<AcmChange> getChangesAsList();

    public String getChangesAsTreeJson() throws JsonProcessingException
    {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(getChangesAsTree());
    }

    public String getChangesAsListJson() throws JsonProcessingException
    {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(getChangesAsList());
    }
}
