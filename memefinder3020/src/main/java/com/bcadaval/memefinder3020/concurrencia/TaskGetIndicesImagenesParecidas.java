package com.bcadaval.memefinder3020.concurrencia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioImagen;
import com.bcadaval.memefinder3020.utils.RutasUtils;
import com.github.kilianB.hash.Hash;
import com.github.kilianB.hashAlgorithms.HashingAlgorithm;
import com.github.kilianB.hashAlgorithms.PerceptiveHash;

import javafx.concurrent.Task;

public class TaskGetIndicesImagenesParecidas extends Task<List<Integer>> {

	private RutasUtils rutasUtils;
	
	private ServicioImagen servicioImagen;
	
	private File original;
	
	public TaskGetIndicesImagenesParecidas(File original, ServicioImagen servicioImagen, RutasUtils rutasUtils) {
		this.original = original;
		this.servicioImagen = servicioImagen;
		this.rutasUtils = rutasUtils;
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
			
			Hash otraImagen = hasher.hash(rutasUtils.getFileDeImagen(img));
			if(imgOriginal.normalizedHammingDistanceFast(otraImagen) < .2) {
				retorna.add(img.getId());
			}
			
//			//TODO quitar al acabar testeos
//			if(isCancelled())break;
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				if(isCancelled())break;
//			}
			
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
