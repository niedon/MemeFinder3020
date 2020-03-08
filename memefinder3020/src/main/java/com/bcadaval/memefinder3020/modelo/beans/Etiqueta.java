package com.bcadaval.memefinder3020.modelo.beans;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="ETIQUETA")
public class Etiqueta {
	
	@Override
	public String toString() {
		return id + "-" + nombre + " (" + imagenes.size()+")";
	}

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="NOMBRE",
			nullable = false)
	private String nombre;
	
	@ManyToMany(mappedBy = "etiquetas",
			fetch = FetchType.EAGER)
	private Set<Imagen> imagenes = new HashSet<Imagen>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Set<Imagen> getImagenes() {
		return imagenes;
	}

	public void setImagenes(Set<Imagen> imagenes) {
		this.imagenes = imagenes;
	}
	
	

}
