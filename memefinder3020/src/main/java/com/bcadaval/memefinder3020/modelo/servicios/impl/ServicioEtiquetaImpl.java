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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.excepciones.NotFoundException;
import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;
import com.bcadaval.memefinder3020.modelo.servicios.Servicio;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioEtiqueta;

@Service
public class ServicioEtiquetaImpl extends Servicio implements ServicioEtiqueta{
	
	private static final Logger log = LogManager.getLogger(ServicioEtiquetaImpl.class);

	@PersistenceContext private EntityManager em;
	
	@Override
	public List<Etiqueta> getAll() {
		
		log.debug(".getAll() - Iniciando recuperación de todas las etiquetas");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Etiqueta> cq = cb.createQuery(Etiqueta.class);
        Root<Etiqueta> rootEntry = cq.from(Etiqueta.class);
        cq.select(rootEntry);
        
        List<Etiqueta> resultados = em.createQuery(cq).getResultList();
        log.debug(".getAll() - Finalizada recuperación de todas las etiquetas: " + resultados.size());
        return resultados;
        
	}
	
	@Override
	public Etiqueta getPorNombre(String nombreEtiqueta) throws ConstraintViolationException, NotFoundException {
		
		log.debug(".getPorNombre() - Iniciando recuperación de etiqueta por nombre");
		
		nombreEtiqueta = validarNombre(nombreEtiqueta);
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Etiqueta> cq = cb.createQuery(Etiqueta.class);
		Root<Etiqueta> root = cq.from(Etiqueta.class);
		
		Predicate p = cb.equal(root.get("nombre"), nombreEtiqueta);
		
		cq.select(root);
		cq.where(p);
		
		try {
			Etiqueta result = em.createQuery(cq).getSingleResult();
			log.debug(".getPorNombre() - Finalizada recuperación de etiqueta por nombre: " + result.getNombre());
			return result;
		} catch (NoResultException e) {
			log.debug(".getPorNombre() - Finalizada recuperación de etiqueta por nombre (no encontrada)");
			throw new NotFoundException();
		}catch (NonUniqueResultException e) {
			log.error(".getPorNombre() - Hay más de una etiqueta con el mismo nombre");
			throw new ConstraintViolationException("Hay más de una etiqueta con el mismo nombre");
		}
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Etiqueta getOCrear(String nombreEtiqueta) throws ConstraintViolationException {
		
		log.debug(".getOCrear() - Iniciado");
		Etiqueta et;
		try {
			et = getPorNombre(nombreEtiqueta);
		} catch (NotFoundException e) {
			et = anadir(nombreEtiqueta);
		}
		
		log.debug(".getOCrear() - Finalizado");
		return et;
		
	}
	
	@Override
	public Long count(Etiqueta et) {
		
		log.debug(".count() - Iniciando count de imágenes con esta etiqueta");
		
		if(et==null) {
			log.error(".count() - La etiqueta es null, devolviendo 0");
			return 0L;
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Etiqueta> root = cq.from(Etiqueta.class);
		
		root.join("imagenes");
		
		Predicate p1 = cb.equal(root.get("id"), et.getId());
		
		cq.select(cb.count(root));
		cq.where(p1);
		
		Long l = em.createQuery(cq).getSingleResult();
		log.debug(".count() - Finalizado count de imágenes de etiqueta: " + l);
		return l;
		
	}

	@Override
	public Long count(String nombreEtiqueta) throws ConstraintViolationException {
		
		log.debug(".count() - Iniciando count de imágenes con esta etiqueta (por nombre)");
		
		nombreEtiqueta = validarNombre(nombreEtiqueta);
		
		Long count;
		
		try {
			Etiqueta et = getPorNombre(nombreEtiqueta);
			log.debug(".count() - La etiqueta existe, llamando a count(Etiqueta)");
			count = count(et);
		} catch (NotFoundException e) {
			log.debug(".count() - La etiqueta no existe, devolviendo 0");
			count = 0L;
		}
		
		log.debug(".count() - Finalizado count de imágenes con esta etiqueta (por nombre): " + count);
		return count;
	}

	@Override
	public Map<Integer, Long> countAll(Collection<Etiqueta> etiquetas) {
		
		log.debug(".countAll() - Iniciando count de colección de etiquetas");
		
		if(etiquetas==null) {
			log.error(".countAll() - La colección es null, devolviendo HashMap vacío");
			return new HashMap<Integer, Long>();
		}
		
		Map<Integer, Long> retorna = new HashMap<Integer, Long>();
		for(Etiqueta et : etiquetas) {
			retorna.put(et.getId(), count(et));
		}
		
		log.debug(".countAll() - Finalizado count de colección de etiquetas");
		return retorna;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public Etiqueta anadir(String nombreEtiqueta) throws ConstraintViolationException {
		
		log.debug(".anadir() - Iniciando adición de  etiqueta");
		
		nombreEtiqueta = validarNombre(nombreEtiqueta);
		
		try {
			getPorNombre(nombreEtiqueta);
			log.error(".anadir() - Ya hay una etiqueta con este nombre: " + nombreEtiqueta);
			throw new ConstraintViolationException("Esta etiqueta ya existe");
		} catch (NotFoundException e) {
			Etiqueta et = new Etiqueta();
			et.setNombre(nombreEtiqueta);
			em.persist(et);
			
			//TODO quitar?
			em.flush();
			
			Etiqueta anadida = em.merge(et);
			log.debug(".anadir() - Finalizada adición de nueva etiqueta");
			return anadida;
		}
		
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public Etiqueta editar(Etiqueta etiqueta, String nuevoNombre) throws ConstraintViolationException {
		
		log.debug(".editar() - Iniciando edición de etiqueta");
		
		if(etiqueta==null) {
			log.error(".editar() - Etiqueta nula");
			throw new ConstraintViolationException("Etiqueta nula");
		}
		
		nuevoNombre = validarNombre(nuevoNombre);
		
		Etiqueta et = em.contains(etiqueta) ? etiqueta : em.merge(etiqueta);
		
		if(et.getNombre().equals(nuevoNombre)) {
			log.debug(".editar() - Finalizada edición (nuevo nombre coincide con antiguo)");
			return et;
		}
		
		try {
			getPorNombre(nuevoNombre);
			log.error(".editar() - Ya hay otra etiqueta con el nuevo nombre");
			throw new ConstraintViolationException("Hay otra etiqueta con ese nombre");
		} catch (NotFoundException e) {
		
			et.setNombre(nuevoNombre);
			Etiqueta retorna = em.merge(et);
			log.debug(".editar() - Finalizada edición: " + nuevoNombre);
			return retorna;
			
		}
		
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public void eliminar(Etiqueta etiqueta) {
		
		log.debug(".eliminar() - Iniciando eliminación de etiqueta");
		
		//TODO eliminar etiquetas del set de etiquetas de las imágenes que la contengan, lanza excepción al borrar una con imágenes
		em.remove(em.contains(etiqueta) ? etiqueta : em.merge(etiqueta));
		
		log.debug(".eliminar() - Finalizada eliminación de etiqueta");
		
	}
	
}
