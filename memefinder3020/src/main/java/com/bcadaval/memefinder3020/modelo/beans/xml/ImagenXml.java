package com.bcadaval.memefinder3020.modelo.beans.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.bcadaval.memefinder3020.utils.adapter.DateStringXmlAdapter;

@XmlRootElement(name="imagen")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImagenXml {

	@XmlAttribute(name = "num")
	private Integer num;
	
	@XmlElement(name = "fecha")
	@XmlJavaTypeAdapter(DateStringXmlAdapter.class)
	private Date fecha;
	@XmlElement(name = "nombre")
	private String nombre;
	@XmlElement(name = "nombrearchivo")
	private String nombrearchivo;
	@XmlElement(name = "extension")
	private String extension;
	@XmlElement(name = "categoria")
	private String categoria;
	@XmlElement(name = "etiquetas")
	private String etiquetas;
	
	public ImagenXml(Integer num, Date fecha, String nombre, String nombrearchivo, String extension,
			String categoria, String etiquetas) {
		super();
		this.num = num;
		this.fecha = fecha;
		this.nombre = nombre;
		this.nombrearchivo = nombrearchivo;
		this.extension = extension;
		this.categoria = categoria;
		this.etiquetas = etiquetas;
	}
	
	public ImagenXml() {
		
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombrearchivo() {
		return nombrearchivo;
	}

	public void setNombrearchivo(String nombrearchivo) {
		this.nombrearchivo = nombrearchivo;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getEtiquetas() {
		return etiquetas;
	}

	public void setEtiquetas(String etiquetas) {
		this.etiquetas = etiquetas;
	}
	
}
