package com.bcadaval.memefinder3020.modelo.servicios;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.excepciones.NotFoundException;
import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;

public interface ServicioEtiqueta {
	
	List<Etiqueta> getAll();
	Etiqueta getPorNombre(String nombreEtiqueta) throws ConstraintViolationException, NotFoundException;
	Etiqueta getOCrear(String nombreEtiqueta) throws ConstraintViolationException;
	
	Long count(Etiqueta et);
	Long count(String nombreEtiqueta) throws ConstraintViolationException;
	Map<Integer, Long> countAll(Collection<Etiqueta> etiquetas);
	
	Etiqueta anadir(String nombreEtiqueta) throws ConstraintViolationException;
	Etiqueta editar(Etiqueta etiqueta, String nuevoNombre) throws ConstraintViolationException;
	void eliminar(Etiqueta etiqueta);
	
}
