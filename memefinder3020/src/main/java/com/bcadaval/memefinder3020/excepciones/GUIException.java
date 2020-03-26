package com.bcadaval.memefinder3020.excepciones;

@SuppressWarnings("serial")
public class GUIException extends MemeFinderException {

	public GUIException() {
	}

	public GUIException(String message, Throwable cause) {
		super(message, cause);
	}

	public GUIException(String message) {
		super(message);
	}

	public GUIException(Throwable cause) {
		super(cause);
	}

}
