package com.bcadaval.memefinder3020.principal;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bcadaval.memefinder3020.excepciones.GUIException;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioCategoria;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioEtiqueta;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioImagen;
import com.bcadaval.memefinder3020.utils.Constantes;
import com.bcadaval.memefinder3020.utils.RutasUtils;

import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public abstract class Controlador implements Initializable{
	
	protected static final Map<String,Object> datos = new HashMap<String,Object>();
	static final String VISTA_ORIGEN = "vistaOrigen";
	
	@Autowired protected RutasUtils rutasUtils;
	
	@Autowired protected GestorDeVentanas gestorDeVentanas;
	
	@Autowired protected ServicioImagen servicioImagen;
	@Autowired protected ServicioEtiqueta servicioEtiqueta;
	@Autowired protected ServicioCategoria servicioCategoria;
	
	private Parent vista;
	private Vistas vistaPadre;
	
	public abstract void initVisionado();
	public abstract void initFoco();

	Parent getVista() {
		return vista;
	}
	
	Stage getStage() {
		return (Stage)vista.getScene().getWindow();
	}
	
	Vistas getVistaPadre() {
		return vistaPadre;
	}
	
	void setVistaPadre(Vistas vistaPadre) {
		this.vistaPadre = vistaPadre;
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
	
    protected void setGraficos(Labeled elemento, String constSvg){
        
        InputStream stream = this.getClass().getResourceAsStream(String.format(Constantes.RUTA_SVG_RFE, constSvg));
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
    
    protected void cambiarEscena(Vistas v){
    	try {
			gestorDeVentanas.cambiarEscena(v);
		} catch (GUIException e) {
			//TODO completar y log
			new Alert(AlertType.ERROR, e.getMensaje(), ButtonType.OK);
			limpiarMapa();
		}
    }
    
    protected final void onClose() {
    	gestorDeVentanas.onClose();
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
