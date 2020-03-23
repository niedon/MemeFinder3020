package com.bcadaval.memefinder3020.controlador;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.bcadaval.memefinder3020.controlador.componentes.PaneEtiquetas;
import com.bcadaval.memefinder3020.modelo.beans.Categoria;
import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;
import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.principal.Controlador;
import com.bcadaval.memefinder3020.principal.Vistas;
import com.bcadaval.memefinder3020.utils.Constantes;
import com.bcadaval.memefinder3020.utils.FormatUtils;
import com.bcadaval.memefinder3020.utils.IOUtils;
import com.bcadaval.memefinder3020.vista.HBoxEtiqueta;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

@Controller
public class ResultadoIndividualControlador extends Controlador {
	
	static final String DATOS_HAYCAMBIOS = "hayCambios";
	private boolean modoEdicion;
	private boolean hayCambios;
	
	private Imagen imagenSeleccionada;
	
	private PaneEtiquetas paneEtiquetas;
	
	@FXML private ImageView ivImagen;
	
	@FXML private Label lbNombre;
	@FXML private Label lbCategoria;
	@FXML private Label lbFecha;
	@FXML private Label lbExtension;
	@FXML private Label lbResolucion;
	@FXML private Label lbPeso;
	@FXML private AnchorPane apEtiquetas;
	
	@FXML private TextField tfNombre;
	@FXML private ComboBox<Categoria> cbCategoria;
	@FXML private Button btLimpiarCategoria;
	@FXML private TextField tfEtiquetas;
	@FXML private Button btEtiquetas;
	
	@FXML private Button btAmpliar;
	@FXML private Button btEditarGuardar;
	@FXML private Button btVolver;
	@FXML private Button btBorrar;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		paneEtiquetas = new PaneEtiquetas();
		apEtiquetas.getChildren().add(paneEtiquetas);
		AnchorPane.setTopAnchor(paneEtiquetas, .0);
		AnchorPane.setRightAnchor(paneEtiquetas, .0);
		AnchorPane.setBottomAnchor(paneEtiquetas, .0);
		AnchorPane.setLeftAnchor(paneEtiquetas, .0);
		
		cbCategoria.setCellFactory(lv -> new ListCell<Categoria>() {
			@Override
			protected void updateItem(Categoria item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNombre());
			}
		});
		cbCategoria.setButtonCell(cbCategoria.getCellFactory().call(null));
		
		//Asignación de gráficos
		setGraficos(btLimpiarCategoria, Constantes.SVG_PAPELERA);
		setGraficos(btEtiquetas, Constantes.SVG_PLUS);
		setGraficos(btAmpliar, Constantes.SVG_LUPA);
		setGraficos(btVolver, Constantes.SVG_EQUIS);
		setGraficos(btBorrar, Constantes.SVG_PAPELERA);
		
	}

	@Override
	public void initVisionado() {
		
		switch (getVistaOrigen()) {
		case RESULTADOS:
			hayCambios = false;
			modoEdicion = false;
			imagenSeleccionada = (Imagen) datos.get(ResultadosControlador.DATOS_IMAGENSELECCIONADA);
			setModoEdicion(modoEdicion);
			refrescarInterfaz();
			break;

		default:
			throw new RuntimeException("Pantalla no contemplada");
		}
		
	}

	@Override
	public void initFoco() {
	}
	
	//------------------------
	
	@FXML
	private void btLimpiarCategoria_click(ActionEvent event) {
		cbCategoria.getSelectionModel().clearSelection();
	}
	
	@FXML
	private void tfEtiquetas_keyPressed(KeyEvent key) {
		if(key.getCode()==KeyCode.ENTER) {
			btEtiquetas_click(null);
		}
	}
	
	@FXML
	private void btEtiquetas_click(ActionEvent event) {
		if(modoEdicion) {
			String txtEtiqueta = tfEtiquetas.getText().trim().toUpperCase();
			if(!txtEtiqueta.isEmpty()) {
				paneEtiquetas.anadirEtiqueta(servicioEtiqueta.countUsosDeEtiqueta(txtEtiqueta), txtEtiqueta);
				tfEtiquetas.clear();	
			}
		}
	}
	
	@FXML
	private void btVolver_click(ActionEvent event) {
		datos.put(DATOS_HAYCAMBIOS, hayCambios);
		gestorDeVentanas.cambiarEscena(Vistas.RESULTADOS);
	}
	
	@FXML
	private void btAmpliar_click(ActionEvent event) {
		datos.put(VisorImagenControlador.DATOS_IMAGENVISIONADO, ivImagen.getImage());
		gestorDeVentanas.mostrarVisorImagen();
	}
	
	@FXML
	private void btEditarGuardar_click(ActionEvent event) {
		if(modoEdicion) {
			
			try {
				Imagen temp = servicioImagen.save(getImagenEditada());
				new Alert(AlertType.INFORMATION, "Se han guardado los cambios", ButtonType.OK).showAndWait();
				modoEdicion = false;
				setModoEdicion(modoEdicion);
				imagenSeleccionada = temp;
				refrescarInterfaz();
				hayCambios=true;
			} catch (RuntimeException e) {
				new Alert(AlertType.ERROR, "No se han podido guardar los cambios", ButtonType.OK).showAndWait();
				//TODO logger
			}
			
		}else {
			modoEdicion=true;
			setModoEdicion(modoEdicion);
			rellenarDatosEdicion();
		}
	}

	@FXML
	private void btBorrar_click(ActionEvent event) {
		Optional<ButtonType> pulsado = new Alert(AlertType.CONFIRMATION, "¿Quiere borrar esta imagen?", ButtonType.YES, ButtonType.NO).showAndWait();
		
		if(pulsado.get()==ButtonType.YES) {
			try {
				servicioImagen.borrarPorId((imagenSeleccionada.getId()));
				new Alert(AlertType.INFORMATION,"La imagen se ha borrado",ButtonType.OK).showAndWait();
				hayCambios = true;
				datos.put(DATOS_HAYCAMBIOS, hayCambios);
				gestorDeVentanas.cambiarEscena(Vistas.RESULTADOS);
			} catch (RuntimeException e) {
				new Alert(AlertType.ERROR,"No se ha podido borrar la imagen",ButtonType.OK).showAndWait();
			}
			
		}
	}
	
	//---------------------------------------
	
	private void refrescarInterfaz() {
		
		ivImagen.setImage(new Image(IOUtils.getURLDeImagen(imagenSeleccionada)));
		setFit(ivImagen);
		
		if(imagenSeleccionada.getNombre()==null) {
			lbNombre.setText("[Sin nombre]");
		}else {
			lbNombre.setText(imagenSeleccionada.getNombre());
		}
		
		if(imagenSeleccionada.getCategoria()==null) {
			lbCategoria.setText("[Sin categoría]");
		}else {
			lbCategoria.setText(imagenSeleccionada.getCategoria().getNombre());
		}
		
		lbFecha.setText(imagenSeleccionada.getFecha().format(DateTimeFormatter.ofPattern(Constantes.FORMAT_DDMMYYHHMM)));
		lbExtension.setText(imagenSeleccionada.getExtension());
		lbResolucion.setText((int)ivImagen.getImage().getWidth() + " x " + (int)ivImagen.getImage().getHeight() + " px");
		lbPeso.setText(FormatUtils.formatoPeso(IOUtils.getFileDeImagen(imagenSeleccionada).length()));
		
		paneEtiquetas.borrarEtiquetas();
		for(Etiqueta et : imagenSeleccionada.getEtiquetas()) {
			paneEtiquetas.anadirEtiqueta(servicioEtiqueta.countUsosDeEtiqueta(et), et.getNombre());
		}
	}
	
	private void setModoEdicion(boolean poner) {
		
		paneEtiquetas.setEventoEtiquetas(poner ? this::eliminarEtiqueta : null);
		
		lbNombre.setVisible(!poner);
		lbNombre.setManaged(!poner);
		tfNombre.setVisible(poner);
		tfNombre.setManaged(poner);
		
		lbCategoria.setVisible(!poner);
		lbCategoria.setManaged(!poner);
		cbCategoria.setVisible(poner);
		cbCategoria.setManaged(poner);
		btLimpiarCategoria.setVisible(poner);
		btLimpiarCategoria.setManaged(poner);
		
		tfEtiquetas.setVisible(poner);
		btEtiquetas.setVisible(poner);
		btEtiquetas.setDisable(!poner);
		
		btEditarGuardar.setText(poner ? "Guardar" : "Editar");
		setGraficos(btEditarGuardar, poner ? Constantes.SVG_GUARDAR : Constantes.SVG_LAPIZ);
	}
	
	private void eliminarEtiqueta(ActionEvent ev) {
		HBoxEtiqueta origen = (HBoxEtiqueta) ((Button)ev.getSource()).getParent();
		paneEtiquetas.borrarEtiqueta(origen);
	}
	
	private void rellenarDatosEdicion() {
		
		tfNombre.setText(imagenSeleccionada.getNombre());
		cbCategoria.setItems(FXCollections.observableArrayList(servicioCategoria.getAll()));
		cbCategoria.getSelectionModel().select(imagenSeleccionada.getCategoria());//TODO comprobar si da excepción
		paneEtiquetas.borrarEtiquetas();
		for(Etiqueta et : imagenSeleccionada.getEtiquetas()) {
			paneEtiquetas.anadirEtiqueta(servicioEtiqueta.countUsosDeEtiqueta(et), et.getNombre());
		}
		
	}
	
	private Imagen getImagenEditada() {
		Imagen retorna = servicioImagen.getPorId(imagenSeleccionada.getId());
		
		if(tfNombre.getText().trim().equals("")) {
			retorna.setNombre(null);
		}else {
			retorna.setNombre(tfNombre.getText().trim());
		}
		
		retorna.setCategoria(cbCategoria.getValue());
		
		retorna.getEtiquetas().clear();
		for(HBoxEtiqueta hb : paneEtiquetas.getEtiquetas()) {
			retorna.getEtiquetas().add(servicioEtiqueta.getOCrear(hb.getNombre()));
		}
		
		return retorna;
	}
	
}
