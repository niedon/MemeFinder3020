package com.bcadaval.memefinder3020.modelo.beans;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;

@Entity
@Table(name="CATEGORIA")
public class Categoria {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="NOMBRE",
			nullable = false,
			length = 32)
	private String nombre;
	
	@OneToMany(mappedBy = "categoria")
	private Set<Imagen> imagenes;

	@PreRemove
	public void nullImagenes() {
		imagenes.forEach(el -> el.setCategoria(null));
	}
	
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
