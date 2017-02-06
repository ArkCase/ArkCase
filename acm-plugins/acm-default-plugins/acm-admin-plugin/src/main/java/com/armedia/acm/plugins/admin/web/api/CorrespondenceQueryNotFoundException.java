package com.armedia.acm.plugins.admin.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 30, 2017
 *
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Error while retreiving correspondence query.")
public class CorrespondenceQueryNotFoundException extends RuntimeException
{

    private static final long serialVersionUID = -4225575694894026598L;

    public CorrespondenceQueryNotFoundException()
    {
    }

    public CorrespondenceQueryNotFoundException(Throwable t)
    {
        super(t);
    }

    /**
     * @param message
     */
    public CorrespondenceQueryNotFoundException(String message)
    {
        super(message);
    }

}