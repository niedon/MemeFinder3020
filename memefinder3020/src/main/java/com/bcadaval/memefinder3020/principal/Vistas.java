package com.bcadaval.memefinder3020.principal;

import com.bcadaval.memefinder3020.controlador.AnadirImagenControlador;
import com.bcadaval.memefinder3020.controlador.CoincidenciasControlador;
import com.bcadaval.memefinder3020.controlador.InicioControlador;

public enum Vistas {
	
	INICIO("inicio", InicioControlador.class),
	ANADIR_IMAGEN("anadirimagen", AnadirImagenControlador.class),
	COINCIDENCIAS("coincidencias", CoincidenciasControlador.class);
	
	String nombre;
	Class<? extends Controlador> claseControlador;
	
	Vistas(String nombre, Class<? extends Controlador> claseControlador) {
		this.nombre = nombre;
		this.claseControlador = claseControlador;
	}

	public String getNombre() {
		return nombre;
	}
	
	public Class<? extends Controlador> getClaseControlador(){
		return claseControlador;
	}

}
