package com.armedia.acm.camelcontext.exception;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Oct, 2019
 */
public class ArkCaseCamelException extends Exception
{
    public ArkCaseCamelException(String message)
    {
        super(message);
    }

    public ArkCaseCamelException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ArkCaseCamelException(Throwable cause)
    {
        super(cause);
    }

    protected ArkCaseCamelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
