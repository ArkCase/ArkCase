package com.armedia.acm.services.users.dao.exception;

public class UserRemoteActionException extends RuntimeException
{
    public UserRemoteActionException(String message)
    {
        super(message);
    }

    public UserRemoteActionException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
