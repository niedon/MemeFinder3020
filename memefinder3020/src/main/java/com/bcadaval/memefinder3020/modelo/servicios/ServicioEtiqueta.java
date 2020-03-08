package com.bcadaval.memefinder3020.modelo.servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;
import com.bcadaval.memefinder3020.modelo.dao.RepositorioEtiqueta;

@Service
public class ServicioEtiqueta {

	@Autowired RepositorioEtiqueta repo;
	
	public List<Etiqueta> getAll(){
		return repo.findAll();
	}
	
}
