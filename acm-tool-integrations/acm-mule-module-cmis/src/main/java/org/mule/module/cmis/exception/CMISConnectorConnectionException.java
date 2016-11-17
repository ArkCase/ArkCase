/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.cmis.exception;

public class CMISConnectorConnectionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CMISConnectorConnectionException() {
		super();
	}

	public CMISConnectorConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public CMISConnectorConnectionException(String message) {
		super(message);
	}

	public CMISConnectorConnectionException(Throwable cause) {
		super(cause);
	}
}
