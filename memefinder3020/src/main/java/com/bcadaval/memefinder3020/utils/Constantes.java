package com.bcadaval.memefinder3020.utils;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Constantes {
	
	static final FileSystem fs = FileSystems.getDefault();
	
	public static final Path DIRECTORIO_BASE_AC = fs.getPath(System.getProperty("user.home"), "memefinder3020");
	
	public static final String RUTA_FXML_RFE = "/fxml/%s.fxml";
	public static final String RUTA_COMPONENTES_FXML_RFE = "/fxml/componentes/%s.fxml";
	public static final String RUTA_IMG_RF = "/img/%s";
	public static final String RUTA_SVG_RFE = "/img/svg/%s.svg";
	public static final String RUTA_CSS_RFE = "/css/%s.css";
	public static final String RUTA_CSSESPECIFICO_RFE = "/css/especifico/%s.css";
	
	public static final String NOMBRE_PANTALLA_CARGA = "pantallaCargando";
	public static final String NOMBRE_PANTALLA_SPLASH = "splash";
	public static final String NOMBRE_PANEETIQUETAS = "paneEtiquetas";
	public static final String NOMBRE_PANERESULTADO = "paneResultado";
	
	public static final Path RUTA_IMAGENES_AC = fs.getPath(DIRECTORIO_BASE_AC.toString(), "img");
	public static final Path RUTA_BD_AA = fs.getPath(DIRECTORIO_BASE_AC.toString(), "db", "db");
	
	public static final String FORMAT_DDMMYYHHMM = "dd/MM/yy HH:mm";
	
	public static final String SVG_AJUSTES = "iAjustes";
	public static final String SVG_CATEGORIA = "iCategoria";
	public static final String SVG_EQUIS = "iEquis";
	public static final String SVG_FLECHAABAJO = "iFechaAbajo";
	public static final String SVG_FLECHAARRIBA = "iFlechaArriba";
	public static final String SVG_FLECHADERECHA = "iFlechaDerecha";
	public static final String SVG_FLECHAIZQUIERDA = "iFlechaIzquierda";
	public static final String SVG_GUARDAR = "iGuardar";
	public static final String SVG_IMAGENFIT = "iImagenFit";
	public static final String SVG_IMAGENESCALA = "iImagenEscala";
	public static final String SVG_MENOS = "iMenos";
	public static final String SVG_LAPIZ = "iLapiz";
	public static final String SVG_LUPA = "iLupa";
	public static final String SVG_PAPELERA = "iPapelera";
	public static final String SVG_PLUS = "iPlus";
	public static final String SVG_TAG = "iTag";
	public static final String SVG_UNIR = "iUnir";

}
