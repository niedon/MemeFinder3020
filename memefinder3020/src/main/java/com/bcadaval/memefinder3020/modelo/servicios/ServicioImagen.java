package com.bcadaval.memefinder3020.modelo.servicios;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.excepciones.NotFoundException;
import com.bcadaval.memefinder3020.modelo.beans.Categoria;
import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;
import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenBusqueda;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenBusquedaExportar;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenTemp;

public interface ServicioImagen {

	List<Imagen> getAll();
	List<Imagen> getAllPorId(Collection<Integer> listaId) throws ConstraintViolationException;
	List<Integer> getAllIds();
	Page<Imagen> getBusqueda(ImagenBusqueda busqueda, Pageable pageable) throws ConstraintViolationException;
	List<Imagen> getBusquedaExportar(ImagenBusquedaExportar busqueda) throws ConstraintViolationException;
	Imagen getPorId(Integer id) throws ConstraintViolationException, NotFoundException;
	List<Imagen> getUltimas(int num) throws ConstraintViolationException;
	
	Imagen anadir(ImagenTemp iTemp) throws ConstraintViolationException;
	Imagen editar(Imagen imagen);
	void eliminar(Imagen imagen);
	void eliminar(Integer id) throws ConstraintViolationException;
	
	void sustituirImagen(File fNueva, Imagen imgOriginal) throws ConstraintViolationException;
	void borrarPorId(Collection<Integer> ids) throws ConstraintViolationException;
	void fusionarCategorias(List<Categoria> categorias, String nuevoNombre) throws ConstraintViolationException;
	void borrarPorCategoria(Categoria cat) throws ConstraintViolationException;
	void fusionarEtiquetas(List<Etiqueta> etiquetas, String nuevoNombre) throws ConstraintViolationException;
	void borrarPorEtiqueta(Etiqueta et) throws ConstraintViolationException;
	
}
