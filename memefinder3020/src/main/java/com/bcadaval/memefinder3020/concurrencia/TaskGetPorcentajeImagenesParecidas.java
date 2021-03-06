package com.bcadaval.memefinder3020.concurrencia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.utils.RutasUtils;
import com.github.kilianB.hash.Hash;
import com.github.kilianB.hashAlgorithms.HashingAlgorithm;
import com.github.kilianB.hashAlgorithms.PerceptiveHash;

import javafx.concurrent.Task;

public class TaskGetPorcentajeImagenesParecidas extends Task<List<Double>> {

	private static final Logger log = LogManager.getLogger(TaskGetPorcentajeImagenesParecidas.class);
	
	private RutasUtils rutasUtils;
	private File imagenNueva;
	private List<Imagen> listaImagenes;
	
	public TaskGetPorcentajeImagenesParecidas(File imagenNueva, List<Imagen> listaImagenes, RutasUtils rutasUtils) {
		this.imagenNueva = imagenNueva;
		this.listaImagenes = listaImagenes;
		this.rutasUtils = rutasUtils;
	}
	
	@Override
	protected List<Double> call() throws Exception {
		
		log.debug(".call() - Iniciando cálculo de porcentajes");
		
		try {
			updateProgress(0, 1);
			
			ArrayList<Double> retorna = new ArrayList<>(listaImagenes.size());
			
			HashingAlgorithm hasher = new PerceptiveHash(64);
			
			Hash imgOriginal = hasher.hash(imagenNueva);
			
			for(int i=0; i<listaImagenes.size(); i++) {
				
				Hash otraImagen = hasher.hash(rutasUtils.getFileDeImagen(listaImagenes.get(i)));
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
			
			log.debug(".call() - Finalizado cálculo de porcentajes");
			return retorna;
			
		} catch (Exception e) {
			log.error(".call() - Error en cálculo de porcentajes", e);
			throw e;
		}
		
	}

}
