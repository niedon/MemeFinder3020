package com.bcadaval.memefinder3020.modelo.beans;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="IMAGEN")
public class Imagen {
	
	@Override
	public String toString() {
		return id + "-" + nombre + " - " + categoria + " (" + etiquetas+")";
	}

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="NOMBRE",
			nullable = false)
	private String nombre;
	
	@Column(name="EXTENSION",
			nullable=false)
	private String extension;
	
	@Column(name="CATEGORIA")
	private String categoria;

	@ManyToMany(cascade = CascadeType.ALL,
			fetch = FetchType.EAGER)
	@JoinTable(
			name="IMAGEN_ETIQUETA",
			joinColumns = {@JoinColumn(name="IDIMAGEN")},
			inverseJoinColumns = {@JoinColumn(name="IDETIQUETA")}
			)
	private Set<Etiqueta> etiquetas = new HashSet<Etiqueta>();

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

	public Set<Etiqueta> getEtiquetas() {
		return etiquetas;
	}

	public void setEtiquetas(Set<Etiqueta> etiquetas) {
		this.etiquetas = etiquetas;
	}
	
}
