package com.bcadaval.memefinder3020.principal;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.bcadaval.memefinder3020.utils.Constantes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class SpringFxmlLoader implements ApplicationContextAware{
	
	private ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx=applicationContext;
	}
	
	public void cargaVistas(Stage stage) throws IOException{
		
		for(Vistas v : Vistas.values()) {
			FXMLLoader load = new FXMLLoader(getClass().getResource(String.format(Constantes.RUTA_FXML, v.getNombre())));
			load.setControllerFactory(ctx::getBean);
			Parent p = load.load();
			((Controlador)load.getController()).setVista(p);
			((Controlador)load.getController()).setStage(stage);
		}
		
	}

}