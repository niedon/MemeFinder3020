package com.bcadaval.memefinder3020.modelo.beans.temp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.bcadaval.memefinder3020.vista.HBoxEtiqueta;

import javafx.concurrent.Task;

public class ImagenTemp {

	private File imagen;
	private String nombre;
	private String categoria;
	private List<HBoxEtiqueta> etiquetas = new ArrayList<HBoxEtiqueta>();
	private Task<List<Integer>> coincidencias;
	
	public File getImagen() {
		return imagen;
	}
	public void setImagen(File imagen) {
		this.imagen = imagen;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public List<HBoxEtiqueta> getEtiquetas() {
		return etiquetas;
	}
	public void setEtiquetas(List<HBoxEtiqueta> etiquetas) {
		this.etiquetas = etiquetas;
	}
	public Task<List<Integer>> getCoincidencias() {
		return coincidencias;
	}
	public void setCoincidencias(Task<List<Integer>> coincidencias) {
		this.coincidencias = coincidencias;
	}
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	
	
	
}
