package com.bcadaval.memefinder3020.concurrencia;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.bcadaval.memefinder3020.Main;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioImagen;
import com.bcadaval.memefinder3020.principal.GestorDeVentanas;
import com.bcadaval.memefinder3020.principal.SpringFxmlLoader;
import com.bcadaval.memefinder3020.utils.Constantes;

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class TaskSplash extends Task<Void> {
	
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
		
		updateMessage("Instalando lector de SVG");
		updateProgress(++tareasActuales, tareasMax);
		SvgImageLoaderFactory.install();
		
		updateMessage("Comprobando consistencia de BD");
		updateProgress(++tareasActuales, tareasMax);
		comprobarConsistencia();
		
		updateMessage("Cargando vistas");
		updateProgress(++tareasActuales, tareasMax);
		ctx.getBean(SpringFxmlLoader.class).cargaVistas();
		
		updateMessage("Carga finalizada");
		updateProgress(++tareasActuales, tareasMax);
		Platform.runLater(() -> {
			ctx.getBean(GestorDeVentanas.class).iniciar(stagePrograma);
		}); 
		
		return null;
	}
	
	private void comprobarConsistencia() {
		
		ServicioImagen servicioImagen = ctx.getBean(ServicioImagen.class);
		List<Integer> listaIdImagen = servicioImagen.getAllIds();
		
		File carpetaImagenes = Constantes.RUTA_IMAGENES_AC.toFile();
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
		System.out.println(listaIdImagen);
		if(!listaIdImagen.isEmpty()) {
			listaIdImagen.forEach(e -> servicioImagen.borrarPorId(e));
		}
		
	}

	@Override
	protected void failed() {
		super.failed();
		Platform.runLater(() -> {
			new Alert(AlertType.ERROR, "Ha habido un error en la carga del programa", ButtonType.CLOSE).showAndWait();
			exceptionProperty().getValue().printStackTrace();
			stagePrograma.close();
			stageSplash.close();
		});
	}

	@Override
	protected void succeeded() {
		super.succeeded();
		Platform.runLater(() -> {
			stagePrograma.show();
			stageSplash.close();
		});
	}

}
