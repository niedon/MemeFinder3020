package com.bcadaval.memefinder3020.principal;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javafx.scene.Scene;

@Component
public class GestorDeVentanas implements ApplicationContextAware{
	
	private ApplicationContext ctx;
	
	private Scene escena;
	
	private Controlador ultimaClase;

	public void setEscena(Scene escena) {
		this.escena = escena;
	}
	
	public void cambiarEscena(Vistas v) {
		
		Controlador c = ctx.getBean(v.getClaseControlador());
		
		escena.setRoot(c.getVista());
		
		if(ultimaClase != null) c.anadirClaseAMapa(ultimaClase.getClass());
		ultimaClase = c;
		
		c.initVisionado();
		c.initFoco();
		c.limpiarMapa();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
		
	}

}
