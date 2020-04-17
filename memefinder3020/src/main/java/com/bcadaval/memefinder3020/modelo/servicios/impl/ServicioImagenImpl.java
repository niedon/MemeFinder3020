package com.bcadaval.memefinder3020.modelo.servicios.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.excepciones.NotFoundException;
import com.bcadaval.memefinder3020.modelo.beans.Categoria;
import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;
import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenBusqueda;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenTemp;
import com.bcadaval.memefinder3020.modelo.servicios.Servicio;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioCategoria;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioEtiqueta;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioImagen;
import com.bcadaval.memefinder3020.utils.Constantes;
import com.bcadaval.memefinder3020.utils.RutasUtils;

@Service
public class ServicioImagenImpl extends Servicio implements ServicioImagen {

	@Autowired private RutasUtils rutasUtils;
	
	@PersistenceContext EntityManager em;
	
	@Autowired ServicioEtiqueta servicioEtiqueta;
	@Autowired ServicioCategoria servicioCategoria;
	
	@Override
	public List<Imagen> getAll() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Imagen> cq = cb.createQuery(Imagen.class);
		final Root<Imagen> root = cq.from(Imagen.class);
        cq.select(root);
        return em.createQuery(cq).getResultList();
	}
	
	@Override
	public List<Imagen> getAllPorId(Collection<Integer> listaId) throws ConstraintViolationException{
		
		if(listaId==null) {
			throw new ConstraintViolationException("Lista nula");
		}else if(listaId.isEmpty()) {
			//TODO comprobar fallos
			return Collections.emptyList();
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Imagen> cq = cb.createQuery(Imagen.class);
        Root<Imagen> root = cq.from(Imagen.class);
        
        In<Integer> pIn = cb.in(root.get("id"));
        for(Integer id : listaId) {
        	pIn.value(id);
        }
        
        cq.select(root).where(pIn);
        
        return em.createQuery(cq).getResultList();
		
	}
	
	@Override
	public List<Integer> getAllIds() {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<Imagen> root = cq.from(Imagen.class);
		
		cq.select(root.get("id"));
		
		return em.createQuery(cq).getResultList();
		
	}
	
	@Override
	public Page<Imagen> getBusqueda(ImagenBusqueda busqueda, Pageable pageable) {
		
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Imagen> cq = cb.createQuery(Imagen.class);
		final Root<Imagen> root = cq.from(Imagen.class);
		
		List<Predicate> predicados = new ArrayList<Predicate>();
		
		//Con nombre %X%
		if(busqueda.getNombre() != null && !busqueda.getNombre().trim().isEmpty()) {
			predicados.add(cb.like(cb.upper(root.get("nombre")), '%'+busqueda.getNombre().trim().toUpperCase()+'%'));
		}
		
		//Sin categoría
		if(busqueda.isBuscarSinCategoria()) {
			predicados.add(cb.isNull(root.get("categoria")));
		//Con categoría X
		}else if(busqueda.getCategoria()!=null) {
			predicados.add(cb.equal(root.get("categoria"), busqueda.getCategoria().getId()));
		}
		
		//Después de X
		if(busqueda.getFechaDespues()!=null) {
			predicados.add(cb.greaterThan(root.get("fecha"), busqueda.getFechaDespues()));
		}
		
		//Antes de X
		if(busqueda.getFechaAntes()!=null) {
			predicados.add(cb.lessThan(root.get("fecha"), busqueda.getFechaAntes()));
		}
		
		//Sin etiquetas
		if(busqueda.isBuscarSinEtiquetas()) {
			predicados.add(cb.isEmpty(root.get("etiquetas")));
		}else {
			//Con al menos tales etiquetas
			if(busqueda.getEtiquetas()!=null) {
				for(Etiqueta et : busqueda.getEtiquetas()) {
					predicados.add(cb.isMember(et, root.get("etiquetas")));
				}
			}
		}
		
		Predicate[] predicadosArray = predicados.toArray(new Predicate[predicados.size()]);
		
		cq.select(root).where(predicadosArray);
		List<Imagen> resultado =  em.createQuery(cq).setMaxResults(pageable.getPageSize()).setFirstResult((int)pageable.getOffset()).getResultList();
		
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Imagen> rootCount = countQuery.from(Imagen.class);
		countQuery.select(cb.count(rootCount)).where(cb.and(predicadosArray));
		
		Long count = em.createQuery(countQuery).getSingleResult();
		
		Page<Imagen> retorna = new PageImpl<>(resultado, pageable, count);
		return retorna;
		
	}
	
	@Override
	public Imagen getPorId(Integer id) throws ConstraintViolationException, NotFoundException {
		
		if(id==null) {
			throw new ConstraintViolationException("ID nula");
		}
		
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Imagen> cq = cb.createQuery(Imagen.class);
		final Root<Imagen> root = cq.from(Imagen.class);
		
		Predicate p = cb.equal(root.get("id"), id);
		
		cq.select(root).where(p);
		
		try {
			return em.createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			throw new NotFoundException();
		}
		
	}
	
	@Override
	public List<Imagen> getUltimas(int num) throws ConstraintViolationException {
		
		if(num<1) {
			throw new ConstraintViolationException("No se permiten valores menores que 1");
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Imagen> cq = cb.createQuery(Imagen.class);
		Root<Imagen> root = cq.from(Imagen.class);
		cq.orderBy(cb.desc(root.get("fecha")));
		
		return em.createQuery(cq).setMaxResults(num).getResultList();
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Imagen anadir(ImagenTemp iTemp) throws ConstraintViolationException {
		
		if(iTemp==null) {
			throw new ConstraintViolationException("Se ha recibido una imagen vacía");
		}
		
		File archivoImagen = iTemp.getImagen();
		
		if(archivoImagen == null) {
			throw new ConstraintViolationException("La imagen es nula");
		}else if(!archivoImagen.exists()) {
			throw new ConstraintViolationException("La imagen no existe");
		}else if(archivoImagen.isDirectory()) {
			throw new ConstraintViolationException("El archivo es un directorio");
		}else if( ! archivoImagen.getName().contains(".") || archivoImagen.getName().charAt(archivoImagen.getName().length()-1)=='.') {
			throw new ConstraintViolationException("El archivo no tiene extensión");
		}else if( ! Arrays.asList(Constantes.FORMATOS_PERMITIDOS).contains(archivoImagen.getName().substring(archivoImagen.getName().indexOf('.')+1).toUpperCase())){
			throw new ConstraintViolationException("El archivo tiene un formato no permitido");
		}
		
		Imagen img = new Imagen();
		String nombre = iTemp.getNombre();
		if(nombre==null || nombre.trim().isEmpty()) {
			//Siempre tendrá . porque se comprueba al principio de este método
			nombre = iTemp.getImagen().getName().substring(0,iTemp.getImagen().getName().indexOf('.'));
		}else {
			nombre = nombre.trim();
		}
		img.setNombre(nombre.length()>64 ? nombre.substring(0,64) : nombre);
		img.setExtension(archivoImagen.getName().substring(archivoImagen.getName().lastIndexOf('.')+1));
		if(iTemp.getCategoria()!=null &&  ! iTemp.getCategoria().isEmpty()) {
			img.setCategoria(servicioCategoria.getOCrear(iTemp.getCategoria()));
		}
		img.setFecha(LocalDateTime.now());
		
		iTemp.getEtiquetasString().forEach(el -> {
			try {
				img.getEtiquetas().add(servicioEtiqueta.getOCrear(el));
			} catch (ConstraintViolationException e) {
				//TODO al log
			}
		});
		
		em.persist(img);
		
		File copiada = rutasUtils.getFileDeImagen(img);
		try {
			copiada.getParentFile().mkdirs();
			FileSystemUtils.copyRecursively(archivoImagen, copiada);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ConstraintViolationException("Error copiando el archivo de imagen");
		}
		
		em.flush();
		
		return em.merge(img);

	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Imagen editar(Imagen imagen) {
		
		return em.merge(imagen);
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public void eliminar(Imagen imagen) {
		
		File f = rutasUtils.getFileDeImagen(imagen);
		
		em.remove(em.contains(imagen) ? imagen : em.merge(imagen));
		
		if( ! FileSystemUtils.deleteRecursively(f)) {
			//TODO al log
		}
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public void eliminar(Integer id) throws ConstraintViolationException {
		
		if(id==null) {
			throw new ConstraintViolationException("ID null");
		}
		
		try {
			Imagen img = getPorId(id);
			
			File f = rutasUtils.getFileDeImagen(img);
			
			em.remove(img);
			
			if( ! FileSystemUtils.deleteRecursively(f)) {
				//TODO al log
			}
			
		} catch (NotFoundException e) {
			throw new ConstraintViolationException("La imagen no existe");
		}
		
		
	}
	
	@Override
	public void sustituirImagen(File fNueva, Imagen imgOriginal) throws IOException {
		Files.copy(fNueva.toPath(), FileSystems.getDefault().getPath(
				rutasUtils.RUTA_IMAGENES_AC,
				String.format("%d.%s", imgOriginal.getId(),imgOriginal.getExtension())),
				StandardCopyOption.REPLACE_EXISTING);
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public void fusionarCategorias(List<Categoria> categorias, String nuevoNombre) throws ConstraintViolationException {
		
		if(categorias==null || categorias.isEmpty()) {
			throw new ConstraintViolationException("La lista de categorías no puede estar vacía");
		}
		
		Categoria categoriaMasAntigua = categorias.get(0);
		for(Categoria c : categorias) {
			if(c.getId() < categoriaMasAntigua.getId()) {
				categoriaMasAntigua = c;
			}
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaUpdate<Imagen> cu = cb.createCriteriaUpdate(Imagen.class);
		Root<Imagen> root = cu.from(Imagen.class);
		
		List<Predicate> listaOr = new ArrayList<Predicate>();
		for(Categoria c : categorias) {
			if(c != categoriaMasAntigua) {
				listaOr.add(cb.equal(root.get("categoria"), c));
			}
		}
		
		Predicate predicateOr;
		
		if(listaOr.size()==1) {
			predicateOr = listaOr.get(0);
		}else {
			predicateOr = cb.or(listaOr.toArray(new Predicate[listaOr.size()]));
		}
		
		cu.set(root.get("categoria"), categoriaMasAntigua);
		cu.where(predicateOr);
		
		em.createQuery(cu).executeUpdate();
		
		List<Categoria> nuevasCat = new ArrayList<Categoria>(categorias.size());
		categorias.forEach(el -> nuevasCat.add(em.find(Categoria.class, el.getId())));
		
		for(Categoria c : nuevasCat) {
			if(c.getId() != categoriaMasAntigua.getId()) {
				servicioCategoria.eliminar(c);
			}
		}
		try {
			nuevoNombre = validarNombre(nuevoNombre);
			servicioCategoria.editar(categoriaMasAntigua, nuevoNombre);
		}catch (ConstraintViolationException e) {
			return;
		}
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public void borrarPorCategoria(Categoria cat) throws ConstraintViolationException {
		
		if(cat==null) {
			throw new ConstraintViolationException("Categoría nula");
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<Imagen> querySelect = cb.createQuery(Imagen.class);
		Root<Imagen> rootSelect = querySelect.from(Imagen.class);
		Predicate pSelect = cb.equal(rootSelect.get("categoria"), cat);
		querySelect.where(pSelect);
		List<Imagen> listaSelect = em.createQuery(querySelect).getResultList();

		if(listaSelect.isEmpty()) {
			return;
		}
		
		CriteriaDelete<Imagen> cd = cb.createCriteriaDelete(Imagen.class);
		Root<Imagen> root = cd.from(Imagen.class);
		
		Predicate p = cb.equal(root.get("categoria"), cat);
		
		cd.where(p);
		
		em.createQuery(cd).executeUpdate();
		
		listaSelect.forEach(el -> {
			if( ! FileSystemUtils.deleteRecursively(rutasUtils.getFileDeImagen(el))) {
				//TODO al log
			}
		});
		
	}
	
}
