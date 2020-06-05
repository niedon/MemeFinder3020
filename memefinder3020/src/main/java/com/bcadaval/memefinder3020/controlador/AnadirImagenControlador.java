package com.bcadaval.memefinder3020.controlador;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;

import com.bcadaval.memefinder3020.concurrencia.TaskGetIndicesImagenesParecidas;
import com.bcadaval.memefinder3020.controlador.componentes.PaneEtiquetas;
import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenTemp;
import com.bcadaval.memefinder3020.principal.Controlador;
import com.bcadaval.memefinder3020.principal.Vistas;
import com.bcadaval.memefinder3020.utils.Constantes;
import com.bcadaval.memefinder3020.vista.HBoxEtiqueta;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Controller
public class AnadirImagenControlador extends Controlador {
	
	private static final Logger log = LogManager.getLogger(AnadirImagenControlador.class);
	
	static final String DATOS_IMAGEN_TEMP = "imagenTemp";
	static final String DATOS_LISTA_NUM_IMG = "listaNumImg";

	private List<ImagenTemp> imagenes;
	private ObservableList<String> listaCategorias;
	private int marcador;
	
	private FileChooser fileChooser;
	
	@FXML private TextField tfDireccion;
	@FXML private Button btExaminar;

	@FXML private ImageView ivImagen;
	@FXML private Button btAnterior;
	@FXML private Label lbNumero;
	@FXML private Button btSiguiente;

	@FXML private TextField tfEtiqueta;
	@FXML private Button btAnadir;
	@FXML private TextField tfNombre;
	@FXML private ComboBox<String> cbCategoria;
	@FXML private Button btCoincidencias;
	@FXML private Label lbCoincidencias;
	@FXML private AnchorPane apEtiquetas;
	private PaneEtiquetas paneEtiquetas;

	@FXML private Button btVolver;
	@FXML private Button btEliminar;
	@FXML private Button btGuardar;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		//Instanciación de elementos necesarios
		
		imagenes = new ArrayList<ImagenTemp>();
		listaCategorias = FXCollections.observableArrayList();
		
		fileChooser = new FileChooser();
		fileChooser.setTitle("Elige la imagen");
		fileChooser.getExtensionFilters()
				.addAll(new ExtensionFilter("Todas las imágenes", "*.jpg", "*jpeg", "*.jpe", "*.jif", "*.jfif", "*.jfi", "*.png", "*.gif", "*.bmp", "*.dib"),
						new ExtensionFilter("JPEG", "*.jpg", "*jpeg", "*.jpe", "*.jif", "*.jfif", "*.jfi"),
						new ExtensionFilter("PNG", "*.png"),
						new ExtensionFilter("GIF", "*.gif"),
						new ExtensionFilter("BMP", "*.bmp", "*.dib"));

		
		//Opciones que no pueden configurarse en SceneBuilder
		
		cbCategoria.setItems(listaCategorias);
		
		paneEtiquetas = new PaneEtiquetas();
		paneEtiquetas.setEventoEtiquetas(this::eliminarEtiqueta);
		apEtiquetas.getChildren().add(paneEtiquetas);
		AnchorPane.setTopAnchor(paneEtiquetas, .0);
		AnchorPane.setRightAnchor(paneEtiquetas, .0);
		AnchorPane.setBottomAnchor(paneEtiquetas, .0);
		AnchorPane.setLeftAnchor(paneEtiquetas, .0);
		
		//Asignación de gráficos
		setGraficos(btExaminar, Constantes.SVG_LUPA);
		setGraficos(btAnadir, Constantes.SVG_PLUS);
		setGraficos(btAnterior, Constantes.SVG_FLECHAIZQUIERDA);
		setGraficos(btSiguiente, Constantes.SVG_FLECHADERECHA);
		setGraficos(btVolver, Constantes.SVG_EQUIS);
		setGraficos(btEliminar, Constantes.SVG_PAPELERA);
		setGraficos(btGuardar, Constantes.SVG_GUARDAR);
		
	}

	@Override
	public void initVisionado() {
		
		log.debug(".initVisionado() - Iniciando visionado");
		
		switch(getVistaOrigen()) {
		case INICIO:
			refrescarInterfaz();
			break;
			
		case COINCIDENCIAS:
			Object o = datos.get(CoincidenciasControlador.BORRAR_IMAGEN); 
			if(o!=null && (boolean)o) {
				btEliminiar_click(null);
			}
			break;
			
		default:
			log.error(".initVisionado() - Pantalla no contemplada: " + getVistaOrigen().toString());
			throw new UnsupportedOperationException("Se ha accedido desde una vista no soportada");
		}
		

	}

	@Override
	public void initFoco() {
	}

	// -----------------------------------

	@FXML
	private void btExaminar_click(ActionEvent event) {

		File f = fileChooser.showOpenDialog(getStage());
		
		if(f!=null && f.exists()) {
			
			for(ImagenTemp temp : imagenes) {
				if(temp.getImagen().getAbsolutePath().equals(f.getAbsolutePath())) {
					new Alert(AlertType.ERROR, "Ya has añadido esta imagen", ButtonType.OK).showAndWait();
					return;
				}
			}
			
			ImagenTemp imgTemp = new ImagenTemp();
			imgTemp.setImagen(f);
			imgTemp.setNombre(f.getName().substring(0,f.getName().lastIndexOf('.')));
			
			Task<List<Integer>> coincidencias = new TaskGetIndicesImagenesParecidas(f,servicioImagen,rutasUtils);
			coincidencias.setOnSucceeded(e -> activarBotonCoincidenciasSiProcede(coincidencias));
			coincidencias.setOnCancelled(e -> btEliminiar_click(null));
			coincidencias.setOnFailed(e -> {
				new Alert(AlertType.ERROR, "No se ha podido cargar la imagen", ButtonType.OK).showAndWait();
				btEliminiar_click(null);
			});
			imgTemp.setCoincidencias(coincidencias);
			
			imagenes.add(imgTemp);
			marcador = imagenes.size()-1;
			
			comenzarTarea(coincidencias, 10);
			
			refrescarInterfaz();
		}
	}

	@FXML
	private void btAnterior_click(ActionEvent event) {
		marcador--;
		cargarDatosYRefrescarInterfaz();
	}

	@FXML
	private void btSiguiente_click(ActionEvent event) {
		marcador++;
		cargarDatosYRefrescarInterfaz();
	}

	@FXML
	private void tfEtiqueta_keyPressed(KeyEvent key) {
		if(key.getCode()==KeyCode.ENTER) btAnadir_click(null);
	}

	@FXML
	private void btAnadir_click(ActionEvent event) {
		
		String texto = tfEtiqueta.getText().toUpperCase().trim();
		
		tfEtiqueta.clear();
		if(imagenes.isEmpty() || texto.isEmpty()) {
			return;
		}
		
		try {
			
			Long count = servicioEtiqueta.count(texto);
			
			HBoxEtiqueta ve = new HBoxEtiqueta(count, texto, this::eliminarEtiqueta);
			
			ImagenTemp seleccionada = imagenes.get(marcador);
			
			if(seleccionada.getEtiquetas().contains(ve)) {
				//int indice = seleccionada.getEtiquetas().indexOf(ve);
				//TODO efecto en la etiqueta indice
				return;
			}
			
			paneEtiquetas.anadirEtiqueta(ve);
			seleccionada.getEtiquetas().add(ve);
			
		} catch (ConstraintViolationException e) {
			new Alert(AlertType.ERROR, "Nombre de etiqueta inválido", ButtonType.OK).showAndWait();
		}
		
	}
	
	@FXML
	private void btCoincidencias_click(ActionEvent event) {
		datos.put(DATOS_IMAGEN_TEMP, imagenes.get(marcador));
		cambiarEscena(Vistas.COINCIDENCIAS);
	}

	@FXML
	private void btVolver_clic(ActionEvent event) {
		cambiarEscena(Vistas.INICIO);
	}

	@FXML
	private void btEliminiar_click(ActionEvent event) {
		imagenes.remove(marcador);
		refrescarInterfaz();
	}

	@FXML
	private void btGuardar_click(ActionEvent event) {
		
		ImagenTemp seleccionada = imagenes.get(marcador); 
		
		seleccionada.setNombre(tfNombre.getText().trim());
		if(cbCategoria.getValue()!=null) {
			seleccionada.setCategoria(cbCategoria.getValue().trim().toUpperCase());
		}
		seleccionada.getEtiquetas().forEach(el -> seleccionada.getEtiquetasString().add(el.getNombre()));
		
		try {
			servicioImagen.anadir(imagenes.get(marcador));
			imagenes.remove(marcador);
		} catch (ConstraintViolationException e) {
			new Alert(AlertType.ERROR,String.format("No se ha podido añadir la imagen: %s", e.getMensaje()),ButtonType.OK).showAndWait();
		}
		//TODO actualizar contador etiquetas
		refrescarInterfaz();
		
	}
	
	//-------------------------------------
	
	private void cargarDatosYRefrescarInterfaz() {
		
		if(!imagenes.isEmpty()) {
			imagenes.get(marcador).setNombre(tfNombre.getText().trim());
			if(cbCategoria.getValue()==null) {
				imagenes.get(marcador).setCategoria(null);
			} else {
				imagenes.get(marcador).setCategoria(cbCategoria.getValue().trim().toUpperCase());
			}
			
		}
		refrescarInterfaz();
	}
	
	private void refrescarInterfaz() {
		
		if(marcador >= imagenes.size()) {
			marcador = imagenes.size()-1;
		}
		listaCategorias.clear();
		cbCategoria.setValue(null);
		paneEtiquetas.borrarEtiquetas();
		btCoincidencias.disableProperty().unbind();
		lbCoincidencias.textProperty().unbind();
		btGuardar.disableProperty().unbind();
		
		if(imagenes.isEmpty()) {
			
			tfDireccion.clear();
			ivImagen.setImage(null);
			tfNombre.clear();
			cbCategoria.setDisable(true);
			
			lbNumero.setText("0/0");
			btCoincidencias.setDisable(true);
			lbCoincidencias.setText("");
			btGuardar.setDisable(true);
			
		}else {
			
			ImagenTemp seleccionada = imagenes.get(marcador);
			
			tfDireccion.setText(seleccionada.getImagen().getAbsolutePath());
			
			ivImagen.setImage(new Image(seleccionada.getImagen().toURI().toString()));
			setFit(ivImagen);
			
			tfNombre.setText(seleccionada.getNombre());
			cbCategoria.setDisable(false);
			servicioCategoria.getAll().forEach(el -> listaCategorias.add(el.getNombre()));
			seleccionada.getEtiquetas().forEach(el -> paneEtiquetas.anadirEtiqueta(el));
			
			lbNumero.setText((marcador+1)+"/"+imagenes.size());
			
			btCoincidencias.setDisable(! (seleccionada.getCoincidencias().isDone() && !seleccionada.getCoincidencias().getValue().isEmpty()) );
			lbCoincidencias.textProperty().bind(imagenes.get(marcador).getCoincidencias().messageProperty());
			
			btGuardar.disableProperty().bind(seleccionada.getCoincidencias().runningProperty());
			
		}
		
		btAnterior.setDisable(marcador<=0);
		btSiguiente.setDisable(marcador>=(imagenes.size()-1));
		tfEtiqueta.clear();
		btAnadir.setDisable(imagenes.isEmpty());
		
		btEliminar.setDisable(imagenes.isEmpty());
		
	}
	
	private void eliminarEtiqueta(ActionEvent ev) {
		HBoxEtiqueta origen = (HBoxEtiqueta) ((Button)ev.getSource()).getParent();
		paneEtiquetas.borrarEtiqueta(origen);
		imagenes.get(marcador).getEtiquetas().remove(origen);
	}
	
	private void activarBotonCoincidenciasSiProcede(Task<List<Integer>> coincidencias) {
		
		if(imagenes.isEmpty()) {
			return;
		}
		
		if(imagenes.get(marcador).getCoincidencias() != coincidencias) {
			return;
		}
		
		if(coincidencias.getValue().isEmpty()) {
			return;
		}
		
		btCoincidencias.setDisable(false);
		
	}

}
