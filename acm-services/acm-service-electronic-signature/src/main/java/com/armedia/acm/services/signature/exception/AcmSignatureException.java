package com.armedia.acm.services.signature.exception;

public class AcmSignatureException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1240350782124505058L;

	public AcmSignatureException() {
	}

	public AcmSignatureException(String message) {
		super(message);
	}

	public AcmSignatureException(String message, Throwable cause) {
		super(message, cause);
	}

	public AcmSignatureException(Throwable cause) {
		super(cause);
	}

	public AcmSignatureException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
