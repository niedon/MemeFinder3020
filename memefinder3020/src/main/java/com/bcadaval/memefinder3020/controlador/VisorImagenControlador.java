package com.bcadaval.memefinder3020.controlador;

import java.net.URL;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.bcadaval.memefinder3020.principal.Controlador;
import com.bcadaval.memefinder3020.utils.Constantes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

@Controller
public class VisorImagenControlador extends Controlador {
	
	static final String DATOS_IMAGENVISIONADO = "imagenVisionado";
	
	private Image img;
	
	private static final double porcentajeZoom = 1.2;
	
	private double pulsacionX, pulsacionY;
	private DoubleProperty escala;
	
	@FXML AnchorPane contenedor;
	@FXML ImageView view;
	
	@FXML VBox vbInterfaz;
	@FXML Label lbEscala;
	@FXML Button btMenos;
	@FXML Button btAjustar;
	@FXML Button btMas;
	
	@SuppressWarnings("serial")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		escala = new SimpleDoubleProperty(1);
		
		lbEscala.textProperty().bindBidirectional(escala, new NumberFormat() {
			@Override
			public Number parse(String source, ParsePosition parsePosition) {
				return null;
			}
			@Override
			public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
				return format(number,toAppendTo,pos);
			}
			@Override
			public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
				return new StringBuffer((int)(100/number) + " %");
			}
		});
		
		//Se delega el scroll del VBox al ImageView (equivale porque ocupa el mismo espacio)
		vbInterfaz.setOnScroll(e -> scrollImagen(e.getDeltaY()>0, e.getX(), e.getY()));
		//Primer valor del puntero al arrastrar
		vbInterfaz.setOnMousePressed(e -> {
			pulsacionX = e.getX();
			pulsacionY = e.getY();
		});
		vbInterfaz.setOnMouseDragged(this::eventoDrag);
		
		//El fit del imageview tiene que ser del tamaño del contenedor
		view.fitWidthProperty().bind(contenedor.widthProperty());
		view.fitHeightProperty().bind(contenedor.heightProperty());
		
		//Estos eventos son necesarios  
		contenedor.widthProperty().addListener((obs,viejo,nuevo) -> {
			view.setViewport(viewportInicio(view, contenedor));
		});
		contenedor.heightProperty().addListener((obs,viejo,nuevo) -> {
			view.setViewport(viewportInicio(view, contenedor));
		});
		
	}

	@Override
	public void initComponentes() {
		setGraficos(btMenos, Constantes.SVG_MENOS);
		setGraficos(btMas, Constantes.SVG_PLUS);
	}

	@Override
	public void initVisionado() {
		
		img = (Image) datos.get(DATOS_IMAGENVISIONADO);
		
		view.setImage(img);
		view.setViewport(viewportInicio(view, contenedor));
		gestionarBotonCentral();
		
	}

	@Override
	public void initFoco() {
	}
	
	@FXML
	private void btMenos_click(ActionEvent e) {
		scrollImagen(false, contenedor.getWidth()/2, contenedor.getHeight()/2);
	}
	
	@FXML
	private void btMas_click(ActionEvent e) {
		scrollImagen(true, contenedor.getWidth()/2, contenedor.getHeight()/2);
	}
	
	//----------------------IMAGEN---------------------
	
	private Rectangle2D viewportInicio(ImageView iv, Pane p) {

		Image im = iv.getImage();
		
		escala.set(Math.max(1, Math.max(im.getWidth()/p.getWidth(), im.getHeight()/p.getHeight())));
		
		double oX = 0;
		double oY = 0;
		
		if(p.getWidth()*escala.get() > im.getWidth()) oX = -(((p.getWidth()*escala.get()) - im.getWidth())/2);
		if(p.getHeight()*escala.get() > im.getHeight()) oY = -(((p.getHeight()*escala.get()) - im.getHeight())/2);
		
		return new Rectangle2D(oX, oY, p.getWidth()*escala.get(), p.getHeight()*escala.get());
	}
	
	//-----------------------EVENTOS-------------------
	
	private void scrollImagen(boolean zoomAumentar, double xPuntero, double yPuntero) {
		
		escala.set(escala.get() * (zoomAumentar ? 1/porcentajeZoom : porcentajeZoom));
		
		//Nuevas dimensiones del viewport
		double nuevaAnchura = contenedor.getWidth() * escala.get();
		double nuevaAltura = contenedor.getHeight() * escala.get();
		
		//Índice del puntero sobre el viewport
		double indiceX = xPuntero/view.getFitWidth();
		double indiceY = yPuntero/view.getFitHeight();
		
		//Píxel del puntero sobre la imagen
		double xEnImagen = view.getViewport().getMinX() + (view.getViewport().getWidth()*indiceX);
		double yEnImagen = view.getViewport().getMinY() + (view.getViewport().getHeight()*indiceY);
		
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
		
		gestionarBotonCentral();
	}
	
	private void eventoDrag(MouseEvent e) {
		
		Rectangle2D vp = view.getViewport();
		
		//Distancia recorrida por el scroll
		double offsetX = (e.getX() - pulsacionX)*escala.get();
		double offsetY = (e.getY() - pulsacionY)*escala.get();
		
		//Nuevo origen del viewport
		double vpX = vp.getMinX()-offsetX;
		double vpY = vp.getMinY()-offsetY;
		
		/* EJE X */
		
		// Si el viewport es más pequeño que la imagen (imagen ampliada)
		if(vp.getWidth()<view.getImage().getWidth()) {
			
			//Si sobresale por la izquierda
			if(vpX<0) {
				vpX=0;
			//Si sobresale por la derecha
			}else if(vp.getWidth() > view.getImage().getWidth()-vpX) {
				vpX = view.getImage().getWidth() - vp.getWidth();
			}
			
		// Si el viewport es más grande que la imagen (imagen reducida)
		}else {
			
			//Si sobresale por la izquierda
			if(vpX>0) {
				vpX = 0;
			//Si sobresale por la derecha
			}else if(vp.getWidth() < Math.abs(vpX)+view.getImage().getWidth()) {
				vpX = - (vp.getWidth() - view.getImage().getWidth());
			}
		}
		
		/* EJE Y */
		
		// Todo igual que el anterior pero con Y
		if(vp.getHeight()<view.getImage().getHeight()) {
			if(vpY<0) {
				vpY=0;
			}else if(vp.getHeight() > view.getImage().getHeight()-vpY) {
				vpY = view.getImage().getHeight() - vp.getHeight();
			}
		}else {
			if(vpY>0) {
				vpY = 0;
			}else if(vp.getHeight() < Math.abs(vpY)+view.getImage().getHeight()) {
				vpY = - (vp.getHeight() - view.getImage().getHeight());
			}
		}
		
		view.setViewport(new Rectangle2D(vpX, vpY, vp.getWidth(), vp.getHeight()));
		
		pulsacionX = e.getX();
		pulsacionY = e.getY();
		
	}
	
	private void gestionarBotonCentral() {
		Image img = view.getImage();
		Rectangle2D vp = view.getViewport();
		
		if(img.getWidth()<contenedor.getWidth() && img.getHeight()<contenedor.getHeight()) {
			
			if(vp.getWidth()==contenedor.getWidth()) {
				btAjustar.setDisable(true);
				btAjustar.setOnAction(null);
			}else {
				btAjustar.setDisable(false);
				setGraficos(btAjustar, Constantes.SVG_IMAGENFIT);
				btAjustar.setOnAction(e -> {
					view.setViewport(viewportInicio(view, contenedor));
					gestionarBotonCentral();
				});
			}
			
		}else {
			
			if(vp.getWidth()==img.getWidth()) {
				setGraficos(btAjustar, Constantes.SVG_IMAGENESCALA);
				btAjustar.setOnAction(e -> {
					double oX = (img.getWidth()-contenedor.getWidth())/2;
					double oY = (img.getHeight()-contenedor.getHeight())/2;
					view.setViewport(new Rectangle2D(oX, oY, contenedor.getWidth(), contenedor.getHeight()));
					gestionarBotonCentral();
				});
			}else {
				setGraficos(btAjustar, Constantes.SVG_IMAGENFIT);
				btAjustar.setOnAction(e -> {
					view.setViewport(viewportInicio(view, contenedor));
					gestionarBotonCentral();
				});
			}
			
		}
		
	}

}
