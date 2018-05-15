package com.armedia.acm.services.signature.exception;

/*-
 * #%L
 * ACM Service: Electronic Signature
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

public class AcmSignatureException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = 1240350782124505058L;

    public AcmSignatureException()
    {
    }

    public AcmSignatureException(String message)
    {
        super(message);
    }

    public AcmSignatureException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmSignatureException(Throwable cause)
    {
        super(cause);
    }

    public AcmSignatureException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
