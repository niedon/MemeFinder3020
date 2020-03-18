package com.bcadaval.memefinder3020.principal;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bcadaval.memefinder3020.modelo.servicios.ServicioCategoria;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioEtiqueta;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioImagen;

import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class Controlador implements Initializable{
	
	protected static final Map<String,Object> datos = new HashMap<String,Object>();
	static final String VISTA_ORIGEN = "vistaOrigen";
	
	@Autowired protected GestorDeVentanas gestorDeVentanas;
	
	@Autowired protected ServicioImagen servicioImagen;
	@Autowired protected ServicioEtiqueta servicioEtiqueta;
	@Autowired protected ServicioCategoria servicioCategoria;
	
	private Parent vista;
	
	public abstract void initComponentes();
	public abstract void initVisionado();
	public abstract void initFoco();

	public Parent getVista() {
		return vista;
	}

	void setVista(Parent vista) {
		this.vista = vista;
	}
	
	void limpiarMapa() {
		datos.clear();
	}
	
	void anadirVistaAMapa(Vistas v) {
		datos.put(VISTA_ORIGEN, v);
	}
	
	protected Vistas getVistaOrigen() {
		return (Vistas) datos.get(VISTA_ORIGEN);
	}
	
	protected void setFit(ImageView iv) {
		Pane parent = (Pane) iv.getParent();
		if(parent.getPrefWidth()>iv.getImage().getWidth() && parent.getPrefHeight()>iv.getImage().getHeight()) {
			iv.setFitWidth(iv.getImage().getWidth());
			iv.setFitHeight(iv.getImage().getHeight());
		}else {
			iv.setFitWidth(parent.getPrefWidth());
			iv.setFitHeight(parent.getPrefHeight());
		}
	}
	
    protected void setGraficos(Labeled elemento, String ruta){
        
        InputStream stream = this.getClass().getResourceAsStream(ruta);
        if(stream!=null) {
            
            double ancho = elemento.getPrefWidth()*0.7;
            double alto = elemento.getPrefHeight()*0.7;
            
            elemento.setGraphic(new ImageView(new Image(stream,
                    Math.min(alto, ancho),
                    Math.min(alto, ancho),
                    true,
                    true)));
        }
        
    }
	
	//--------------Concurrencia--------------
	
	protected void comenzarTarea(Task<?> t, int segundos) {
		
		t.stateProperty().addListener((obs, viejo, nuevo) -> {
			
			switch(nuevo) {
			case RUNNING:
				gestorDeVentanas.setCargando(t, segundos);
				break;
				
			case SUCCEEDED:
			case FAILED:
			case CANCELLED:
				gestorDeVentanas.quitarCargando();
				break;
			
			default:
				break;
			}
			
		});
		
		new Thread(t).start();
		
	}

}
