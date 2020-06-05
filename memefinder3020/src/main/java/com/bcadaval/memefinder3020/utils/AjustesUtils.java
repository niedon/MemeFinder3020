package com.bcadaval.memefinder3020.utils;

import java.io.File;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcadaval.memefinder3020.modelo.beans.xml.Ajustes;

@Component
public class AjustesUtils {
	
	@Autowired private RutasUtils rutasUtils;
	@Autowired private JAXBContext contextoXml;
	
	private static final Logger log = LogManager.getLogger(AjustesUtils.class);

	private final Ajustes ajustes;
	
	public AjustesUtils(RutasUtils rutasUtils, JAXBContext contextoXml) {
		
		this.rutasUtils = rutasUtils;
		this.contextoXml = contextoXml;
		
		File f = rutasUtils.ARCHIVO_AJUSTES;
		
		if(f.exists()) {
			
			log.debug(".AjustesUtils() - Cargando xml de ajustes");
			Ajustes temp;
			try {
				 temp = (Ajustes) contextoXml.createUnmarshaller().unmarshal(rutasUtils.ARCHIVO_AJUSTES);
			} catch (JAXBException e) {
				log.error(".AjustesUtils() - Error haciendo unmarshall a archivo de ajustes", e);
				temp = getAjustesDefault();
			}
			ajustes = temp;
			
		}else {
			log.debug(".AjustesUtils() - No se ha encontrado el archivo de ajustes");
			ajustes = getAjustesDefault();
		}
		comprobarAjustes();
		marshallear();
		
	}
	
	private Ajustes getAjustesDefault() {
		Ajustes ajustes = new Ajustes();
		ajustes.setLocale(new Locale("es"));
		return ajustes;
	}
	
	private void comprobarAjustes() {
		if(ajustes.getLocale()==null) {
			ajustes.setLocale(new Locale("es"));
		}
	}
	
	private void marshallear() {
		Marshaller marshaller;
		try {
			marshaller = contextoXml.createMarshaller();
		} catch (JAXBException e) {
			log.error(".marshallear() - No se ha podido crear el marshaller", e);
			return;
		}
		try {
			marshaller.marshal(ajustes, rutasUtils.ARCHIVO_AJUSTES);
		} catch (JAXBException e) {
			log.error(".marshallear() - No se ha podido convertir el bean a xml", e);
		}
	}
	
	//--------
	
	public Locale getLocale() {
		return ajustes.getLocale();
	}
	
	public void setLocale(Locale locale) {
		ajustes.setLocale(locale);
		marshallear();
	}
	
}
