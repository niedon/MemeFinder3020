package com.bcadaval.memefinder3020.controlador.componentes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bcadaval.memefinder3020.utils.Constantes;
import com.bcadaval.memefinder3020.vista.HBoxEtiqueta;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

@Controller
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PaneEtiquetas extends StackPane {
	
	@FXML private FlowPane fpEtiquetas;
	
	private EventHandler<ActionEvent> eventoEtiquetas;
	
	public PaneEtiquetas() {
		
		FXMLLoader load = new FXMLLoader(getClass().getResource(String.format(Constantes.RUTA_COMPONENTES_FXML_RFE, Constantes.NOMBRE_PANEETIQUETAS)));
		load.setRoot(this);
		load.setController(this);
		
		try {
            load.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
	}
	
	public void borrarEtiquetas() {
		fpEtiquetas.getChildren().clear();
	}
	
	public void borrarEtiqueta(HBoxEtiqueta etiqueta) {
		fpEtiquetas.getChildren().remove(etiqueta);
	}
	
	public void anadirEtiqueta(int num, String etiqueta) {
		fpEtiquetas.getChildren().add(new HBoxEtiqueta(num, etiqueta, eventoEtiquetas));
	}
	
	public void anadirEtiqueta(HBoxEtiqueta etiqueta) {
		fpEtiquetas.getChildren().add(etiqueta);
	}
	
	public void setEventoEtiquetas(EventHandler<ActionEvent> eventoEtiquetas) {
		this.eventoEtiquetas = eventoEtiquetas;
	}
	
	public boolean contiene(HBoxEtiqueta etiqueta) {
		return fpEtiquetas.getChildren().contains(etiqueta);
	}
	
	public List<HBoxEtiqueta> getEtiquetas(){
		return Arrays.asList(fpEtiquetas.getChildren().toArray(new HBoxEtiqueta[fpEtiquetas.getChildren().size()]));
	}
	
	
	
}
