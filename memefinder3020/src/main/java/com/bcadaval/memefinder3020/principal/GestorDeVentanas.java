package com.bcadaval.memefinder3020.principal;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.bcadaval.memefinder3020.excepciones.GUIException;
import com.bcadaval.memefinder3020.utils.Constantes;

import javafx.application.Platform;
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
		
		//Se evita cambiar de una vista modal a una normal (hay que usar onClose)
		if(vistaActual.esModal() && !v.esModal()) {
			throw new GUIException("No se puede cambiar a una vista normal desde una modal");
		}
		
		//Se evita cambiar a la misma vista
		if(v==vistaActual) {
			throw new GUIException("No se puede cambiar a la misma vista");
		}
		
		//Se evita mostrar una vista más de una vez en una pila de vistas
		Controlador c = ctx.getBean(vistaActual.getClaseControlador());
		while(c.getVistaPadre() != null) {
			if(c.getVistaPadre()==v) {
				throw new GUIException(String.format("La vista %s ya está en la pila de vistas", v.getNombre()));
			}
			c = ctx.getBean(c.getVistaPadre().getClaseControlador());
		}
		
		Controlador cNuevaVista = ctx.getBean(v.getClaseControlador());
		if(v.esModal()) {
			
			cNuevaVista.setVistaPadre(vistaActual);
			
			if(cNuevaVista.getStage().getOwner()==null) {
				cNuevaVista.getStage().initOwner(ctx.getBean(vistaActual.getClaseControlador()).getStage());
				cNuevaVista.getStage().initModality(Modality.WINDOW_MODAL);
			}
			
			cNuevaVista.getStage().show();
			
		}else {
			panePrincipal.getChildren().set(0, cNuevaVista.getVista());
		}
		
		eventosControlador(cNuevaVista, v);
	}
	
	void onClose() {
		
		Controlador cActual = ctx.getBean(vistaActual.getClaseControlador());
		
		if(cActual.getVistaPadre()==null) {
			Platform.exit();
		}else {
			
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
		mostrarPrimeraPantalla();
		quitarCargando();
		
	}
	
	void setCargando(Task<?> task, int segundos) {
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
					task.cancel();
				}
			});
		}else {
			btCancelar.setVisible(false);
			btCancelar.setOnAction(null);
		}
		
	}
	
	void quitarCargando() {
		panePrincipal.getChildren().get(0).setEffect(null);
		nodoPrincipalPantallaCarga.setMouseTransparent(true);
		nodoPrincipalPantallaCarga.setVisible(false);
		btCancelar.setOnAction(null);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
	}

}
