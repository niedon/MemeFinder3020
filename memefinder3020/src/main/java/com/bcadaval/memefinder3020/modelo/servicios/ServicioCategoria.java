package com.bcadaval.memefinder3020.modelo.servicios;

import java.util.List;

import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.excepciones.NotFoundException;
import com.bcadaval.memefinder3020.modelo.beans.Categoria;
import com.bcadaval.memefinder3020.modelo.beans.temp.CategoriaBusqueda;

public interface ServicioCategoria {

	List<Categoria> getAll();
	List<Categoria> getBusqueda(CategoriaBusqueda busqueda);
	Categoria getPorNombre(String nombre) throws ConstraintViolationException, NotFoundException;
	Categoria getOCrear(String nombre) throws ConstraintViolationException;
	
	Categoria anadir(String nombre) throws ConstraintViolationException;
	Categoria editar(Categoria cat, String nuevoNombre) throws ConstraintViolationException;
	void eliminar(Categoria cat);
	
}
