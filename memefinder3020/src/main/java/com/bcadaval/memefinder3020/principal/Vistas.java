package com.bcadaval.memefinder3020.principal;

import com.bcadaval.memefinder3020.controlador.AjustesControlador;
import com.bcadaval.memefinder3020.controlador.AnadirImagenControlador;
import com.bcadaval.memefinder3020.controlador.CategoriasControlador;
import com.bcadaval.memefinder3020.controlador.CoincidenciasControlador;
import com.bcadaval.memefinder3020.controlador.EtiquetasControlador;
import com.bcadaval.memefinder3020.controlador.ExportarControlador;
import com.bcadaval.memefinder3020.controlador.InicioControlador;
import com.bcadaval.memefinder3020.controlador.ResultadoIndividualControlador;
import com.bcadaval.memefinder3020.controlador.ResultadosControlador;
import com.bcadaval.memefinder3020.controlador.VisorImagenControlador;

public enum Vistas {
	
	AJUSTES("ajustes", AjustesControlador.class, true),
	ANADIR_IMAGEN("anadirimagen", AnadirImagenControlador.class, false),
	CATEGORIAS("categorias", CategoriasControlador.class, true),
	COINCIDENCIAS("coincidencias", CoincidenciasControlador.class, false),
	ETIQUETAS("etiquetas", EtiquetasControlador.class, true),
	EXPORTAR("exportar", ExportarControlador.class, true),
	INICIO("inicio", InicioControlador.class, false),
	RESULTADOS("resultados", ResultadosControlador.class, false),
	RESULTADOINDIVIDUAL("resultadoIndividual", ResultadoIndividualControlador.class, false),
	VISORIMAGEN("visorImagen", VisorImagenControlador.class, true);
	
	private String nombre;
	private Class<? extends Controlador> claseControlador;
	private boolean modal;
	
	Vistas(String nombre, Class<? extends Controlador> claseControlador, boolean modal) {
		this.nombre = nombre;
		this.claseControlador = claseControlador;
		this.modal = modal;
	}

	public String getNombre() {
		return nombre;
	}
	
	public Class<? extends Controlador> getClaseControlador(){
		return claseControlador;
	}
	
	public boolean esModal() {
		return modal;
	}

}
