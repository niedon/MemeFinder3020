package com.bcadaval.memefinder3020.excepciones;

@SuppressWarnings("serial")
public class NotFoundException extends MemeFinderException {

	public NotFoundException() {
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(Throwable cause) {
		super(cause);
	}

}
