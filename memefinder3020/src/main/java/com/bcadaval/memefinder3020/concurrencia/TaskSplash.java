package com.bcadaval.memefinder3020.concurrencia;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.bcadaval.memefinder3020.Main;
import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioImagen;
import com.bcadaval.memefinder3020.principal.GestorDeVentanas;
import com.bcadaval.memefinder3020.principal.SpringFxmlLoader;
import com.bcadaval.memefinder3020.utils.RutasUtils;

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class TaskSplash extends Task<Void> {
	
	private static final Logger log = LogManager.getLogger(TaskSplash.class);
	
	private ApplicationContext ctx;
	private Stage stagePrograma, stageSplash;
	
	public TaskSplash(Stage stagePrograma, Stage stageSplash) {
		this.stagePrograma = stagePrograma;
		this.stageSplash = stageSplash;
	}

	@Override
	protected Void call() throws Exception {
		int tareasActuales = 0;
		int tareasMax = 5;
		
		updateMessage("Iniciando carga de Spring");
		updateProgress(++tareasActuales, tareasMax);
		ctx = SpringApplication.run(Main.class);
		
		log.debug(".call() - Finalizada carga de Spring");
		log.debug(".call() - Iniciando instalación del lector de SVG");
		
		updateMessage("Instalando lector de SVG");
		updateProgress(++tareasActuales, tareasMax);
		SvgImageLoaderFactory.install();
		
		log.debug(".call() - Finalizada instalación del lector de SVG");
		log.debug(".call() - Iniciando comprobación de consistencia de la BD");
		
		updateMessage("Comprobando consistencia de BD");
		updateProgress(++tareasActuales, tareasMax);
		comprobarConsistencia();
		
		log.debug(".call() - Finalizada comprobación de BD");
		log.debug(".call() - Iniciando carga de vistas");
		
		updateMessage("Cargando vistas");
		updateProgress(++tareasActuales, tareasMax);
		ctx.getBean(SpringFxmlLoader.class).cargaVistas();
		
		log.debug(".call() - Finalizada carga de vistas");
		log.debug(".call() - Iniciando visualización de la primera ventana");
		
		updateMessage("Carga finalizada");
		updateProgress(++tareasActuales, tareasMax);
		Platform.runLater(() -> {
			ctx.getBean(GestorDeVentanas.class).iniciar(stagePrograma);
		});
		
		log.debug(".call() - Carga finalizada");
		
		return null;
	}
	
	private void comprobarConsistencia() {
		//TODO reestructurar
		ServicioImagen servicioImagen = ctx.getBean(ServicioImagen.class);
		List<Integer> listaIdImagen = servicioImagen.getAllIds();
		
		File carpetaImagenes = new File(ctx.getBean(RutasUtils.class).RUTA_IMAGENES_AC);
		if(!carpetaImagenes.exists()) {
			return;
		}
		Set<File> listaArchivos = new HashSet<File>(Arrays.asList(carpetaImagenes.listFiles()));
		
		listaArchivos.removeIf(p -> {
			
			if(p.getName().indexOf('.')<1) {
				return true;
			}
			
			String nombre = p.getName().substring(0, p.getName().indexOf('.'));
			
			for(Integer i : listaIdImagen) {
				
				if(nombre.equals(i.intValue()+"")) {
					listaIdImagen.remove(i);
					return true;
				}
			}
			
			return false;
			
		});
		
		if(!listaArchivos.isEmpty()) {
			for (File f : listaArchivos) {
				f.delete();
			}
		}
		
		if(!listaIdImagen.isEmpty()) {
			listaIdImagen.forEach(e -> {
				try {
					servicioImagen.eliminar(e);
				} catch (ConstraintViolationException e1) {
					//TODO log
				}
			});
		}
		
	}

	@Override
	protected void failed() {
		super.failed();
		Platform.runLater(() -> {
			Alert alerta = new Alert(AlertType.ERROR);
			alerta.setTitle("Fallo en el programa");
			alerta.setHeaderText("Ha habido un error en la carga del programa:");
			alerta.setContentText(exceptionProperty().getValue().getMessage());
			alerta.showAndWait();
			log.error(".failed() - Fallo en la carga del programa", exceptionProperty().getValue());
			
			stagePrograma.close();
			stageSplash.close();
		});
	}

	@Override
	protected void succeeded() {
		super.succeeded();
		Platform.runLater(() -> {
			log.info("======================================");
			log.info("=           MemeFinder3020           =");
			log.info("======================================");
			stagePrograma.show();
			stageSplash.close();
		});
	}

}
