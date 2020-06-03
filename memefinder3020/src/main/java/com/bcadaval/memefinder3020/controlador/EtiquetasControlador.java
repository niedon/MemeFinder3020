package com.bcadaval.memefinder3020.controlador;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;

import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;
import com.bcadaval.memefinder3020.modelo.beans.temp.CategoriaEtiquetaBusqueda;
import com.bcadaval.memefinder3020.principal.Controlador;
import com.bcadaval.memefinder3020.utils.Constantes;
import com.bcadaval.memefinder3020.utils.enumerados.Comparador;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;

@Controller
public class EtiquetasControlador extends Controlador{

	private static final Logger log = LogManager.getLogger(EtiquetasControlador.class);
	
	private ObservableList<Etiqueta> resultados;

	@FXML private TextField tfNombre;
	@FXML private Button btBuscar;
	@FXML private CheckBox cbImagenes;
	@FXML private Spinner<Integer> spNumero;
	private ToggleGroup grupoRadio;
	@FXML private RadioButton rbMas;
	@FXML private RadioButton rbMenos;
	@FXML private RadioButton rbIgual;
	@FXML private TableView<Etiqueta> tvEtiquetas;
	@FXML private TableColumn<Etiqueta, String> tcEtiqueta;
	@FXML private Label lbResultados;
	@FXML private Button btRenombrar;
	@FXML private Button btFusionar;
	@FXML private Button btEliminar;
	@FXML private Button btVaciar;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		resultados = FXCollections.observableArrayList();
		
		grupoRadio = new ToggleGroup();
		rbMas.setToggleGroup(grupoRadio);
		rbMenos.setToggleGroup(grupoRadio);
		rbIgual.setToggleGroup(grupoRadio);
		grupoRadio.selectToggle(rbMas);
		
		spNumero.disableProperty().bind(cbImagenes.selectedProperty().not());
		rbMas.disableProperty().bind(cbImagenes.selectedProperty().not());
		rbMenos.disableProperty().bind(cbImagenes.selectedProperty().not());
		rbIgual.disableProperty().bind(cbImagenes.selectedProperty().not());
		
		lbResultados.textProperty().bind(Bindings.format("%d resultado/s",
				Bindings.size(resultados)));
		
		
		tcEtiqueta.setCellValueFactory(el -> {
			
			String caena = String.format("%s (%d %s)",
            		el.getValue().getNombre(),
            		el.getValue().getCountImagenes(),
            		el.getValue().getCountImagenes()==1 ? "imagen" : "imágenes");
		
			return new SimpleStringProperty(caena);
			
		});
		tvEtiquetas.setItems(resultados);
		tvEtiquetas.setPlaceholder(new Label("No hay resultados"));
		tvEtiquetas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		spNumero.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
		
		spNumero.getValueFactory().setValue(1);

		//Bindings botones
		ObservableList<Etiqueta> seleccionTvCategorias = tvEtiquetas.getSelectionModel().getSelectedItems();
		
		btRenombrar.disableProperty().bind(Bindings.size(seleccionTvCategorias).isNotEqualTo(1));
		btEliminar.disableProperty().bind(Bindings.size(seleccionTvCategorias).isNotEqualTo(1));
		btVaciar.disableProperty().bind(Bindings.size(seleccionTvCategorias).isNotEqualTo(1));
		btFusionar.disableProperty().bind(Bindings.size(seleccionTvCategorias).lessThan(2));
		
		//Gráficos de botones
		setGraficos(btBuscar, Constantes.SVG_LUPA);
		setGraficos(btEliminar, Constantes.SVG_EQUIS);
		setGraficos(btVaciar, Constantes.SVG_PAPELERA);
		setGraficos(btRenombrar, Constantes.SVG_LAPIZ);
		setGraficos(btFusionar, Constantes.SVG_UNIR);
		
	}

	@Override
	public void initVisionado() {
		
		log.debug(".initVisionado() - Iniciando visionado");
		
		limpiarCampos();
		hacerBusqueda();
		
	}

	@Override
	public void initFoco() {
		tfNombre.requestFocus();
	}
	
	//----------------------------------------------------
	
	@FXML
	private void btBuscar_click(ActionEvent event) {
		hacerBusqueda();
	}
	
	@FXML
	private void btEliminar_click(ActionEvent event) {
		Etiqueta et = tvEtiquetas.getSelectionModel().getSelectedItem();
		
		Optional<ButtonType> respuesta = new Alert(AlertType.CONFIRMATION, String.format("¿Borrar la etiqueta %s?", et.getNombre()), ButtonType.YES, ButtonType.NO).showAndWait();
		if(respuesta.isPresent() && respuesta.get()==ButtonType.YES) {
			
			servicioEtiqueta.eliminar(et);
			new Alert(AlertType.INFORMATION, "Se ha borrado la etiqueta", ButtonType.OK).showAndWait();
			hacerBusqueda();
			
		}
		
	}
	
	@FXML
	private void btVaciar_click(ActionEvent event) {
		Etiqueta et = tvEtiquetas.getSelectionModel().getSelectedItem();
		
		if(et.getCountImagenes()<1) {
			return;
		}
	
		Optional<ButtonType> respuesta = new Alert(AlertType.CONFIRMATION, String.format("¿Vaciar la etiqueta %s?", et.getNombre()), ButtonType.YES, ButtonType.NO).showAndWait();
		
		if(respuesta.isPresent() && respuesta.get()==ButtonType.YES) {
			Long imagenes = et.getCountImagenes();
			Optional<ButtonType> confirmacion = new Alert(AlertType.WARNING, String.format("Se eliminará%c %d %s, ¿confirmar?",
					imagenes==1 ? 0 : 'n',
					imagenes,
					imagenes==1 ? "imagen" : "imágenes"),
					ButtonType.YES, ButtonType.NO).showAndWait();
			
			if(confirmacion.isPresent() && confirmacion.get()==ButtonType.YES) {
				
				try {
					servicioImagen.borrarPorEtiqueta(et);
					new Alert(AlertType.INFORMATION, "La etiqueta se ha vaciado", ButtonType.OK).showAndWait();
					hacerBusqueda();
				} catch (ConstraintViolationException e) {
					new Alert(AlertType.ERROR,String.format("No se ha podido vaciar la etiqueta: %s", e.getMensaje()),ButtonType.OK).showAndWait();
				}
				
			}
			
		}
		
	}
	
	@FXML
	private void btRenombrar_click(ActionEvent event) {
		
		Etiqueta et = tvEtiquetas.getSelectionModel().getSelectedItem();
		
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Renombrar etiqueta");
		dialog.setHeaderText("Elige el nuevo nombre de la etiqueta");
		
		Optional<String> resultado = dialog.showAndWait();
		if(resultado.isPresent()) {
			String nuevoNombre = resultado.get().trim().toUpperCase();
			
			if(nuevoNombre.equalsIgnoreCase(et.getNombre())) {
				return;
			}
			
			try {
				servicioEtiqueta.editar(et, nuevoNombre);
				hacerBusqueda();
			} catch (ConstraintViolationException e) {
				new Alert(AlertType.ERROR,String.format("No se ha podido editar la etiqueta: %s", e.getMensaje()),ButtonType.OK).showAndWait();
			}
			
		}
		
	}
	
	@FXML
	private void btFusionar_click(ActionEvent event) {
		
		Optional<ButtonType> respuesta = new Alert(AlertType.CONFIRMATION, String.format("¿Fusionar %d etiquetas?", tvEtiquetas.getSelectionModel().getSelectedItems().size()),ButtonType.YES, ButtonType.NO).showAndWait();
		if(respuesta.isPresent() && respuesta.get()==ButtonType.YES) {
			
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Fusionar etiquetas");
			dialog.setHeaderText("Elige el nombre de la nueva etiqueta (o deja vacío para unirlas con el nombre de la más antigua)");
			
			Optional<String> nuevoNombre = dialog.showAndWait();
			
			try {
				servicioImagen.fusionarEtiquetas(tvEtiquetas.getSelectionModel().getSelectedItems(), nuevoNombre.isPresent() ? nuevoNombre.get() : null);
				hacerBusqueda();
			} catch (ConstraintViolationException e) {
				new Alert(AlertType.ERROR,String.format("No se han podido fusionar las etiquetas: %s", e.getMensaje()),ButtonType.OK).showAndWait();
			}
			
		}
		
	}
	
	//--------------------------------------------
	
	private CategoriaEtiquetaBusqueda getBusqueda() {
		
		CategoriaEtiquetaBusqueda retorna = new CategoriaEtiquetaBusqueda();
		
		retorna.setNombre(tfNombre.getText().trim().toUpperCase());
		
		if(cbImagenes.isSelected()) {
			
			retorna.setConNum(true);
			retorna.setNum(spNumero.getValue());
			if(rbMas.isSelected()) {
				retorna.setComparador(Comparador.MAYOR);
			}else if(rbMenos.isSelected()) {
				retorna.setComparador(Comparador.MENOR);
			}else {
				retorna.setComparador(Comparador.IGUAL);
			}
			
		}else {
			retorna.setConNum(false);
		}
		
		return retorna;
		
	}
	
	private void limpiarCampos() {
		
		tfNombre.setText("");
		cbImagenes.setSelected(false);
		
	}
	
	private void hacerBusqueda() {
		
		try {
			resultados.setAll(servicioEtiqueta.getBusqueda(getBusqueda()));
		} catch (ConstraintViolationException e) {
			new Alert(AlertType.ERROR, "No se ha podido hacer la búsqueda: " + e.getMensaje(), ButtonType.OK).showAndWait();
		}
		
	}

}
