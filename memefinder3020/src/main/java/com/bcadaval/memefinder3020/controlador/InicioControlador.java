package com.bcadaval.memefinder3020.controlador;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;

import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.principal.Controlador;
import com.bcadaval.memefinder3020.principal.Vistas;
import com.bcadaval.memefinder3020.utils.Constantes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Controller
public class InicioControlador extends Controlador {
	
	private static final Logger log = LogManager.getLogger(InicioControlador.class);
	
	static final String DATOS_TF_BUSQUEDA = "valorTfBusqueda";
	
	private static final int numUltimas = 4;
	private List<ImageView> ultimasIv;
	
	@FXML private TextField tfBusqueda;
	
	@FXML private Button btBuscar;
	@FXML private Button btAnadirImagen;
	@FXML private Button btCategorias;
	@FXML private Button btEtiquetas;
	@FXML private Button btAjustes;
	@FXML private Button btImportar;
	@FXML private Button btExportar;
	
	@FXML private ImageView ivUltimas1;
	@FXML private ImageView ivUltimas2;
	@FXML private ImageView ivUltimas3;
	@FXML private ImageView ivUltimas4;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		ultimasIv = new ArrayList<ImageView>(Arrays.asList(ivUltimas1, ivUltimas2, ivUltimas3, ivUltimas4));
		
		tfBusqueda.textProperty().addListener((obs, viejo, nuevo) -> {
			if(viejo.isEmpty() != nuevo.isEmpty()) {
				setGraficos(btBuscar, nuevo.isEmpty() ? Constantes.SVG_IMAGEN : Constantes.SVG_LUPA);
			}
		});
		
		setGraficos(btBuscar, Constantes.SVG_IMAGEN);
		setGraficos(btAnadirImagen, Constantes.SVG_PLUS);
		setGraficos(btCategorias, Constantes.SVG_CATEGORIA);
		setGraficos(btEtiquetas, Constantes.SVG_TAG);
		setGraficos(btAjustes, Constantes.SVG_AJUSTES);
		setGraficos(btImportar, Constantes.SVG_IMPORTAR);
		setGraficos(btExportar, Constantes.SVG_EXPORTAR);
		
	}

	@Override
	public void initVisionado() {
		
		log.debug(".initVisionado() - Iniciando visionado");
		
		switch (getVistaOrigen()) {
		case INICIO://Viene desde pantalla de carga
			//TODO comprobar errores
			break;

		case RESULTADOS:
		case ANADIR_IMAGEN:
		case AJUSTES:
		case CATEGORIAS:
		case ETIQUETAS:
		case EXPORTAR:
			break;
			
		default:
			log.error(".initVisionado() - Pantalla no contemplada: " + getVistaOrigen().toString());
			throw new UnsupportedOperationException("Pantalla no contemplada");
		}
		
		ultimasIv.forEach(el -> el.setImage(null));
		try {
			List<Imagen> ultimas = servicioImagen.getUltimas(numUltimas);
			for(int i=0; i<ultimas.size();i++) {
				ultimasIv.get(i).setImage(new Image(rutasUtils.getURLDeImagen(ultimas.get(i))));
			}
		} catch (ConstraintViolationException e) {
			log.error(".initVisionado() - Error cargando últimas imágenes", e);
			new Alert(AlertType.ERROR,String.format("No se han podido cargar las últimas imágenes: %s", e.getMensaje(), ButtonType.OK)).showAndWait();
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
		datos.put(DATOS_TF_BUSQUEDA, tfBusqueda.getText().trim());
		cambiarEscena(Vistas.RESULTADOS);
	}
	

	
	@FXML
	private void btAnadirImagen_click(ActionEvent event) {
		cambiarEscena(Vistas.ANADIR_IMAGEN);
	}
	
	@FXML
	private void btCategorias_click(ActionEvent event) {
		cambiarEscena(Vistas.CATEGORIAS);
	}
	
	@FXML
	private void btEtiquetas_click(ActionEvent event) {
		cambiarEscena(Vistas.ETIQUETAS);
	}
	
	@FXML
	private void btAjustes_click(ActionEvent event) {
		cambiarEscena(Vistas.AJUSTES);
	}
	
	@FXML
	private void btImportar_click(ActionEvent event) {
		
	}
	
	@FXML
	private void btExportar_click(ActionEvent event) {
		cambiarEscena(Vistas.EXPORTAR);
	}

}
