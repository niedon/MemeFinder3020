package com.bcadaval.memefinder3020.controlador;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.principal.Controlador;
import com.bcadaval.memefinder3020.principal.Vistas;
import com.bcadaval.memefinder3020.utils.Constantes;
import com.bcadaval.memefinder3020.utils.IOUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Controller
public class InicioControlador extends Controlador {
	
	static final String DATOS_TF_BUSQUEDA = "valorTfBusqueda";
	
	private List<Imagen> ultimasImagenes;
	
	@FXML private TextField tfBusqueda;
	
	@FXML private Button btBuscar;
	@FXML private Button btAnadirImagen;
	@FXML private Button btCategorias;
	@FXML private Button btEtiquetas;
	@FXML private Button btAjustes;
	
	@FXML private ImageView ivUltimas1;
	@FXML private ImageView ivUltimas2;
	@FXML private ImageView ivUltimas3;
	@FXML private ImageView ivUltimas4;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	@Override
	public void initComponentes() {
		setGraficos(btBuscar, String.format(Constantes.RUTA_SVG, Constantes.SVG_LUPA));
		setGraficos(btAnadirImagen, String.format(Constantes.RUTA_SVG, Constantes.SVG_PLUS));
		setGraficos(btCategorias, String.format(Constantes.RUTA_SVG, Constantes.SVG_CATEGORIA));
		setGraficos(btEtiquetas, String.format(Constantes.RUTA_SVG, Constantes.SVG_TAG));
		setGraficos(btAjustes, String.format(Constantes.RUTA_SVG, Constantes.SVG_AJUSTES));
	}

	@Override
	public void initVisionado() {
		
		ultimasImagenes = servicioImagen.getUltimas(4);
		
		if(ultimasImagenes.size()>0) {
			ivUltimas1.setImage(new Image(IOUtils.getURLDeImagen(ultimasImagenes.get(0))));
			setFit(ivUltimas1);
		}
		if(ultimasImagenes.size()>1) {
			ivUltimas2.setImage(new Image(IOUtils.getURLDeImagen(ultimasImagenes.get(1))));
			setFit(ivUltimas2);		
		}
		if(ultimasImagenes.size()>2) {
			ivUltimas3.setImage(new Image(IOUtils.getURLDeImagen(ultimasImagenes.get(2))));
			setFit(ivUltimas3);
		}
		if(ultimasImagenes.size()>3) {
			ivUltimas4.setImage(new Image(IOUtils.getURLDeImagen(ultimasImagenes.get(3))));
			setFit(ivUltimas4);
		}
		
	}

	@Override
	public void initFoco() {
		tfBusqueda.requestFocus();
	}

	//----------------
	
	@FXML
	private void tfBusqueda_keyPressed(KeyEvent key) {
		if(key.getCode()==KeyCode.ENTER) {
			btBuscar_click(null);
		}
	}
	
	@FXML
	private void btBuscar_click(ActionEvent event) {
		datos.put(DATOS_TF_BUSQUEDA, tfBusqueda.getText());
		gestorDeVentanas.cambiarEscena(Vistas.RESULTADOS);
	}
	

	
	@FXML
	private void btAnadirImagen_click(ActionEvent event) {
		gestorDeVentanas.cambiarEscena(Vistas.ANADIR_IMAGEN);
	}
	
	@FXML
	private void btCategorias_click(ActionEvent event) {
		
	}
	
	@FXML
	private void btEtiquetas_click(ActionEvent event) {
		
	}
	
	@FXML
	private void btAjustes_click(ActionEvent event) {
		
	}

}
