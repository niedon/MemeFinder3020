package com.bcadaval.memefinder3020.principal;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.bcadaval.memefinder3020.utils.Constantes;

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
	
	@FXML private AnchorPane nodoPrincipalPantallaCarga;
	@FXML private Button btCancelar;
	
	private ApplicationContext ctx;
	
	private Stage stage;
	private StackPane panePrincipal;
	
	private Stage stageVisor;
	
	private Vistas ultimaVista;

	public Stage getStage() {
		return stage;
	}
	
	public void cambiarEscena(Vistas v) {
		
		Controlador c = ctx.getBean(v.getClaseControlador());
		
		panePrincipal.getChildren().set(0, c.getVista());
		//TODO embutir vista de pantalla de carga para la primera pantalla que aparezca
		if(ultimaVista != null) c.anadirVistaAMapa(ultimaVista);
		ultimaVista = v;
		c.initComponentes();
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
		escenaPrincipal.getStylesheets().add(getClass().getResource(String.format(Constantes.RUTA_CSS, "principal")).toExternalForm());
		
		stage.setScene(escenaPrincipal);
		cambiarEscena(Vistas.INICIO);
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
	
	public void mostrarVisorImagen() {
		
		Controlador c = ctx.getBean(Vistas.VISORIMAGEN.getClaseControlador());
		
		if(stageVisor==null) {
			stageVisor = new Stage();
			
			stageVisor.initOwner(stage);
			stageVisor.initModality(Modality.APPLICATION_MODAL);
			
			stageVisor.setMaximized(true);
			stageVisor.setScene(new Scene(c.getVista()));
			c.initComponentes();
			
		}
		c.initVisionado();
		stageVisor.showAndWait();
		
	}

}
