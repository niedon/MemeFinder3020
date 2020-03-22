package com.bcadaval.memefinder3020.principal;

import java.io.IOException;
import java.net.URL;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.bcadaval.memefinder3020.utils.Constantes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class SpringFxmlLoader implements ApplicationContextAware{
	
	private ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx=applicationContext;
	}
	
	public void cargaVistas() throws IOException{
		
		//Carga de vistas "accesibles"
		for(Vistas v : Vistas.values()) {
			FXMLLoader load = new FXMLLoader(getClass().getResource(String.format(Constantes.RUTA_FXML, v.getNombre())));
			load.setControllerFactory(ctx::getBean);
			Parent p = load.load();
			
			URL cssEspecifico = getClass().getResource(String.format(Constantes.RUTA_CSSESPECIFICO, v.getNombre()));
			if(cssEspecifico==null) {
				//TODO al logger
			}else {
				//TODO al logger
				p.getStylesheets().add(cssEspecifico.toExternalForm());
			}
			
			((Controlador)load.getController()).setVista(p);
		}
		
		//Carga de pantalla de carga
		FXMLLoader load = new FXMLLoader(getClass().getResource(String.format(Constantes.RUTA_FXML, Constantes.NOMBRE_PANTALLA_CARGA)));
		load.setController(ctx.getBean(GestorDeVentanas.class));
		load.load();
		
	}

}