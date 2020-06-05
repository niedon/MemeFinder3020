package com.bcadaval.memefinder3020.utils;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import org.springframework.stereotype.Component;

import com.bcadaval.memefinder3020.modelo.beans.Imagen;

@Component
public class RutasUtils {
	
	private final FileSystem fs;
	
	public final String DIRECTORIO_BASE_AC;
	public final String RUTA_IMAGENES_AC;
	public final String RUTA_BD_AA;
	
	public final File ARCHIVO_AJUSTES;

	public RutasUtils(String arranqueTest) {
		
		fs = FileSystems.getDefault();
		
		if(arranqueTest.equals("false")) {
			DIRECTORIO_BASE_AC = fs.getPath(System.getProperty("user.home"), "memefinder3020").toString();
			RUTA_IMAGENES_AC = fs.getPath(DIRECTORIO_BASE_AC, "img").toString();
			RUTA_BD_AA = fs.getPath(DIRECTORIO_BASE_AC, "db", "db").toString();
		}else {
			DIRECTORIO_BASE_AC = fs.getPath(System.getProperty("java.io.tmpdir"), "memefinder3020").toString();
			RUTA_IMAGENES_AC = fs.getPath(DIRECTORIO_BASE_AC, "img").toString();
			RUTA_BD_AA = "bdtest";
		}
		
		ARCHIVO_AJUSTES = fs.getPath(DIRECTORIO_BASE_AC, "ajustes.xml").toFile();
		
	}
	
	public File getFileRutaImagen(String id, String extension) {
		return getFileRutaImagen(id+'.'+extension);
	}

	public File getFileRutaImagen(String idYExtension) {
		return fs.getPath(RUTA_IMAGENES_AC.toString(), idYExtension).toFile();
	}

	public File getFileDeImagen(Imagen imagen) {
		return getFileRutaImagen(imagen.getId() + "." + imagen.getExtension());
	}
	
	public String getURLDeImagen(Imagen imagen) {
		return getFileDeImagen(imagen).toURI().toString();
	}
	
}
