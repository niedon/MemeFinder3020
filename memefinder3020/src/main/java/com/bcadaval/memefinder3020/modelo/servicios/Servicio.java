package com.bcadaval.memefinder3020.modelo.servicios;

import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;

public abstract class Servicio {

	
	protected String validarNombre(String nombre) throws ConstraintViolationException {
		
		if(nombre==null) {
			throw new ConstraintViolationException("Nombre nulo");
		}
		
		nombre = nombre.replaceAll("_", " ");
		nombre = nombre.trim();
		
		if(nombre.isEmpty()) throw new ConstraintViolationException("Nombre inválido");
		
		String[] nombreTemp = nombre.split(" ");
		
		StringBuffer buff = new StringBuffer();
		
		for(int i=0;i<nombreTemp.length;i++) {
			if(!nombreTemp[i].isEmpty()) {
				buff.append(nombreTemp[i]);
				if(i!=(nombreTemp.length-1)) {
					buff.append('_');
				}
			}
		}
		
		return buff.toString().toUpperCase();
	}
	
}
