package com.bcadaval.memefinder3020.principal;

public enum Vistas {
	
	INICIO("inicio"),
	ANADIR_IMAGEN("anadirimagen");
	
	String nombre;
	
	Vistas(String nombre) {
		this.nombre = nombre;
	}
	
	public String getNombre() {
		return nombre;
	}

}
