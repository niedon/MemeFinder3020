package com.bcadaval.memefinder3020.controlador;

import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.bcadaval.memefinder3020.concurrencia.TaskGetPorcentajeImagenesParecidas;
import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenTemp;
import com.bcadaval.memefinder3020.principal.Controlador;
import com.bcadaval.memefinder3020.principal.Vistas;
import com.bcadaval.memefinder3020.utils.MiscUtils;
import com.bcadaval.memefinder3020.vista.HBoxEtiqueta;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

@Controller
public class CoincidenciasControlador extends Controlador {
	
	private ImagenTemp imgNueva;
	private List<Imagen> listaImgCoincidencias;
	private List<Double> listaPorcentajeCoincidencias;
	private int marcador;

	@FXML private ImageView ivNueva;
	@FXML private ImageView ivOriginal;
	
	@FXML private TitledPane tpNueva;
	@FXML private TitledPane tpOriginal;
	
	@FXML private FlowPane flowNueva;
	@FXML private FlowPane flowOriginal;
	
	@FXML private Label lbNombreNueva;
	@FXML private Label lbExtensionNueva;
	@FXML private Label lbResolucionNueva;
	@FXML private Label lbCategoriaNueva;
	@FXML private Label lbFechaNueva;
	@FXML private Label lbNombreOriginal;
	@FXML private Label lbExtensionOriginal;
	@FXML private Label lbResolucionOriginal;
	@FXML private Label lbCategoriaOriginal;
	@FXML private Label lbFechaOriginal;
	@FXML private Label lbPorcentajeParecido;
	
	@FXML private Button btAnadirEtiquetas;
	@FXML private Button btAnterior;
	@FXML private Label lbMarcador;
	@FXML private Button btSiguiente;
	
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tpNueva.setCollapsible(false);
		tpOriginal.setCollapsible(false);
		
	}

	@Override
	public void initComponentes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initVisionado() {
		
		Class<?> c = (Class<?>) datos.get(CLASE_ORIGEN);
		
		if(c == AnadirImagenControlador.class) {
			
			flowNueva.getChildren().clear();
			
			imgNueva = (ImagenTemp) datos.get(AnadirImagenControlador.DATOS_IMAGEN_TEMP);
			listaImgCoincidencias = servicioImagen.getAllPorId(imgNueva.getCoincidencias().getValue());
			marcador = 0;
			
			ivNueva.setImage(new Image(imgNueva.getImagen().toURI().toString()));
			imgNueva.getEtiquetas().forEach( el -> {
				flowNueva.getChildren().add(new HBoxEtiqueta(el.getNumVeces(), el.getNombre()));
			});
			
			lbNombreNueva.setText(imgNueva.getNombre());
			String nombreImagenNueva = imgNueva.getImagen().getName();
			lbExtensionNueva.setText(nombreImagenNueva.substring(nombreImagenNueva.lastIndexOf('.')));
			
			try {
				Dimension d = MiscUtils.getImageDimension(imgNueva.getImagen());
				lbResolucionNueva.setText(d.width + " x " + d.height);
			} catch (IOException e) {
				//TODO log
				lbResolucionNueva.setText("No disponible");
			}
			
			if(imgNueva.getCategoria() == null) {
				lbCategoriaNueva.setText("[Sin categoría]");
			}else {
				lbCategoriaNueva.setText(imgNueva.getCategoria());
			}
			
			lbFechaNueva.setText("(Ahora)");
			
			TaskGetPorcentajeImagenesParecidas task = new TaskGetPorcentajeImagenesParecidas(imgNueva.getImagen(), listaImgCoincidencias);
			task.setOnSucceeded(e -> {
				listaPorcentajeCoincidencias = task.getValue();
				refrescarInterfaz();
			});
			task.setOnCancelled(e -> {
				gestorDeVentanas.cambiarEscena(Vistas.ANADIR_IMAGEN);
			});
			task.setOnFailed(e -> {
				new Alert(AlertType.ERROR, "Ha habido un error calculando el parecido de la imagen", ButtonType.OK).showAndWait();
				gestorDeVentanas.cambiarEscena(Vistas.ANADIR_IMAGEN);
			});
			
			comenzarTarea(task, 10);
			
			
			
			
		}
		
	}

	@Override
	public void initFoco() {
		// TODO Auto-generated method stub
		
	}
	
	@FXML
	private void btVolver_click(ActionEvent event) {
		gestorDeVentanas.cambiarEscena(Vistas.ANADIR_IMAGEN);
	}
	
	@FXML
	private void btAnadirEtiquetas_click(ActionEvent event) {
		
	}
	
	@FXML
	private void btSobreescribirImagen_click(ActionEvent event) {
		
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
	
	private void refrescarInterfaz() {
		
		flowOriginal.getChildren().clear();
		
		if(marcador>=listaImgCoincidencias.size()) {
			marcador = listaImgCoincidencias.size()-1;
		}
		Imagen elegida = listaImgCoincidencias.get(marcador);
		
		ivOriginal.setImage(new Image(MiscUtils.getStringRutaImagen(elegida)));
		elegida.getEtiquetas().forEach( el -> {
			flowOriginal.getChildren().add(new HBoxEtiqueta(el.getImagenes().size(), el.getNombre()));
		});
		
		if(elegida.getNombre()==null || elegida.getNombre().isEmpty()) {
			lbNombreOriginal.setText("[Sin nombre]");
		}else {
			lbNombreOriginal.setText(elegida.getNombre());
		}
		
		lbExtensionOriginal.setText(elegida.getExtension());
		
		try {
			Dimension d = MiscUtils.getImageDimension(MiscUtils.getFileDeImagen(elegida));
			lbResolucionOriginal.setText(d.width + " x " + d.height);
		} catch (IOException e) {
			//TODO log
			lbResolucionOriginal.setText("No disponible");
		}
		
		if(elegida.getCategoria() == null || elegida.getCategoria().isEmpty()) {
			lbCategoriaOriginal.setText("[Sin categoría]");
		}else {
			lbCategoriaOriginal.setText(elegida.getCategoria());
		}
		
		lbFechaOriginal.setText(elegida.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")));
		
		lbPorcentajeParecido.setText(String.format("%.2f %%", 100*(1-listaPorcentajeCoincidencias.get(marcador))));
		
		boolean noHayElementos = listaImgCoincidencias==null || listaImgCoincidencias.isEmpty();
		btAnterior.setDisable(noHayElementos || marcador==0);
		btSiguiente.setDisable(noHayElementos || marcador==listaImgCoincidencias.size()-1);
		lbMarcador.setText(noHayElementos ? "0/0" : String.format("%d/%d", marcador+1,listaImgCoincidencias.size()));
		
	}

}
