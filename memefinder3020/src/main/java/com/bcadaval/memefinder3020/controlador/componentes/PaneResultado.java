package com.bcadaval.memefinder3020.controlador.componentes;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.utils.Constantes;
import com.bcadaval.memefinder3020.utils.IOUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class PaneResultado extends AnchorPane{
	
	@FXML private ImageView ivImagen;
	@FXML private Label lbNombre;
	@FXML private Label lbExtension;
	@FXML private Label lbCategoria;
	@FXML private Label lbFecha;
	@FXML private AnchorPane apEtiquetas;
	
	public PaneResultado(Imagen imagen, Map<Integer,Integer> mapaNum) {
		
		FXMLLoader load = new FXMLLoader(getClass().getResource(String.format(Constantes.RUTA_COMPONENTE_FXML, Constantes.NOMBRE_PANERESULTADO)));
		load.setRoot(this);
		load.setController(this);
		
		try {
            load.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
		
		ivImagen.setImage(new Image(IOUtils.getURLDeImagen(imagen)));
		lbNombre.setText(imagen.getNombre());
		lbExtension.setText(imagen.getExtension());
		if(imagen.getCategoria()==null) {
			lbCategoria.setText("[Sin categoría]");
		}else {
			lbCategoria.setText(imagen.getCategoria().getNombre());
		}
		
		lbFecha.setText(imagen.getFecha().format(DateTimeFormatter.ofPattern(Constantes.FORMAT_DDMMYYHHMM)));
		
		PaneEtiquetas etiquetas = new PaneEtiquetas();
		imagen.getEtiquetas().forEach(el -> etiquetas.anadirEtiqueta(mapaNum.get(el.getId()),el.getNombre()));
		apEtiquetas.getChildren().add(etiquetas);
		AnchorPane.setTopAnchor(etiquetas, .0);
		AnchorPane.setRightAnchor(etiquetas, .0);
		AnchorPane.setBottomAnchor(etiquetas, .0);
		AnchorPane.setLeftAnchor(etiquetas, .0);
		
	}

}
