/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.cmis.exception;

public class CMISConnectorException extends RuntimeException
{

    private static final long serialVersionUID = -2284974690732034438L;

    public CMISConnectorException(Throwable cause)
    {
        super(cause);
    }
}