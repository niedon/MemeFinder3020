package com.bcadaval.memefinder3020.excepciones;

@SuppressWarnings("serial")
public class ConstraintViolationException extends MemeFinderException {

	public ConstraintViolationException() {
	}

	public ConstraintViolationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConstraintViolationException(String message) {
		super(message);
	}

	public ConstraintViolationException(Throwable cause) {
		super(cause);
	}

}
