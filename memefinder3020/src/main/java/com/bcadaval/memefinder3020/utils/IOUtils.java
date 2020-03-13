package com.bcadaval.memefinder3020.utils;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import com.bcadaval.memefinder3020.modelo.beans.Imagen;

public class IOUtils {
	
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
	
	public static File getFileRutaImagen(String id, String extension) {
		return getFileRutaImagen(id+'.'+extension);
	}

	public static File getFileRutaImagen(String idYExtension) {
		return Constantes.fs.getPath(Constantes.RUTA_IMAGENES.toString(), idYExtension).toFile();
	}

	public static File getFileDeImagen(Imagen imagen) {
		return getFileRutaImagen(imagen.getId() + "." + imagen.getExtension());
	}
	
	public static String getURLDeImagen(Imagen imagen) {
		return getFileDeImagen(imagen).toURI().toString();
	}

}
