package com.bcadaval.memefinder3020.excepciones;

@SuppressWarnings("serial")
public class MemeFinderException extends Exception {

	private static final String mensajeDefecto = "Ha ocurrido un error en la aplicación";
	private String mensaje;
	
	public MemeFinderException() {
		super();
		mensaje = mensajeDefecto;
	}
	
	public MemeFinderException(String message, Throwable cause) {
		super(message, cause);
		mensaje = message;
	}
	
	public MemeFinderException(String message) {
		this(message, null);
	}
	
	public MemeFinderException(Throwable cause) {
		this(null, cause);
	}
	
	public String getMensaje() {
		return mensaje;
	}
	
}
