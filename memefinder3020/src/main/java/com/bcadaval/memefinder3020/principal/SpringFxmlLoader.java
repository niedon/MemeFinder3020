package com.bcadaval.memefinder3020.principal;

import java.io.IOException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.bcadaval.memefinder3020.utils.Constantes;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class SpringFxmlLoader implements ApplicationContextAware{
	
	private static final Logger log = LogManager.getLogger(SpringFxmlLoader.class);
	
	private ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx=applicationContext;
	}
	
	public void cargaVistas() throws IOException{
		
		log.debug(".cargaVistas() - Iniciando carga de vistas");
		
		//Carga de vistas "accesibles"
		for(Vistas v : Vistas.values()) {
			
			log.debug(".cargaVistas() - Cargando vista: " + v.toString());
			
			FXMLLoader load = new FXMLLoader(getClass().getResource(String.format(Constantes.RUTA_FXML_RFE, v.getNombre())));
			load.setControllerFactory(ctx::getBean);
//			load.setControllerFactory(new Callback<Class<?>, Object>() {
//				@Override
//				public Object call(Class<?> clazz) {
//					return ctx.getBean(clazz);
//				}
//			});
			Parent p = load.load();
			
			URL cssEspecifico = getClass().getResource(String.format(Constantes.RUTA_CSSESPECIFICO_RFE, v.getNombre()));
			if(cssEspecifico==null) {
				log.error(".cargaVistas() - No se ha encontrado css específico para: " + v.toString());
			}else {
				log.debug(".cargaVistas() - Aplicando css específico a: " + v.toString());
				p.getStylesheets().add(cssEspecifico.toExternalForm());
			}
			
			/*TODO
				mover el siguiente if a gestordeventanas si no se puede
				cambiar el initowner de una ventana, o dejar así si no
				se plantea mostrar una ventana modal sobre distintos
				stages 
			*/
			if(v.esModal()) {
				Platform.runLater(() -> {
					Stage stageModal = new Stage();
					stageModal.setOnCloseRequest(e -> {
						((Controlador)load.getController()).onClose();
						e.consume();
					});
					stageModal.setResizable(false);
					stageModal.setScene(new Scene(p));
				});
				
			}
			((Controlador)load.getController()).setVista(p);
		}
		
		log.debug(".cargaVistas() - Cargando vista de pantalla splash");
		
		//Carga de pantalla de carga
		FXMLLoader load = new FXMLLoader(getClass().getResource(String.format(Constantes.RUTA_FXML_RFE, Constantes.NOMBRE_PANTALLA_CARGA)));
		load.setController(ctx.getBean(GestorDeVentanas.class));
		load.load();
		
		log.debug(".cargaVistas() - Finalizada carga de vistas");
		
	}

}