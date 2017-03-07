package com.armedia.acm.core.exceptions;


import java.util.HashMap;
import java.util.Map;

public class AcmAppErrorJsonMsg extends Exception
{
    private String objectType;
    private String field;
    private Map<String, Object> extra;

    public AcmAppErrorJsonMsg(String message, String objectType, String field, Throwable cause)
    {
        super(message, cause);
        this.objectType = objectType;
        this.field = field;
        this.extra = new HashMap<>();
    }

    public String getObjectType()
    {
        return objectType;
    }

    public String getField()
    {
        return field;
    }

    public Map<String, Object> getExtra()
    {
        return extra;
    }

    public void putExtra(String key, Object value)
    {
        extra.put(key, value);
    }
}
