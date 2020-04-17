package com.bcadaval.memefinder3020.modelo.servicios.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.excepciones.NotFoundException;
import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;
import com.bcadaval.memefinder3020.modelo.servicios.Servicio;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioEtiqueta;

@Service
public class ServicioEtiquetaImpl extends Servicio implements ServicioEtiqueta{

	@PersistenceContext EntityManager em;
	
	@Override
	public List<Etiqueta> getAll() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Etiqueta> cq = cb.createQuery(Etiqueta.class);
        Root<Etiqueta> rootEntry = cq.from(Etiqueta.class);
        cq.select(rootEntry);
        return em.createQuery(cq).getResultList();
	}
	
	@Override
	public Etiqueta getPorNombre(String nombreEtiqueta) throws ConstraintViolationException, NotFoundException {
		
		nombreEtiqueta = validarNombre(nombreEtiqueta);
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Etiqueta> cq = cb.createQuery(Etiqueta.class);
		Root<Etiqueta> root = cq.from(Etiqueta.class);
		
		Predicate p = cb.equal(root.get("nombre"), nombreEtiqueta);
		
		cq.select(root);
		cq.where(p);
		
		try {
			Etiqueta result = em.createQuery(cq).getSingleResult();
			return result;
		} catch (NoResultException e) {
			throw new NotFoundException();
		}catch (NonUniqueResultException e) {
			throw new ConstraintViolationException("Hay más de una etiqueta con el mismo nombre");
			//TODO al log pero ya
		}
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Etiqueta getOCrear(String nombreEtiqueta) throws ConstraintViolationException {
		try {
			return getPorNombre(nombreEtiqueta);
		} catch (NotFoundException e) {
			return anadir(nombreEtiqueta);
		}
	}
	
	@Override
	public Long count(Etiqueta et) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Etiqueta> root = cq.from(Etiqueta.class);
		
		root.join("imagenes");
		
		
		Predicate p1 = cb.equal(root.get("id"), et.getId());
		
		cq.select(cb.count(root));
		cq.where(p1);
		
		Long l = em.createQuery(cq).getSingleResult();
		
		return l;
		
	}

	@Override
	public Long count(String nombreEtiqueta) throws ConstraintViolationException {
		if(nombreEtiqueta==null || nombreEtiqueta.isEmpty()) {
			throw new ConstraintViolationException("Nombre vacío");
		}
		
		nombreEtiqueta = nombreEtiqueta.trim().toUpperCase();
		
		try {
			Etiqueta et = getPorNombre(nombreEtiqueta);
			return count(et);
		} catch (NotFoundException e) {
			return 0L;
		}
	}

	@Override
	public Map<Integer, Long> countAll(Collection<Etiqueta> etiquetas) {
		Map<Integer, Long> retorna = new HashMap<Integer, Long>();
		for(Etiqueta et : etiquetas) {
			retorna.put(et.getId(), count(et));
		}
		return retorna;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public Etiqueta anadir(String nombreEtiqueta) throws ConstraintViolationException {
		
		nombreEtiqueta = validarNombre(nombreEtiqueta);
		
		try {
			getPorNombre(nombreEtiqueta);
			throw new ConstraintViolationException("Esta etiqueta ya existe");
		} catch (NotFoundException e) {
			Etiqueta et = new Etiqueta();
			et.setNombre(nombreEtiqueta);
			em.persist(et);
			
			//TODO quitar?
			em.flush();
			
			return et;
		}
		
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public Etiqueta editar(Etiqueta etiqueta, String nuevoNombre) throws ConstraintViolationException {
		
		if(etiqueta==null) {
			throw new ConstraintViolationException("Etiqueta nula");
		}
		
		nuevoNombre = validarNombre(nuevoNombre);
		
		Etiqueta et = em.contains(etiqueta) ? etiqueta : em.merge(etiqueta);
		
		if(et.getNombre().equals(nuevoNombre)) {
			return et;
		}
		
		try {
			getPorNombre(nuevoNombre);
			throw new ConstraintViolationException("Hay otra etiqueta con ese nombre");
		} catch (NotFoundException e) {
		
			et.setNombre(nuevoNombre);
			return em.merge(et);
			
		}
		
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public void eliminar(Etiqueta etiqueta) {
		//TODO eliminar etiquetas del set de etiquetas de las imágenes que la contengan, lanza excepción al borrar una con imágenes
		em.remove(em.contains(etiqueta) ? etiqueta : em.merge(etiqueta));
	}
	
}
