package com.bcadaval.memefinder3020.utils;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import com.bcadaval.memefinder3020.modelo.beans.Imagen;

public class MiscUtils {
	
	/**
	 * 
	 * @param imgFile
	 * @return
	 * @throws IOException
	 * @author https://stackoverflow.com/a/12164026/11835818
	 */
	public static Dimension getImageDimension(File imgFile) throws IOException {
		  int pos = imgFile.getName().lastIndexOf(".");
		  if (pos == -1)
		    throw new IOException("No extension for file: " + imgFile.getAbsolutePath());
		  String suffix = imgFile.getName().substring(pos + 1);
		  Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
		  while(iter.hasNext()) {
		    ImageReader reader = iter.next();
		    try {
		      ImageInputStream stream = new FileImageInputStream(imgFile);
		      reader.setInput(stream);
		      int width = reader.getWidth(reader.getMinIndex());
		      int height = reader.getHeight(reader.getMinIndex());
		      return new Dimension(width, height);
		    } catch (IOException e) {
		      //TODO log
		    } finally {
		      reader.dispose();
		    }
		  }

		  throw new IOException("Not a known image file: " + imgFile.getAbsolutePath());
		}
	
	public static String getStringRutaImagen(String id, String extension) {
		return getStringRutaImagen(id+'.'+extension);
	}
	
	public static String getStringRutaImagen(String idYExtension) {
		return new File(Constantes.RUTA_IMAGENES + "\\" + idYExtension).toURI().toString();
	}
	
	public static String getStringRutaImagen(Imagen imagen) {
		return getStringRutaImagen(imagen.getId()+"", imagen.getExtension());
	}
	
	public static File getFileDeImagen(Imagen imagen) {
		return new File(Constantes.RUTA_IMAGENES + "\\" + imagen.getId() + '.' + imagen.getExtension());
	}

}
