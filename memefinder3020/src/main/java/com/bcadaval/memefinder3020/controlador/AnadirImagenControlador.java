package com.bcadaval.memefinder3020.controlador;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenTemp;
import com.bcadaval.memefinder3020.principal.Vistas;
import com.bcadaval.memefinder3020.utils.TareaComparaImagenes;
import com.bcadaval.memefinder3020.vista.HBoxEtiqueta;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Controller
public class AnadirImagenControlador extends Controlador {

	private List<ImagenTemp> imagenes;
	private int marcador;
	
	private FileChooser fileChooser;
	
	@FXML
	private TextField tfDireccion;

	@FXML
	private ImageView ivImagen;
	@FXML
	private Button btAnterior;
	@FXML
	private Label lbNumero;
	@FXML
	private Button btSiguiente;

	@FXML
	private TextField tfEtiqueta;
	@FXML
	private Button btAnadir;
	@FXML
	private TextField tfNombre;
	@FXML
	private Button btCoincidencias;
	@FXML
	private Label lbCoincidencias;
	@FXML
	private TitledPane tpEtiquetas;
	@FXML
	private FlowPane flowEtiquetas;

	@FXML
	private Button btEliminar;
	@FXML
	private Button btGuardar;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		//Instanciación de elementos necesarios
		
		imagenes = new ArrayList<ImagenTemp>();
		
		fileChooser = new FileChooser();
		fileChooser.setTitle("Elige la imagen");
		fileChooser.getExtensionFilters()
				.addAll(new ExtensionFilter("Todas las imágenes", "*.jpg", "*jpeg", "*.jpe", "*.jif", "*.jfif", "*.jfi", "*.png", "*.gif", "*.bmp", "*.dib"),
						new ExtensionFilter("JPEG", "*.jpg", "*jpeg", "*.jpe", "*.jif", "*.jfif", "*.jfi"),
						new ExtensionFilter("PNG", "*.png"),
						new ExtensionFilter("GIF", "*.gif"),
						new ExtensionFilter("BMP", "*.bmp", "*.dib"));

		
		//Opciones que no pueden configurarse en SceneBuilder
		
		tpEtiquetas.setCollapsible(false);
		
		tfNombre.focusedProperty().addListener((obs, viejo, nuevo) -> {
			
			if( ! nuevo && ! imagenes.isEmpty()) {
				imagenes.get(marcador).setNombre(tfNombre.getText());
			}
		});
		
	}

	@Override
	public void initComponentes() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initVisionado() {
		refrescarInterfaz();

	}

	@Override
	public void initFoco() {
		// TODO Auto-generated method stub

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
			
			Task<List<Integer>> coincidencias = new TareaComparaImagenes(f,servicioImagen);
			coincidencias.setOnSucceeded(e -> activarBotonCoincidenciasSiProcede(coincidencias));
			imgTemp.setCoincidencias(coincidencias);
			
			imagenes.add(imgTemp);
			marcador = imagenes.size()-1;
			
			new Thread(coincidencias).start();
			
			refrescarInterfaz();
		}
	}

	@FXML
	private void btAnterior_click(ActionEvent event) {
		marcador--;
		refrescarInterfaz();
	}

	@FXML
	private void btSiguiente_click(ActionEvent event) {
		marcador++;
		refrescarInterfaz();
	}

	@FXML
	private void tfEtiqueta_keyPressed(KeyEvent key) {
		if(key.getCode()==KeyCode.ENTER) btAnadir_click(null);
	}

	@FXML
	private void btAnadir_click(ActionEvent event) {
		
		String texto = tfEtiqueta.getText().toUpperCase().trim();
		
		tfEtiqueta.clear();
		if(texto.isEmpty()) {
			return;
		}
		
		HBoxEtiqueta ve = new HBoxEtiqueta(servicioEtiqueta.countUsosDeEtiqueta(texto), texto, this::eliminarEtiqueta);
		
		ImagenTemp seleccionada = imagenes.get(marcador);
		
		if(seleccionada.getEtiquetas().contains(ve)) {
			int indice = seleccionada.getEtiquetas().indexOf(ve);
			//TODO efecto en la etiqueta indice
			return;
		}
		
		flowEtiquetas.getChildren().add(ve);
		seleccionada.getEtiquetas().add(ve);
		
	}

	@FXML
	private void btVolver_clic(ActionEvent event) {
		gestorDeVentanas.cambiarEscena(Vistas.INICIO);
	}

	@FXML
	private void btEliminiar_click(ActionEvent event) {
		imagenes.remove(marcador);
		refrescarInterfaz();
	}

	@FXML
	private void btGuardar_click(ActionEvent event) {
		
		servicioImagen.anadirImagen(imagenes.get(marcador));
		imagenes.remove(marcador);
		//TODO actualizar contador etiquetas
		refrescarInterfaz();
		
	}
	
	//-------------------------------------
	
	private void refrescarInterfaz() {
		
		if(marcador >= imagenes.size()) {
			marcador = imagenes.size()-1;
		}
		flowEtiquetas.getChildren().clear();
		btCoincidencias.disableProperty().unbind();
		lbCoincidencias.textProperty().unbind();
		btGuardar.disableProperty().unbind();
		
		
		if(imagenes.isEmpty()) {
			
			tfDireccion.clear();
			ivImagen.setImage(null);
			tfNombre.clear();
			
			lbNumero.setText("0/0");
			btCoincidencias.setDisable(true);
			lbCoincidencias.setText("");
			btGuardar.setDisable(true);
			
		}else {
			
			ImagenTemp seleccionada = imagenes.get(marcador);
			
			tfDireccion.setText(seleccionada.getImagen().getAbsolutePath());
			ivImagen.setImage(new Image(seleccionada.getImagen().toURI().toString()));
			tfNombre.setText(seleccionada.getNombre());
			flowEtiquetas.getChildren().addAll(seleccionada.getEtiquetas());
			
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
		flowEtiquetas.getChildren().remove(origen);
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
