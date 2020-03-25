package com.bcadaval.memefinder3020.controlador;

import com.bcadaval.memefinder3020.utils.Constantes;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SplashControlador {

	@FXML private ImageView ivPrincipal;
	@FXML private Label lbTexto;
	@FXML private ProgressBar pbCarga;
	
	public void asignarBindings(ReadOnlyStringProperty msj, ReadOnlyDoubleProperty carga) {
		ivPrincipal.setImage(new Image(getClass().getResource(String.format(Constantes.RUTA_IMG_RF, "splash.jpg")).toString()));
		lbTexto.textProperty().bind(msj);
		pbCarga.progressProperty().bind(carga);
	}
	
}
