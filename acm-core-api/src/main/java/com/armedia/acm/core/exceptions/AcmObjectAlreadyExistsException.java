package com.armedia.acm.core.exceptions;

public class AcmObjectAlreadyExistsException extends Exception
{
    private String objectType;
    private Long objectId;

    public AcmObjectAlreadyExistsException(String objectType, Long objectId, String message, Throwable cause)
    {
        super(message, cause);

        this.objectId = objectId;
        this.objectType = objectType;
    }

    public AcmObjectAlreadyExistsException(String message)
    {
        super(message);
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public Long getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

}
