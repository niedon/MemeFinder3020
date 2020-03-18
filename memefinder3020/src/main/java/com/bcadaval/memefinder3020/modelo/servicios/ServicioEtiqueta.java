package com.bcadaval.memefinder3020.modelo.servicios;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	@Transactional
	public List<Etiqueta> getAll(){
		return repo.findAll();
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Etiqueta anadir(String nombreEtiqueta) {
		Etiqueta nueva = new Etiqueta();
		nueva.setNombre(nombreEtiqueta);
		return repo.saveAndFlush(nueva);
	}
	
	@Transactional
	public int countUsosDeEtiqueta(Etiqueta et) {
		return repo.countUsosDeEtiqueta(et.getId());
	}
	
	@Transactional
	public int countUsosDeEtiqueta(String nombreEtiqueta) {
		
		Etiqueta et = getPorNombre(nombreEtiqueta);
		return et==null ? 0 : repo.countUsosDeEtiqueta(et.getId());
	}
	
	@Transactional
	public Map<Integer, Integer> countUsosDeEtiquetas(Collection<Etiqueta> etiquetas){
		Map<Integer,Integer> retorna = new HashMap<Integer,Integer>();
		for(Etiqueta et : etiquetas) {
			retorna.put(et.getId(), countUsosDeEtiqueta(et));
		}
		return retorna;
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Etiqueta getPorNombre(String nombreEtiqueta) {
		
		Etiqueta etParaEjemplo = new Etiqueta();
		etParaEjemplo.setNombre(nombreEtiqueta.toUpperCase());
		Example<Etiqueta> ejemplo = Example.of(etParaEjemplo,ExampleMatcher.matchingAll());
		List<Etiqueta> resultado = repo.findAll(ejemplo);
		return resultado.isEmpty() ? null : resultado.get(0);
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Etiqueta getOCrear(String nombreEtiqueta) {
		Etiqueta et = getPorNombre(nombreEtiqueta);
		if(et==null) {
			et = anadir(nombreEtiqueta);
		}
		return et;
	}
	
}
