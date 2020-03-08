package com.bcadaval.memefinder3020.controlador;

import org.springframework.beans.factory.annotation.Autowired;

import com.bcadaval.memefinder3020.modelo.servicios.ServicioEtiqueta;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioImagen;
import com.bcadaval.memefinder3020.principal.GestorDeVentanas;

import javafx.fxml.Initializable;
import javafx.scene.Parent;

public abstract class Controlador implements Initializable{
	
	@Autowired protected GestorDeVentanas gestorDeVentanas;
	
	@Autowired protected ServicioImagen servicioImagen;
	@Autowired protected ServicioEtiqueta servicioEtiqueta;
	
	private Parent vista;
	
	public abstract void initComponentes();
	public abstract void initVisionado();
	public abstract void initFoco();

	public Parent getVista() {
		return vista;
	}

	public void setVista(Parent vista) {
		this.vista = vista;
	}

}
