package com.bcadaval.memefinder3020.modelo.servicios;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;
import com.bcadaval.memefinder3020.modelo.dao.RepositorioEtiqueta;

@Service
public class ServicioEtiqueta {

	@Autowired RepositorioEtiqueta repo;
	
	public List<Etiqueta> getAll(){
		return repo.findAll();
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Etiqueta anadir(String nombreEtiqueta) {
		Etiqueta nueva = new Etiqueta();
		nueva.setNombre(nombreEtiqueta);
		return repo.saveAndFlush(nueva);
	}
	
	public int countUsosDeEtiqueta(String nombreEtiqueta) {
		
		Etiqueta et = getPorNombre(nombreEtiqueta);
		return et==null ? 0 : repo.countUsosDeEtiqueta(et.getId());
	}
	
	public Etiqueta getPorNombre(String nombreEtiqueta) {
		
		Etiqueta etParaEjemplo = new Etiqueta();
		etParaEjemplo.setNombre(nombreEtiqueta);
		Example<Etiqueta> ejemplo = Example.of(etParaEjemplo,ExampleMatcher.matchingAll());
		List<Etiqueta> resultado = repo.findAll(ejemplo);
		return resultado.isEmpty() ? null : resultado.get(0);
	}
	
}
