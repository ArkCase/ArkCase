package com.armedia.acm.core.exceptions;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 13, 2017
 *
 */
public class AcmUpdateObjectFailedException extends Exception
{
    private static final long serialVersionUID = 2066799651380572308L;

    private String objectType;

    private Long objectId;

    public AcmUpdateObjectFailedException(String objectType, Long objectId, String message, Throwable cause)
    {
        super(message, cause);
        this.objectType = objectType;
        this.objectId = objectId;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    /**
     * @return the objectId
     */
    public Long getObjectId()
    {
        return objectId;
    }

    /**
     * @param objectId
     *            the objectId to set
     */
    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

    @Override
    public String getMessage()
    {
        String message = "";
        if (getObjectType() != null)
        {
            message += "Could not update " + getObjectType() + ".\n";
        }
        if (super.getMessage() != null)
        {
            message += "Server encountered exception: " + super.getMessage() + "\n";
        }

        message += "Exception type was: '" + getClass().getName() + "'.";

        return message;
    }
}
