package com.armedia.acm.core.exceptions;

/**
 * Created by armdev on 6/12/14.
 */
public class AcmUserActionFailedException extends Exception
{
    private String actionName;
    private String objectType;
    private Long objectId;

    public AcmUserActionFailedException(String actionName, String objectType, Long objectId, String message, Throwable cause)
    {
        super(message, cause);
        this.actionName = actionName;
        this.objectType = objectType;
        this.objectId = objectId;
    }

    public String getActionName()
    {
        return actionName;
    }

    public void setActionName(String actionName)
    {
        this.actionName = actionName;
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
        if ( getActionName() != null && getObjectId() != null && getObjectType() != null )
        {
            message += "Could not " + getActionName() + " " + getObjectType() + " with ID = " + getObjectId() + ".\n";
        }
        if ( super.getMessage() != null )
        {
            message += "Server encountered exception: " + super.getMessage() + "\n";
        }

        message += "Exception type was: '" + getClass().getName() + "'.";

        return message;
    }
}
