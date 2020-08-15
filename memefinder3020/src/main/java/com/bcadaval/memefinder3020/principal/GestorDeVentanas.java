package com.bcadaval.memefinder3020.principal;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.bcadaval.memefinder3020.excepciones.GUIException;
import com.bcadaval.memefinder3020.utils.Constantes;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class GestorDeVentanas implements ApplicationContextAware{
	
	private static final Logger log = LogManager.getLogger(GestorDeVentanas.class);
	
	@Autowired private ResourceBundle resourceBundle;
	
	private static final Vistas primeraPantalla = Vistas.INICIO;
	
	@FXML private AnchorPane nodoPrincipalPantallaCarga;
	@FXML private Button btCancelar;
	
	private ApplicationContext ctx;
	
	private Stage stage;
	private StackPane panePrincipal;
	
	private Vistas vistaActual;

	public Stage getStage() {
		return stage;
	}
	
	public void cambiarEscena(Vistas v) throws GUIException {
		
		log.debug(String.format(".cambiarEscena() - Cambiando vista: %s -> %s", vistaActual.toString(), v.toString()));
		
		//Se evita cambiar de una vista modal a una normal (hay que usar onClose)
		if(vistaActual.esModal() && !v.esModal()) {
			log.error(".cambiarEscena() - Se ha intentado cambiar de una vista modal a una normal");
			throw new GUIException("No se puede cambiar a una vista normal desde una modal");
		}
		
		//Se evita cambiar a la misma vista
		if(v==vistaActual) {
			log.error(".cambiarEscena() - Se ha intentado cambiar de una vista a sí misma");
			throw new GUIException("No se puede cambiar a la misma vista");
		}
		
		//Se evita mostrar una vista más de una vez en una pila de vistas
		Controlador c = ctx.getBean(vistaActual.getClaseControlador());
		while(c.getVistaPadre() != null) {
			if(c.getVistaPadre()==v) {
				log.error(".cambiarEscena() - Se ha intentado cambiar a una vista que ya está en la pila");
				throw new GUIException(String.format("La vista %s ya está en la pila de vistas", v.getNombre()));
			}
			c = ctx.getBean(c.getVistaPadre().getClaseControlador());
		}
		
		Controlador cNuevaVista = ctx.getBean(v.getClaseControlador());
		if(v.esModal()) {
			//escenaPrincipal.getStylesheets().add(getClass().getResource(String.format(Constantes.RUTA_CSS_RFE, "principal")).toExternalForm());
			cNuevaVista.setVistaPadre(vistaActual);
			
			if(cNuevaVista.getStage().getOwner()==null) {
				cNuevaVista.getStage().initOwner(ctx.getBean(vistaActual.getClaseControlador()).getStage());
				cNuevaVista.getStage().initModality(Modality.WINDOW_MODAL);
			}
			
			//TODO [SOLUCIÓN TEMPORAL] aplicar cascadas o refactorizar la aplicación de CSS
			ObservableList<String> estilosModal = cNuevaVista.getStage().getScene().getStylesheets();
			
			//El visor se trata por separado
			if(v==Vistas.VISORIMAGEN) {
				cNuevaVista.getStage().setMaximized(true);
			}else {
				estilosModal.add(getClass().getResource(String.format(Constantes.RUTA_CSS_RFE, "principal")).toExternalForm());
			}
			
			setTitulo(cNuevaVista.getStage(), v);
			
			cNuevaVista.getStage().show();
			
		}else {
			panePrincipal.getChildren().set(0, cNuevaVista.getVista());
			setTitulo(stage, v);
		}
		
		eventosControlador(cNuevaVista, v);
	}
	
	void onClose() {
		
		Controlador cActual = ctx.getBean(vistaActual.getClaseControlador());
		
		if(cActual.getVistaPadre()==null) {
			log.debug(".onClose() - Cerrando aplicación");
			Platform.exit();
		}else {
			
			log.debug(String.format(".onClose() - Cambiando de vista actual a vista padre: %s -> %s", vistaActual, cActual.getVistaPadre()));
			
			Controlador cPadre = ctx.getBean(cActual.getVistaPadre().getClaseControlador());
			
			eventosControlador(cPadre, cActual.getVistaPadre());
			
			cActual.getStage().hide();
		}
		
	}
	
	private void eventosControlador(Controlador c, Vistas nuevaVista) {
		
		c.anadirVistaAMapa(vistaActual);
		vistaActual = nuevaVista;
		c.initVisionado();
		c.initFoco();
		c.limpiarMapa();
		
	}
	
	private void mostrarPrimeraPantalla(){
		
		Controlador c = ctx.getBean(primeraPantalla.getClaseControlador());
		panePrincipal.getChildren().set(0, c.getVista());
		
		//Datos de carga
		c.anadirVistaAMapa(primeraPantalla);
		vistaActual = primeraPantalla;
		//TODO añadir datos de información de carga
		Controlador.datos.put("test", "datos aquí");
		
		setTitulo(stage, primeraPantalla);
		
		c.initVisionado();
		c.initFoco();
		c.limpiarMapa();
		
	}
	
	public void iniciar(Stage stage) {
		this.stage = stage;
		
		panePrincipal = new StackPane();
		panePrincipal.setPrefSize(1024, 768);
		panePrincipal.getChildren().add(0,new Pane());
		panePrincipal.getChildren().add(1, nodoPrincipalPantallaCarga);
		
		Scene escenaPrincipal = new Scene(panePrincipal);
		escenaPrincipal.getStylesheets().add(getClass().getResource(String.format(Constantes.RUTA_CSS_RFE, "principal")).toExternalForm());
		
		stage.setScene(escenaPrincipal);
		stage.setResizable(false);
		mostrarPrimeraPantalla();
		quitarCargando();
		
	}
	
	void setCargando(Task<?> task, int segundos) {
		
		log.debug(String.format(".setCargando() - Iniciando carga. Tarea: [%s]. Tiempo botón cancelar: [%d]", task.getTitle(), segundos));
		
		panePrincipal.getChildren().get(0).setEffect(new GaussianBlur(5));
		nodoPrincipalPantallaCarga.setMouseTransparent(false);
		nodoPrincipalPantallaCarga.setVisible(true);
		if(segundos>0) {
			btCancelar.setVisible(false);
			new Thread(() -> {
				try {
					Thread.sleep(segundos*1000);
				} catch (InterruptedException e) {
					
				}finally {
					btCancelar.setVisible(true);
				}
				
			}).start();
			btCancelar.setOnAction(e -> {
				if(task!=null && task.isRunning()) {
					log.debug(".setCargando() - Carga cancelada");
					task.cancel();
				}
			});
		}else {
			btCancelar.setVisible(false);
			btCancelar.setOnAction(null);
		}
		
	}
	
	void quitarCargando() {
		
		log.debug(".quitarCargando() - Quitando pantalla de carga");
		
		panePrincipal.getChildren().get(0).setEffect(null);
		nodoPrincipalPantallaCarga.setMouseTransparent(true);
		nodoPrincipalPantallaCarga.setVisible(false);
		btCancelar.setOnAction(null);
	}
	
	private void setTitulo(Stage stage, Vistas v) {
		stage.setTitle(resourceBundle.getString("fxml.titulo." + v.getNombre()));
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
	}

}
