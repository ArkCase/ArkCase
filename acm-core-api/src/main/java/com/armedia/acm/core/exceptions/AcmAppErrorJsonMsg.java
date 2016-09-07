package com.armedia.acm.core.exceptions;


public class AcmAppErrorJsonMsg extends Exception
{
    private String objectType;
    private String field;

    public AcmAppErrorJsonMsg(String message, String objectType, String field, Throwable cause)
    {
        super(message, cause);
        this.objectType = objectType;
        this.field = field;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public String getField()
    {
        return field;
    }
}
