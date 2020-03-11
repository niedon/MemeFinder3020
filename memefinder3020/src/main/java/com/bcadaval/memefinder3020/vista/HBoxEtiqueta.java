package com.bcadaval.memefinder3020.vista;

import org.springframework.lang.NonNull;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class HBoxEtiqueta extends HBox {

	private int numVeces;
	private String etiqueta;
	
	private Label labelContador;
	
	public HBoxEtiqueta(int numVeces, String etiqueta) {
		this(numVeces,etiqueta,false,null);
	}
	
	public HBoxEtiqueta(int numVeces, @NonNull String etiqueta, @NonNull EventHandler<ActionEvent> eventoDelBoton) {
		this(numVeces,etiqueta,true,eventoDelBoton);
	}
	
	private HBoxEtiqueta(int numVeces, @NonNull String etiqueta, boolean eliminable, EventHandler<ActionEvent> eventoDelBoton) {
		super();
		this.numVeces = numVeces;
		this.etiqueta = etiqueta;
		
		ObservableList<Node> hijos = this.getChildren();
		
		labelContador = new Label("("+numVeces+")");
		
		hijos.add(labelContador);
		hijos.add(new Label(etiqueta));
		
		Button b = new Button("X");
		b.setOnAction(eventoDelBoton);
		hijos.add(b);
	}
	
	public void cambiaNum(int num) {
		labelContador.setText("("+num+")");
	}
	
	public int getNumVeces() {
		return numVeces;
	}
	
	public String getNombre() {
		return etiqueta;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof HBoxEtiqueta))
			return false;
		HBoxEtiqueta other = (HBoxEtiqueta) obj;
		if (etiqueta == null) {
			if (other.etiqueta != null)
				return false;
		} else if (!etiqueta.equals(other.etiqueta))
			return false;
		return true;
	}
	
}
