package com.bcadaval.memefinder3020.concurrencia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.utils.IOUtils;
import com.github.kilianB.hash.Hash;
import com.github.kilianB.hashAlgorithms.HashingAlgorithm;
import com.github.kilianB.hashAlgorithms.PerceptiveHash;

import javafx.concurrent.Task;

public class TaskGetPorcentajeImagenesParecidas extends Task<List<Double>> {

	private File imagenNueva;
	private List<Imagen> listaImagenes;
	
	public TaskGetPorcentajeImagenesParecidas(File imagenNueva, List<Imagen> listaImagenes) {
		this.imagenNueva = imagenNueva;
		this.listaImagenes = listaImagenes;
	}
	
	@Override
	protected List<Double> call() throws Exception {
		
		try {
			updateProgress(0, 1);
			
			ArrayList<Double> retorna = new ArrayList<>(listaImagenes.size());
			
			HashingAlgorithm hasher = new PerceptiveHash(64);
			
			Hash imgOriginal = hasher.hash(imagenNueva);
			
			for(int i=0; i<listaImagenes.size(); i++) {
				
				Hash otraImagen = hasher.hash(IOUtils.getFileDeImagen(listaImagenes.get(i)));
				retorna.add(i, imgOriginal.normalizedHammingDistance(otraImagen));
				
//				//TODO quitar al acabar testeos
//				if(isCancelled())break;
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					if(isCancelled())break;
//				}
				
				updateProgress(i, listaImagenes.size());
			}
			
			return retorna;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

}
