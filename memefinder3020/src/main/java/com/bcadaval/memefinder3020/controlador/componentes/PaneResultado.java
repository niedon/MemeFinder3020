package com.bcadaval.memefinder3020.controlador.componentes;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.utils.Constantes;
import com.bcadaval.memefinder3020.utils.RutasUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class PaneResultado extends AnchorPane{
	
	@FXML private ImageView ivImagen;
	@FXML private Label lbNombre;
	@FXML private Label lbCategoria;
	@FXML private Label lbFecha;
	@FXML private Label lbEtiquetas;
	@FXML private AnchorPane apEtiquetas;
	
	public PaneResultado(Imagen imagen, RutasUtils rutasUtils) {
		
		FXMLLoader load = new FXMLLoader(getClass().getResource(String.format(Constantes.RUTA_COMPONENTES_FXML_RFE, Constantes.NOMBRE_PANERESULTADO)));
		load.setRoot(this);
		load.setController(this);
		
		try {
            load.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
		
		ivImagen.setImage(new Image(rutasUtils.getURLDeImagen(imagen)));
		lbNombre.setText(imagen.getNombre());
		if(imagen.getCategoria()==null) {
			lbCategoria.setText("[Sin categoría]");
		}else {
			lbCategoria.setText(imagen.getCategoria().getNombre());
		}
		
		lbFecha.setText(imagen.getFecha().format(DateTimeFormatter.ofPattern(Constantes.FORMAT_DDMMYYHHMM)));
		//TODO internacionalizar:
		lbEtiquetas.setText(imagen.getEtiquetas().size() + " etiqueta" + (imagen.getEtiquetas().size()==1 ? "" : "s"));
		
	}

}
