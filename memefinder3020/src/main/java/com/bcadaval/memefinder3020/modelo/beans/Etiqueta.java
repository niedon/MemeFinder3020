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

import org.hibernate.annotations.Formula;

@Entity
@Table(name="ETIQUETA")
public class Etiqueta {

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="NOMBRE",
			nullable = false)
	private String nombre;
	
	@ManyToMany(mappedBy = "etiquetas",
			fetch = FetchType.LAZY)
	private Set<Imagen> imagenes = new HashSet<Imagen>();
	
	@Formula("(select count(*) from IMAGEN_ETIQUETA imget where imget.IDETIQUETA=id)")
	private Long countImagenes;

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

	public Long getCountImagenes() {
		return countImagenes;
	}

	public void setCountImagenes(Long countImagenes) {
		this.countImagenes = countImagenes;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Etiqueta))
			return false;
		Etiqueta other = (Etiqueta) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}
