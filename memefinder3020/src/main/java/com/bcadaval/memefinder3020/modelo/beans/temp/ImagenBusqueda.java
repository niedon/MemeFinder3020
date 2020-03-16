package com.bcadaval.memefinder3020.modelo.beans.temp;

import java.time.LocalDateTime;
import java.util.List;

import com.bcadaval.memefinder3020.modelo.beans.Categoria;
import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;

public class ImagenBusqueda {
	
	private String nombre;
	private boolean buscarSinCategoria;
	private Categoria categoria;
	private LocalDateTime fechaDespues;
	private LocalDateTime fechaAntes;
	private List<Etiqueta> etiquetas;
	private boolean buscarSinEtiquetas;
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public boolean isBuscarSinCategoria() {
		return buscarSinCategoria;
	}
	public void setBuscarSinCategoria(boolean buscarSinCategoria) {
		this.buscarSinCategoria = buscarSinCategoria;
	}
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	public LocalDateTime getFechaDespues() {
		return fechaDespues;
	}
	public void setFechaDespues(LocalDateTime fechaDespues) {
		this.fechaDespues = fechaDespues;
	}
	public LocalDateTime getFechaAntes() {
		return fechaAntes;
	}
	public void setFechaAntes(LocalDateTime fechaAntes) {
		this.fechaAntes = fechaAntes;
	}
	public List<Etiqueta> getEtiquetas() {
		return etiquetas;
	}
	public void setEtiquetas(List<Etiqueta> etiquetas) {
		this.etiquetas = etiquetas;
	}
	public boolean isBuscarSinEtiquetas() {
		return buscarSinEtiquetas;
	}
	public void setBuscarSinEtiquetas(boolean buscarSinEtiquetas) {
		this.buscarSinEtiquetas = buscarSinEtiquetas;
	}

}
