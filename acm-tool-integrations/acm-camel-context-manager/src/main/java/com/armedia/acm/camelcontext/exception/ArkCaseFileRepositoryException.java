package com.armedia.acm.camelcontext.exception;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Oct, 2019
 */
public class ArkCaseFileRepositoryException extends Exception
{
    public ArkCaseFileRepositoryException(String message)
    {
        super(message);
    }

    public ArkCaseFileRepositoryException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ArkCaseFileRepositoryException(Throwable cause)
    {
        super(cause);
    }

    protected ArkCaseFileRepositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
