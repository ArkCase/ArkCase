package com.armedia.acm.core.exceptions;

/**
 * Created by armdev on 6/12/14.
 */
public class AcmListObjectsFailedException extends Exception
{
    private String objectType;

    public AcmListObjectsFailedException(String objectType, String message, Throwable cause)
    {
        super(message, cause);
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

    @Override
    public String getMessage()
    {
        String message = "";
        if ( getObjectType() != null )
        {
            message += "Could not list " + getObjectType() + "s.\n";
        }
        if ( super.getMessage() != null )
        {
            message += "Server encountered exception: " + super.getMessage() + "\n";
        }

        message += "Exception type was: '" + getClass().getName() + "'.";

        return message;
    }
}
