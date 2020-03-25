package com.bcadaval.memefinder3020.utils;

import java.io.File;

import com.bcadaval.memefinder3020.modelo.beans.Imagen;

public class IOUtils {
	
	public static File getFileRutaImagen(String id, String extension) {
		return getFileRutaImagen(id+'.'+extension);
	}

	public static File getFileRutaImagen(String idYExtension) {
		return Constantes.fs.getPath(Constantes.RUTA_IMAGENES_AC.toString(), idYExtension).toFile();
	}

	public static File getFileDeImagen(Imagen imagen) {
		return getFileRutaImagen(imagen.getId() + "." + imagen.getExtension());
	}
	
	public static String getURLDeImagen(Imagen imagen) {
		return getFileDeImagen(imagen).toURI().toString();
	}

}
