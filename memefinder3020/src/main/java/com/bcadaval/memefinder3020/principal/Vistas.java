package com.bcadaval.memefinder3020.principal;

import com.bcadaval.memefinder3020.controlador.AnadirImagenControlador;
import com.bcadaval.memefinder3020.controlador.CoincidenciasControlador;
import com.bcadaval.memefinder3020.controlador.InicioControlador;
import com.bcadaval.memefinder3020.controlador.ResultadoIndividualControlador;
import com.bcadaval.memefinder3020.controlador.ResultadosControlador;
public enum Vistas {
	
	INICIO("inicio", InicioControlador.class),
	ANADIR_IMAGEN("anadirimagen", AnadirImagenControlador.class),
	COINCIDENCIAS("coincidencias", CoincidenciasControlador.class),
	RESULTADOS("resultados", ResultadosControlador.class),
	RESULTADOINDIVIDUAL("resultadoIndividual",ResultadoIndividualControlador.class);
	
	private String nombre;
	private Class<? extends Controlador> claseControlador;
	
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
