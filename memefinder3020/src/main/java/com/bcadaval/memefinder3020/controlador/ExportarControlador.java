package com.bcadaval.memefinder3020.controlador;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;

import com.bcadaval.memefinder3020.controlador.componentes.PaneEtiquetas;
import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.excepciones.MemeFinderException;
import com.bcadaval.memefinder3020.modelo.beans.Categoria;
import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenBusquedaExportar;
import com.bcadaval.memefinder3020.modelo.beans.xml.Paquete;
import com.bcadaval.memefinder3020.principal.Controlador;
import com.bcadaval.memefinder3020.utils.Constantes;
import com.bcadaval.memefinder3020.vista.HBoxEtiqueta;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

@Controller
public class ExportarControlador extends Controlador {

	private static final Logger log = LogManager.getLogger(ExportarControlador.class);
	
	private ObservableList<Imagen> listaImagenes;
	private ObservableList<Boolean> arrayExportar;
	private SimpleIntegerProperty marcador;
	
	private FileChooser fileChooser;
	
	
	private ToggleGroup tgRadio;
	@FXML private RadioButton rbTodasMenos;
	@FXML private RadioButton rbSolo;
	
	@FXML private TextField tfEtiquetas;
	@FXML private Button btAnadirEtiqueta;
	private PaneEtiquetas paneEtiquetas;
	@FXML private AnchorPane apEtiquetas;
	private ObservableList<Categoria> listaCategorias;
	@FXML private ChoiceBox<Categoria> cbCategorias;
	@FXML private Button btBorrarCategoria;
	@FXML private CheckBox cbDespuesDe;
	@FXML private DatePicker dpDespuesDe;
	@FXML private CheckBox cbAntesDe;
	@FXML private DatePicker dpAntesDe;
	
	@FXML private Button btLimpiar;
	@FXML private Button btBuscar;
	
	@FXML private Button btAnterior;
	@FXML private Label lbMarcador;
	@FXML private Button btSiguiente;
	
	@FXML private ImageView ivImagen;
	
	@FXML private Button btExportarNo;
	@FXML private Label lbExportarNo;
	
	@FXML private CheckBox cbConservarImagenes;
	@FXML private Button btExportar;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		listaImagenes = FXCollections.observableArrayList();
		arrayExportar = FXCollections.observableArrayList();
		listaCategorias = FXCollections.observableArrayList();
		marcador = new SimpleIntegerProperty(0);
		
		File f = new File(System.getProperty("user.home") + File.separator +  "Desktop");
		if(!f.exists()) {
			f = new File(System.getProperty("user.home"));
		}
		
		fileChooser = new FileChooser();
		fileChooser.setTitle("Exportar paquete");
		fileChooser.setInitialDirectory(f);
		
		tgRadio = new ToggleGroup();
		rbTodasMenos.setToggleGroup(tgRadio);
		rbSolo.setToggleGroup(tgRadio);
		
		tfEtiquetas.setOnKeyPressed(ev -> {
			if(ev.getCode() == KeyCode.ENTER) {
				btAnadirEtiqueta_click(null);
			}
		});
		
		paneEtiquetas = new PaneEtiquetas();
		//TODO evento
		apEtiquetas.getChildren().add(paneEtiquetas);
		AnchorPane.setTopAnchor(paneEtiquetas, 0d);
		AnchorPane.setRightAnchor(paneEtiquetas, 0d);
		AnchorPane.setBottomAnchor(paneEtiquetas, 0d);
		AnchorPane.setLeftAnchor(paneEtiquetas, 0d);
		
		btAnadirEtiqueta.disableProperty().bind(Bindings.createBooleanBinding(
				() -> tfEtiquetas.getText().trim().isEmpty(),
				tfEtiquetas.textProperty()));
		
		cbCategorias.setConverter(new StringConverter<Categoria>() {
			@Override
			public String toString(Categoria object) {
				return object==null ? "" : object.getNombre();
			}
			@Override
			public Categoria fromString(String string) {
				return null;
			}
		});
		cbCategorias.setItems(listaCategorias);
		
		btBorrarCategoria.disableProperty().bind(Bindings.isNull(cbCategorias.getSelectionModel().selectedItemProperty()));
		
		dpDespuesDe.disableProperty().bind(Bindings.not(cbDespuesDe.selectedProperty()));
		dpAntesDe.disableProperty().bind(Bindings.not(cbAntesDe.selectedProperty()));
		
		btAnterior.disableProperty().bind(Bindings.or(
				Bindings.isEmpty(listaImagenes),
				Bindings.lessThan(marcador, 1)));
		btSiguiente.disableProperty().bind(Bindings.or(
				Bindings.isEmpty(listaImagenes),
				Bindings.greaterThanOrEqual(marcador, Bindings.subtract(Bindings.size(listaImagenes), 1))));
		
		lbMarcador.textProperty().bind(Bindings.when(
				Bindings.isEmpty(listaImagenes))
				.then("0/0")
				.otherwise(Bindings.format("%d/%d", Bindings.add(1, marcador), Bindings.size(listaImagenes)))
				);
		
		ivImagen.imageProperty().bind(Bindings.createObjectBinding(() -> {
			
			if(listaImagenes.isEmpty()) {
				return null;
			}else {
				return new Image(rutasUtils.getURLDeImagen(listaImagenes.get(marcador.get())));
			}
			
		}, listaImagenes, marcador));
		
		btExportarNo.disableProperty().bind(Bindings.isEmpty(listaImagenes));
		btExportarNo.graphicProperty().bind(Bindings.createObjectBinding(() -> {
			
			if(listaImagenes.isEmpty()) {
				return null;
			}
			
			//TODO código copiado de setGrafico(), refactorizar o modularizar
			String constSvg = arrayExportar.get(marcador.get()) ? Constantes.SVG_CROSS : Constantes.SVG_TICK;
			InputStream stream = this.getClass().getResourceAsStream(String.format(Constantes.RUTA_SVG_RFE, constSvg));
	        if(stream!=null) {
	            
	            double ancho = btExportarNo.getPrefWidth()*0.7;
	            double alto = btExportarNo.getPrefHeight()*0.7;
	            
	            return new ImageView(new Image(stream,
	                    Math.min(alto, ancho),
	                    Math.min(alto, ancho),
	                    true,
	                    true));
	        }
	        return null;
			
		}, marcador, listaImagenes, arrayExportar));
		
		lbExportarNo.textProperty().bind(Bindings.createStringBinding(() -> {
			
			if(listaImagenes.isEmpty()) {
				return null;
			}
			return arrayExportar.get(marcador.get()) ? "No exportar esta imagen" : "Exportar esta imagen";
			
		}, marcador, listaImagenes, arrayExportar));
		
		//Gráficos
		setGraficos(btAnadirEtiqueta, Constantes.SVG_PLUS);
		setGraficos(btBorrarCategoria, Constantes.SVG_PAPELERA);
		setGraficos(btLimpiar, Constantes.SVG_PAPELERA);
		setGraficos(btBuscar, Constantes.SVG_LUPA);
		setGraficos(btAnterior, Constantes.SVG_FLECHAIZQUIERDA);
		setGraficos(btSiguiente, Constantes.SVG_FLECHADERECHA);
		setGraficos(btExportar, Constantes.SVG_EXPORTAR);
		
	}

	@Override
	public void initVisionado() {
		
		log.debug(".initVisionado() - Iniciando visionado");
		
		switch (getVistaOrigen()) {
		case INICIO:
			refrescarInterfaz();
			break;
		default:
			log.error(".initVisionado() - Pantalla no contemplada: " + getVistaOrigen().toString());
			throw new UnsupportedOperationException("Pantalla no contemplada");
		}
		
	}

	@Override
	public void initFoco() {
		tfEtiquetas.requestFocus();
	}
	
	//-------------
	
	@FXML
	private void btAnadirEtiqueta_click(ActionEvent event) {
		
		String texto = tfEtiquetas.getText();
		
		if(texto==null || texto.trim().isEmpty()) {
			return;
		}
		
		texto = texto.trim().toUpperCase();
		
		tfEtiquetas.setText("");
		
		try {
			
			Long count = servicioEtiqueta.count(texto);
			
			HBoxEtiqueta ve = new HBoxEtiqueta(count, texto, this::eliminarEtiqueta);
			
			if(paneEtiquetas.contiene(ve)) {
				//TODO efecto en la etiqueta indice
				return;
			}
			
			paneEtiquetas.anadirEtiqueta(ve);
			
		} catch (ConstraintViolationException e) {
			new Alert(AlertType.ERROR, "Nombre de etiqueta inválido", ButtonType.OK).showAndWait();
		}
		
	}
	
	@FXML
	private void btBorrarCategoria_click(ActionEvent event) {
		cbCategorias.getSelectionModel().clearSelection();
	}
	
	@FXML
	private void btLimpiar_click(ActionEvent event) {
		limpiarCampos();
	}
	
	@FXML
	private void btBuscar_click(ActionEvent event) {
		
		ImagenBusquedaExportar busqueda = new ImagenBusquedaExportar();
		busqueda.setTodasMenos(rbTodasMenos.isSelected());
		busqueda.setEtiquetas(paneEtiquetas.getEtiquetas().stream().map(HBoxEtiqueta::getNombre).collect(Collectors.toList()));
		busqueda.setCategoria(cbCategorias.getSelectionModel().getSelectedItem());
		if(cbDespuesDe.isSelected() && dpDespuesDe.getValue()!=null) {
			busqueda.setDespuesDe(dpDespuesDe.getValue().atStartOfDay());
		}
		if(cbAntesDe.isSelected() && dpAntesDe.getValue()!=null) {
			busqueda.setAntesDe(dpAntesDe.getValue().atStartOfDay());
		}
		
		try {
			List<Imagen> img = servicioImagen.getBusquedaExportar(busqueda);
			marcador.set(0);
			listaImagenes.clear();
			arrayExportar.clear();
			arrayExportar.addAll(Collections.nCopies(img.size(), true));
			listaImagenes.addAll(img);
		} catch (ConstraintViolationException e) {
			new Alert(AlertType.ERROR, "Se ha producido un error accediendo a ServicioImagen: " + e.getMensaje(), ButtonType.OK);
		}
		
	}
	
	@FXML
	private void btAnterior_click(ActionEvent event) {
		marcador.set(marcador.get()-1);
	}
	
	@FXML
	private void btSiguiente_click(ActionEvent event) {
		marcador.set(marcador.get()+1);
	}
	
	@FXML
	private void btExportarNo_click(ActionEvent event) {
		arrayExportar.set(marcador.get(), ! arrayExportar.get(marcador.get()));
	}
	
	@FXML
	private void btExportar_click(ActionEvent event) {
		
		boolean borrarImagenes = ! cbConservarImagenes.isSelected();
		
		if(borrarImagenes) {
			Optional<ButtonType> respuesta = new Alert(AlertType.CONFIRMATION,
					"Las imágenes exportadas se perderán de tu base de datos. ¿Continuar?",
					ButtonType.OK, ButtonType.CANCEL).showAndWait();
			
			if( ! respuesta.isPresent() || ! respuesta.get().equals(ButtonType.OK)) {
				return;
			}
		}
		
		File f = fileChooser.showSaveDialog(getStage());
		
		if(f==null) {
			return;
		}
		
		if(f.exists()) {
			return;
		}
		
		List<Imagen> imagenesExportar = new ArrayList<>(listaImagenes).stream()
				.filter(img -> arrayExportar.get(listaImagenes.indexOf(img)))
				.collect(Collectors.toList());
		
		Paquete paq = null;
		try {
			
			paq = xmlUtils.convertir(imagenesExportar);
			
			zipUtils.exportar(f.getAbsolutePath(), paq);
			
			//TODO poner para abrir carpeta donde se ha exportado
			new Alert(AlertType.INFORMATION, "Paquete exportado").showAndWait();
			
		} catch (MemeFinderException e) {
			new Alert(AlertType.ERROR, e.getMensaje(), ButtonType.OK).showAndWait();
			return;
		}
		
		if(borrarImagenes) {
			try {
				servicioImagen.borrarPorId(imagenesExportar.stream()
						.map(Imagen::getId)
						.collect(Collectors.toList()));
				btBuscar_click(null);
			} catch (ConstraintViolationException e) {
				new Alert(AlertType.ERROR,
						"No se han podido borrar las imágenes: " +  e.getMensaje(),
						ButtonType.OK).showAndWait();
			}
		}
		
		
	}
	
	//-------------
	
	private void refrescarInterfaz() {
		
		limpiarCampos();
		listaImagenes.clear();
		listaCategorias.clear();
		paneEtiquetas.borrarEtiquetas();
		listaCategorias.addAll(servicioCategoria.getAll());
		marcador.set(0);
		
	}
	
	private void limpiarCampos() {
		
		tgRadio.selectToggle(rbTodasMenos);
		tfEtiquetas.setText("");
		cbCategorias.getSelectionModel().clearSelection();
		dpDespuesDe.setValue(null);
		cbDespuesDe.setSelected(false);
		dpAntesDe.setValue(null);
		cbAntesDe.setSelected(false);
		cbConservarImagenes.setSelected(true);
		
	}
	
	private void eliminarEtiqueta(ActionEvent ev) {
		HBoxEtiqueta origen = (HBoxEtiqueta) ((Button)ev.getSource()).getParent();
		paneEtiquetas.borrarEtiqueta(origen);
	}

}
