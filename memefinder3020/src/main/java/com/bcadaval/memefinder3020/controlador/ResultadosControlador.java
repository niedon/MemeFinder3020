package com.bcadaval.memefinder3020.controlador;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;

import com.bcadaval.memefinder3020.controlador.componentes.PaneEtiquetas;
import com.bcadaval.memefinder3020.controlador.componentes.PaneResultado;
import com.bcadaval.memefinder3020.modelo.beans.Categoria;
import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;
import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenBusqueda;
import com.bcadaval.memefinder3020.principal.Controlador;
import com.bcadaval.memefinder3020.principal.Vistas;
import com.bcadaval.memefinder3020.utils.Constantes;
import com.bcadaval.memefinder3020.utils.IOUtils;
import com.bcadaval.memefinder3020.vista.HBoxEtiqueta;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

@Controller
public class ResultadosControlador extends Controlador{
	
	static final String DATOS_IMAGENSELECCIONADA = "imagenSeleccionada";
	
	private Pageable pageable;
	private Page<Imagen> page;
	private ImagenBusqueda busqueda;
	private Imagen imgSeleccionada;
	
	private ObservableList<Categoria> listaCategorias;
	
	@FXML private Button btBuscar;
	@FXML private TextField tfNombre;
	@FXML private CheckBox cbSinCategoria;
	@FXML private ComboBox<Categoria> cbCategoria;
	@FXML private Button btLimpiarCategoria;
	@FXML private CheckBox cbDespuesDe;
	@FXML private CheckBox cbAntesDe;
	@FXML private TextField tfEtiquetas;
	@FXML private Button btAnadirEtiqueta;
	@FXML private DatePicker dpDespuesDe;
	@FXML private DatePicker dpAntesDe;
	@FXML private CheckBox cbSinEtiquetas;
	
	@FXML private AnchorPane apEtiquetasBusqueda;
	private PaneEtiquetas paneEtiquetasBusqueda;
	@FXML private Label lbNumResultados;

	@FXML private GridPane gpResultados;
	
	@FXML private Button btAnterior;
	@FXML private Label lbMarcador;
	@FXML private Button btSiguiente;
	
	@FXML private ImageView ivSeleccionada;
	@FXML private Label lbSeleccionada;
	@FXML private AnchorPane apSeleccionadaEtiquetas;
	private PaneEtiquetas paneEtiquetasSeleccionada;
	@FXML private Button btVolver;
	@FXML private Button btAmpliar;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		listaCategorias = FXCollections.observableArrayList();
		cbCategoria.setItems(listaCategorias);
		
		cbCategoria.setCellFactory(lv -> new ListCell<Categoria>() {
			@Override
			protected void updateItem(Categoria item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNombre());
			}
		});
		cbCategoria.setButtonCell(cbCategoria.getCellFactory().call(null));
		
		paneEtiquetasBusqueda = new PaneEtiquetas();
		paneEtiquetasBusqueda.setEventoEtiquetas(this::eliminarEtiqueta);
		apEtiquetasBusqueda.getChildren().add(paneEtiquetasBusqueda);
		AnchorPane.setTopAnchor(paneEtiquetasBusqueda, .0);
		AnchorPane.setRightAnchor(paneEtiquetasBusqueda, .0);
		AnchorPane.setBottomAnchor(paneEtiquetasBusqueda, .0);
		AnchorPane.setLeftAnchor(paneEtiquetasBusqueda, .0);
		
		paneEtiquetasSeleccionada = new PaneEtiquetas();
		apSeleccionadaEtiquetas.getChildren().add(paneEtiquetasSeleccionada);
		AnchorPane.setTopAnchor(paneEtiquetasSeleccionada, .0);
		AnchorPane.setRightAnchor(paneEtiquetasSeleccionada, .0);
		AnchorPane.setBottomAnchor(paneEtiquetasSeleccionada, .0);
		AnchorPane.setLeftAnchor(paneEtiquetasSeleccionada, .0);
		
		dpDespuesDe.disableProperty().bind(cbDespuesDe.selectedProperty().not());
		dpAntesDe.disableProperty().bind(cbAntesDe.selectedProperty().not());
		apEtiquetasBusqueda.disableProperty().bind(cbSinEtiquetas.selectedProperty());
		
		cbCategoria.disableProperty().bind(cbSinCategoria.selectedProperty());
		btLimpiarCategoria.disableProperty().bind(cbSinCategoria.selectedProperty());
		
	}

	@Override
	public void initComponentes() {
		setGraficos(btBuscar, Constantes.SVG_LUPA);
		setGraficos(btLimpiarCategoria, Constantes.SVG_PAPELERA);
		setGraficos(btAnadirEtiqueta, Constantes.SVG_PLUS);
		setGraficos(btAnterior, Constantes.SVG_FLECHAIZQUIERDA);
		setGraficos(btSiguiente, Constantes.SVG_FLECHADERECHA);
		setGraficos(btVolver, Constantes.SVG_EQUIS);
		setGraficos(btAmpliar, Constantes.SVG_LUPA);
	}

	@Override
	public void initVisionado() {
		
		switch (getVistaOrigen()) {
		case INICIO:
			limpiarCampos();
			tfNombre.setText((String)datos.get(InicioControlador.DATOS_TF_BUSQUEDA));
			rellenarBusqueda();
			break;

		case RESULTADOINDIVIDUAL:
			if((boolean)datos.get(ResultadoIndividualControlador.DATOS_HAYCAMBIOS)) {
				limpiarSeleccionada();
				rellenarBusqueda();
			}
			break;
			
		default:
			throw new RuntimeException("Pantalla no contemplada");
		}
		
	}

	@Override
	public void initFoco() {
		tfNombre.requestFocus();
	}
	
	@FXML
	private void btBuscar_click(ActionEvent event) {
		limpiarSeleccionada();
		rellenarBusqueda();
	}
	
	@FXML
	private void btLimpiarCategoria_click(ActionEvent event) {
		cbCategoria.valueProperty().set(null);
	}
	
	@FXML
	private void btAnadirEtiqueta_click(ActionEvent event) {
		
		String etiqueta = tfEtiquetas.getText().trim().toUpperCase();
		
		if(!etiqueta.isEmpty()) {
			paneEtiquetasBusqueda.anadirEtiqueta(servicioEtiqueta.countUsosDeEtiqueta(etiqueta), etiqueta);
		}
		
	}
	
	@FXML
	private void btAnterior_click(ActionEvent event) {
		pageable = page.previousPageable();
		realizarBusqueda();
	}
	
	@FXML
	private void btSiguiente_click(ActionEvent event) {
		pageable = page.nextPageable();
		realizarBusqueda();
	}
	
	@FXML
	private void btVolver_click(ActionEvent event) {
		gestorDeVentanas.cambiarEscena(Vistas.INICIO);
	}
	
	@FXML
	private void btAmpliar_click(ActionEvent event) {
		if(imgSeleccionada!=null) {
			datos.put(DATOS_IMAGENSELECCIONADA, imgSeleccionada);
			gestorDeVentanas.cambiarEscena(Vistas.RESULTADOINDIVIDUAL);
		}
	}
	
	//---------------------------------------------
	
	private void eliminarEtiqueta(ActionEvent ev) {
		HBoxEtiqueta origen = (HBoxEtiqueta) ((Button)ev.getSource()).getParent();
		paneEtiquetasBusqueda.borrarEtiqueta(origen);
	}
	
	private void limpiarCampos() {
		
		tfNombre.clear();
		cbDespuesDe.setSelected(false);
		cbAntesDe.setSelected(false);
		tfEtiquetas.clear();
		cbSinEtiquetas.setSelected(false);
		
		limpiarSeleccionada();
		
	}

	private void rellenarBusqueda() {
		
		pageable = PageRequest.of(0, 5, Direction.DESC, "id");
		busqueda = new ImagenBusqueda();
		
		String textoNombre = tfNombre.getText().trim().toUpperCase();
		if(!textoNombre.isEmpty()) {
			busqueda.setNombre(textoNombre);
		}
		
		busqueda.setBuscarSinCategoria(cbSinCategoria.isSelected());
		
		if(cbCategoria.getValue() != null) {
			busqueda.setCategoria(cbCategoria.getValue());
		}
		
		if(cbDespuesDe.isSelected()) {
			busqueda.setFechaDespues(dpDespuesDe.getValue().atStartOfDay());
		}
		
		if(cbAntesDe.isSelected()) {
			busqueda.setFechaAntes(dpAntesDe.getValue().atStartOfDay());
		}
		
		busqueda.setBuscarSinEtiquetas(cbSinEtiquetas.isSelected());
		ArrayList<Etiqueta> etiquetas = new ArrayList<Etiqueta>();
		for(HBoxEtiqueta et : paneEtiquetasBusqueda.getEtiquetas()) {
			etiquetas.add(servicioEtiqueta.getPorNombre(et.getNombre()));
		}
		busqueda.setEtiquetas(etiquetas);
		
		realizarBusqueda();
	}
	
	private void realizarBusqueda() {
		page = servicioImagen.getBusqueda(busqueda, pageable);
		refrescarInterfaz();
	}
	
	private void refrescarInterfaz() {
		
		listaCategorias.setAll(servicioCategoria.getAll());
		
		int num = (int)page.getTotalElements();
		lbNumResultados.setText(num + " resultado" + (num==1?"":"s"));
		
		gpResultados.getChildren().clear();
		
		for (int i=0; i<5; i++) {
			if(i<page.getContent().size()) {
				PaneResultado pr = new PaneResultado(page.getContent().get(i));
				pr.setOnMouseClicked(me -> {
					refrescarSeleccionada(GridPane.getRowIndex(pr));
				});
				gpResultados.add(pr, 0, i);
				
			}else {
				AnchorPane ap = new AnchorPane();
				ap.getStyleClass().add("paneResultado");
				gpResultados.add(ap, 0, i);
			}
		}
		
		btAnterior.setDisable(!page.hasPrevious());
		btSiguiente.setDisable(!page.hasNext());
		if(page.getTotalPages()==0) {
			lbMarcador.setText("0/0");
		}else {
			lbMarcador.setText((page.getNumber()+1) + "/" + page.getTotalPages());
		}
	}
	
	private void refrescarSeleccionada(int index) {
		
		if(index<0 || index>=page.getContent().size()) {
			limpiarSeleccionada();
			return;
		}
		
		imgSeleccionada = page.getContent().get(index);
		
		ivSeleccionada.setImage(new Image(IOUtils.getURLDeImagen(imgSeleccionada)));
		setFit(ivSeleccionada);
		
		if(imgSeleccionada.getNombre()==null) {
			lbSeleccionada.setText("[Sin nombre]");
		}else {
			lbSeleccionada.setText(imgSeleccionada.getNombre());
		}
		paneEtiquetasSeleccionada.borrarEtiquetas();
		for(Etiqueta et : imgSeleccionada.getEtiquetas()) {
			paneEtiquetasSeleccionada.anadirEtiqueta(servicioEtiqueta.countUsosDeEtiqueta(et), et.getNombre());
		}
		btAmpliar.setDisable(false);
		
	}
	
	private void limpiarSeleccionada() {
		ivSeleccionada.setImage(null);
		lbSeleccionada.setText("");
		paneEtiquetasSeleccionada.borrarEtiquetas();
		btAmpliar.setDisable(true);
	}

}
