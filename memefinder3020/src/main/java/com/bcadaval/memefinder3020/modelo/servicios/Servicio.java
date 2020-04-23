package com.bcadaval.memefinder3020.modelo.servicios;

import java.io.File;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.utils.Constantes;

public abstract class Servicio {

	private static final Logger log = LogManager.getLogger(Servicio.class);
	
	protected static String validarNombre(String nombre) throws ConstraintViolationException {
		
		log.debug(".validarNombre() - Iniciando validación del nombre");
		
		if(nombre==null) {
			log.error(".validarNombre() - Nombre nulo");
			throw new ConstraintViolationException("Nombre nulo");
		}
		
		nombre = nombre.replaceAll("_", " ");
		nombre = nombre.trim();
		
		if(nombre.isEmpty()) {
			log.error(".validarNombre() - Nombre inválido (vacío tras trimear)");
			throw new ConstraintViolationException("Nombre inválido");
		}
		
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
		
		String nombreFinal = buff.toString().toUpperCase();
		log.debug(".validarNombre() - Finalizada validación del nombre: " + nombreFinal);
		return nombreFinal;
		
	}
	
	protected static void validarArchivoImagen(File imagen) throws ConstraintViolationException {
		
		log.debug(".validarArchivoImagen() - Iniciando validación del File de imagen");
		
		if(imagen == null) {
			log.error(".validarArchivoImagen() - File nula");
			throw new ConstraintViolationException("La imagen es nula");
		}else if(!imagen.exists()) {
			log.error(".validarArchivoImagen() - File no existe");
			throw new ConstraintViolationException("La imagen no existe");
		}else if(imagen.isDirectory()) {
			log.error(".validarArchivoImagen() - File es una carpeta");
			throw new ConstraintViolationException("El archivo es un directorio");
		}else if( ! imagen.getName().contains(".") || imagen.getName().charAt(imagen.getName().length()-1)=='.') {
			log.error(".validarArchivoImagen() - File sin extensión");
			throw new ConstraintViolationException("El archivo no tiene extensión");
		}else if( ! Arrays.asList(Constantes.FORMATOS_PERMITIDOS).contains(imagen.getName().substring(imagen.getName().indexOf('.')+1).toUpperCase())){
			log.error(".validarArchivoImagen() - File con formato no permitido");
			throw new ConstraintViolationException("El archivo tiene un formato no permitido");
		}
		
		log.debug(".validarArchivoImagen() - Finalizada validación del File de imagen: " + imagen.getAbsolutePath());
		
	}
	
}
