package com.bcadaval.memefinder3020.modelo.servicios;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.bcadaval.memefinder3020.modelo.beans.Categoria;
import com.bcadaval.memefinder3020.modelo.dao.RepositorioCategoria;


@Service
public class ServicioCategoria {

	@Autowired RepositorioCategoria repo;
	
	public List<Categoria> getAll(){
		return repo.findAll();
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Categoria getOCrear(String nombre) {
		
		Categoria resultado = getPorNombre(nombre);
		if(resultado==null) {
			Categoria nueva = new Categoria();
			nueva.setNombre(nombre.toUpperCase());
			return repo.saveAndFlush(nueva);
		}else {
			return resultado;
		}
		
	}
	
	public Categoria getPorNombre(String nombre) {
		nombre = nombre.toUpperCase();
		
		Categoria catParaEjemplo = new Categoria();
		catParaEjemplo.setNombre(nombre);
		
		Example<Categoria> ejem = Example.of(catParaEjemplo,ExampleMatcher.matchingAll());
		List<Categoria> resultado = repo.findAll(ejem);
		
		return resultado.isEmpty() ? null : resultado.get(0);
	}
	
	public void borrarCategoria(Categoria cat) {
		repo.delete(cat);
	}
	
}
