package com.armedia.acm.core.exceptions;

/**
 * Created by armdev on 6/12/14.
 */
public class AcmObjectNotFoundException extends Exception
{
    private String objectType;
    private Long objectId;


    public AcmObjectNotFoundException(String objectType, Long objectId, String message, Throwable cause)
    {
        super(message, cause);

        this.objectId = objectId;
        this.objectType = objectType;
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

    @Override
    public String getMessage()
    {
        String message = "";
        if ( getObjectId() != null && getObjectType() != null )
        {
            message += "Could not retrieve " + getObjectType() + " with ID = " + getObjectId() + ".\n";
        }
        if ( super.getMessage() != null )
        {
            message += "Server encountered exception: " + super.getMessage() + "\n";
        }

        message += "Exception type was: '" + getClass().getName() + "'.";

        return message;
    }
}
