package com.bcadaval.memefinder3020.principal;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bcadaval.memefinder3020.modelo.servicios.ServicioEtiqueta;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioImagen;

import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;

public abstract class Controlador implements Initializable{
	
	protected static final Map<String,Object> datos = new HashMap<String,Object>();
	protected static final String CLASE_ORIGEN = "claseOrigen";
	
	@Autowired protected GestorDeVentanas gestorDeVentanas;
	
	@Autowired protected ServicioImagen servicioImagen;
	@Autowired protected ServicioEtiqueta servicioEtiqueta;
	
	private Stage stage;
	private Parent vista;
	
	public abstract void initComponentes();
	public abstract void initVisionado();
	public abstract void initFoco();

	public Parent getVista() {
		return vista;
	}

	void setVista(Parent vista) {
		this.vista = vista;
	}
	protected Stage getStage() {
		return stage;
	}
	void setStage(Stage stage) {
		this.stage = stage;
	}
	
	void limpiarMapa() {
		datos.clear();
	}
	
	void anadirClaseAMapa(Class c) {
		datos.put(CLASE_ORIGEN,c);
	}
	
	//--------------Concurrencia--------------
	
	protected void setCargando(Task t) {
		
		//TODO
		
	}

}
