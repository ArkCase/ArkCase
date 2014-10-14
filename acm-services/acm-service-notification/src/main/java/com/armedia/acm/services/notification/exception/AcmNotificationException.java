package com.armedia.acm.services.notification.exception;

public class AcmNotificationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1240350782124505058L;

	public AcmNotificationException() {
	}

	public AcmNotificationException(String message) {
		super(message);
	}

	public AcmNotificationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AcmNotificationException(Throwable cause) {
		super(cause);
	}

	public AcmNotificationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
