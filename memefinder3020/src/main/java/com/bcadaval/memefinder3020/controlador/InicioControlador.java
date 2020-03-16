package com.bcadaval.memefinder3020.controlador;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.bcadaval.memefinder3020.principal.Controlador;
import com.bcadaval.memefinder3020.principal.Vistas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

@Controller
public class InicioControlador extends Controlador {
	
	static final String DATOS_TF_BUSQUEDA = "valorTfBusqueda";
	
	@FXML private TextField tfBusqueda;
	@FXML private Button btBuscar;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void initComponentes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initVisionado() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initFoco() {
		tfBusqueda.requestFocus();
	}

	//----------------
	
	
	
	@FXML
	private void btBuscar_click(ActionEvent event) {
		datos.put(DATOS_TF_BUSQUEDA, tfBusqueda.getText());
		gestorDeVentanas.cambiarEscena(Vistas.RESULTADOS);
	}
	

	
	@FXML
	private void btAnadir_click(ActionEvent event) {
		gestorDeVentanas.cambiarEscena(Vistas.ANADIR_IMAGEN);
	}

}
