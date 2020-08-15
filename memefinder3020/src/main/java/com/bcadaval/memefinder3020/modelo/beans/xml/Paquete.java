package com.bcadaval.memefinder3020.modelo.beans.xml;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "paquete")
@XmlAccessorType(XmlAccessType.FIELD)
public class Paquete {

	@XmlElement(name = "version")
	private String version;
	
	@XmlElement(name = "nombre")
	private String nombre;
	
	@XmlElementWrapper(name = "imagenes")
	@XmlElement(name = "imagen")
	private ArrayList<ImagenXml> imagenes;

	public Paquete(String version, String nombre, ArrayList<ImagenXml> imagenes) {
		super();
		this.version = version;
		this.nombre = nombre;
		this.imagenes = imagenes;
	}
	
	public Paquete() {
		
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public ArrayList<ImagenXml> getImagenes() {
		return imagenes;
	}

	public void setImagenes(ArrayList<ImagenXml> imagenes) {
		this.imagenes = imagenes;
	}
	
}
