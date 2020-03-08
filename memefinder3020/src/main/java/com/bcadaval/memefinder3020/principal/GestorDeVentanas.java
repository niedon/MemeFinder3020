package com.bcadaval.memefinder3020.principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcadaval.memefinder3020.controlador.AnadirImagenControlador;
import com.bcadaval.memefinder3020.controlador.Controlador;
import com.bcadaval.memefinder3020.controlador.InicioControlador;

import javafx.scene.Scene;

@Component
public class GestorDeVentanas{
	
	private Scene escena;
	
	@Autowired InicioControlador inicioControlador;
	@Autowired AnadirImagenControlador anadirImagenControlador;

	public void setEscena(Scene escena) {
		this.escena = escena;
	}
	
	public void cambiarEscena(Vistas v) {
		
		Controlador c = null;
		
		switch(v) {
		case INICIO:
			c = inicioControlador;
			break;
		case ANADIR_IMAGEN:
			c = anadirImagenControlador;
			break;
		default:
			throw new RuntimeException("Pantalla no encontrada");
		}
		
		escena.setRoot(c.getVista());
		
		c.initVisionado();
		c.initFoco();
		
		
	}

}
