package com.bcadaval.memefinder3020.controlador;

import java.net.URL;
import java.util.ResourceBundle;

import com.bcadaval.memefinder3020.principal.Controlador;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;


public class ControladorTempEscalado extends Controlador {

	public static final String SONIC = "https://eloutput.com/app/uploads-eloutput.com/2019/03/sonic-real-imagen-pelicula.jpg";
	public static final String LONGCAT = "https://i.kym-cdn.com/photos/images/facebook/001/295/524/cda.jpg";
	private static final double porcentajeZoom = 1.2;
	
	@FXML AnchorPane contenedor;
	@FXML ImageView view;
	
	private double escala;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		contenedor.setStyle("-fx-background-color: red;");
		
		Image imagen = new Image(SONIC);
		double ancho = imagen.getWidth();
		double alto = imagen.getHeight();
		
		view.setImage(imagen);
		
		
		
		view.setViewport(viewportInicio(view, contenedor));
		view.setOnMouseClicked(e -> {
			System.out.println("contenedor: " + contenedor.getHeight());
			System.out.println("iv: " + view.getFitHeight());
		});
		
		view.setOnScroll(e -> {
			
			escala *= (e.getDeltaY()<0 ? porcentajeZoom : 1/porcentajeZoom);
			
			//Nuevas dimensiones del viewport
			double nuevaAnchura = contenedor.getPrefWidth() * escala;
			double nuevaAltura = contenedor.getPrefHeight() * escala;
			
			//System.out.println("e.getXY: " + e.getX() + "x"+e.getY());
			
			//Índice del puntero sobre el viewport
			double indiceX = e.getX()/view.getFitWidth();
			double indiceY = e.getY()/view.getFitHeight();
			
			//Píxel del puntero sobre la imagen
			double xEnImagen = view.getViewport().getMinX() + (view.getViewport().getWidth()*indiceX);
			double yEnImagen = view.getViewport().getMinY() + (view.getViewport().getHeight()*indiceY);
			System.out.println("en imagen: " + xEnImagen+" x "+yEnImagen);
			//Origen del nuevo viewport sobre la imagen
			double nuevoOX = xEnImagen - (nuevaAnchura * indiceX);
			double nuevoOY = yEnImagen - (nuevaAltura * indiceY);
			
			
			if(nuevaAnchura > view.getImage().getWidth()) {
				nuevoOX = -((nuevaAnchura-view.getImage().getWidth())/2);
			}else {
				
				if(nuevoOX<0) {
					nuevoOX = 0;
				}
				if(nuevoOX+nuevaAnchura>view.getImage().getWidth()) {
					nuevoOX = view.getImage().getWidth() - nuevaAnchura;
				}
				
			}
			
			if(nuevaAltura > view.getImage().getHeight()) {
				nuevoOY = -((nuevaAltura-view.getImage().getHeight())/2);
			}else {
				
				if(nuevoOY<0) {
					nuevoOY = 0;
				}
				if(nuevoOY+nuevaAltura>view.getImage().getHeight()) {
					nuevoOY = view.getImage().getHeight() - nuevaAltura;
				}
			}
			
			view.setViewport(new Rectangle2D(nuevoOX, nuevoOY, nuevaAnchura, nuevaAltura));

		});
	}
	

	private Rectangle2D centrarViewportPeque(ImageView iv, Rectangle2D r2d) {
		
		double origenX = r2d.getMinX();
		double origenY = r2d.getMinY();
		
		if(iv.getImage().getWidth()<r2d.getWidth()) {
			origenX = -(r2d.getWidth() - iv.getImage().getWidth())/2;
			System.out.println("origenX: " + origenX);
		}
		
		if(iv.getImage().getHeight()<r2d.getHeight()) {
			origenY = -(r2d.getHeight() - iv.getImage().getHeight())/2;
			System.out.println("origenY: " + origenY);
		}
		
		return new Rectangle2D(origenX, origenY, r2d.getWidth(),r2d.getHeight());
		
	}
	
	private Rectangle2D viewportInicio(ImageView iv, Pane p) {

		Image im = iv.getImage();
		
		escala = Math.max(1, Math.max(im.getWidth()/p.getPrefWidth(), im.getHeight()/p.getPrefHeight()));
		
		double oX = 0;
		double oY = 0;
		
		if(p.getPrefWidth()*escala > im.getWidth()) oX = -(((p.getPrefWidth()*escala) - im.getWidth())/2);
		if(p.getPrefHeight()*escala > im.getHeight()) oY = -(((p.getPrefHeight()*escala) - im.getHeight())/2);
		
		return new Rectangle2D(oX, oY, p.getPrefWidth()*escala, p.getPrefHeight()*escala);
		
		
		
		
	}


	@Override
	public void initComponentes() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void initVisionado() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void initFoco() {
		// TODO Auto-generated method stub
		
	}
	
	
}
