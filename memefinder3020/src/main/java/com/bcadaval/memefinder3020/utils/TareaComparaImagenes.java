package com.bcadaval.memefinder3020.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioImagen;
import com.github.kilianB.hash.Hash;
import com.github.kilianB.hashAlgorithms.HashingAlgorithm;
import com.github.kilianB.hashAlgorithms.PerceptiveHash;

import javafx.concurrent.Task;

public class TareaComparaImagenes extends Task<List<Integer>> {

	private ServicioImagen servicioImagen;
	
	private File original;
	
	public TareaComparaImagenes(File original, ServicioImagen servicioImagen) {
		this.original = original;
		this.servicioImagen = servicioImagen;
	}
	
	@Override
	protected List<Integer> call() throws Exception {
		
		updateProgress(0, 100);
		
		updateMessage("0 %");
		
		ArrayList<Integer> retorna = new ArrayList<Integer>();
		
		List<Imagen> imagenesBD = servicioImagen.getAll();
		
		updateProgress(0, imagenesBD.size());
		
		HashingAlgorithm hasher = new PerceptiveHash(32);
		
		Hash imgOriginal = hasher.hash(original);
		
		for(Imagen img : imagenesBD) {
			Hash otraImagen = hasher.hash(new File(Constantes.RUTA_IMAGENES + "\\" + img.getId() + '.' + img.getExtension()));
			if(imgOriginal.normalizedHammingDistanceFast(otraImagen) < .2) {
				retorna.add(img.getId());
			}
			
			//TODO eliminar al acabar testeos
			Thread.sleep(1000);
			
			updateProgress(imagenesBD.indexOf(img), imagenesBD.size());
			updateMessage((int)(((float)imagenesBD.indexOf(img)/(float)imagenesBD.size())*100) + " %");
		}
		
		if(retorna.isEmpty()) {
			updateMessage("No se han encontrado coincidencias");
		}else {
			updateMessage("Coincidencias encontradas: " + retorna.size());
		}
		
		return retorna;
	}
	
	

}
