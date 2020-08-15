package com.bcadaval.memefinder3020.modelo.beans.temp;

import java.time.LocalDateTime;
import java.util.List;

import com.bcadaval.memefinder3020.modelo.beans.Categoria;

public class ImagenBusquedaExportar {

	private boolean todasMenos;
	private List<String> etiquetas;
	private Categoria categoria;
	private LocalDateTime despuesDe;
	private LocalDateTime antesDe;
	public boolean isTodasMenos() {
		return todasMenos;
	}
	public void setTodasMenos(boolean todasMenos) {
		this.todasMenos = todasMenos;
	}
	public List<String> getEtiquetas() {
		return etiquetas;
	}
	public void setEtiquetas(List<String> etiquetas) {
		this.etiquetas = etiquetas;
	}
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	public LocalDateTime getDespuesDe() {
		return despuesDe;
	}
	public void setDespuesDe(LocalDateTime despuesDe) {
		this.despuesDe = despuesDe;
	}
	public LocalDateTime getAntesDe() {
		return antesDe;
	}
	public void setAntesDe(LocalDateTime antesDe) {
		this.antesDe = antesDe;
	}
	
}
