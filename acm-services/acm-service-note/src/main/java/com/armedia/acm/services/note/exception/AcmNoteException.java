package com.armedia.acm.services.note.exception;

public class AcmNoteException extends Exception {


	private static final long serialVersionUID = 1240350782124505058L;

	public AcmNoteException() {
	}

	public AcmNoteException(String message) {
		super(message);
	}

	public AcmNoteException(String message, Throwable cause) {
		super(message, cause);
	}

	public AcmNoteException(Throwable cause) {
		super(cause);
	}

	public AcmNoteException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
