package com.bcadaval.memefinder3020.modelo.servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.dao.RepositorioImagen;

@Service
public class ServicioImagen {

	@Autowired RepositorioImagen repo;
	
	public List<Imagen> getAll(){
		return repo.findAll();
	}
	
	
	public void anadirImagen(Imagen im) {
		repo.saveAndFlush(im);
		
		
	}
}
