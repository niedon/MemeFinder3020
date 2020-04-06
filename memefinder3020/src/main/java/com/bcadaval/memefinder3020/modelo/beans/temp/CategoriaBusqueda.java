package com.bcadaval.memefinder3020.modelo.beans.temp;

import com.bcadaval.memefinder3020.utils.enumerados.Comparador;

public class CategoriaBusqueda {

	private String nombre;
	private boolean conNum;
	private Comparador comparador;
	private int num;
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public boolean isConNum() {
		return conNum;
	}
	public void setConNum(boolean conNum) {
		this.conNum = conNum;
	}
	public Comparador getComparador() {
		return comparador;
	}
	public void setComparador(Comparador operador) {
		this.comparador = operador;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	
}
