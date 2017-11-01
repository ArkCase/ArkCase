package com.armedia.acm.objectdiff.model;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public abstract class AcmDiff
{
    private ObjectConverter objectConverter;

    AcmDiff(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public abstract AcmChange getChangesAsTree();

    public abstract List<AcmChange> getChangesAsList();

    public String getChangesAsTreeJson() throws JsonProcessingException
    {
        return objectConverter.getIndentedJsonMarshaller().marshal(getChangesAsTree());
    }

    public String getChangesAsListJson() throws JsonProcessingException
    {
        return objectConverter.getIndentedJsonMarshaller().marshal(getChangesAsList());
    }
}
