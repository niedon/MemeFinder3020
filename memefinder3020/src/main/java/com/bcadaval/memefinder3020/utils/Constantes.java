package com.bcadaval.memefinder3020.utils;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Constantes {
	
	static final FileSystem fs = FileSystems.getDefault();
	
	public static final Path DIRECTORIO_BASE = fs.getPath(System.getProperty("user.home"), "memefinder3020");
	
	public static final String RUTA_FXML = "/fxml/%s.fxml";
	public static final String RUTA_IMG = "/img/%s";
	public static final String NOMBRE_PANTALLA_CARGA = "pantallaCargando";
	public static final String NOMBRE_PANTALLA_SPLASH = "splash";
	
	public static final Path RUTA_IMAGENES = fs.getPath(DIRECTORIO_BASE.toString(), "img");
	public static final Path RUTA_BD = fs.getPath(DIRECTORIO_BASE.toString(), "db", "db");

}
