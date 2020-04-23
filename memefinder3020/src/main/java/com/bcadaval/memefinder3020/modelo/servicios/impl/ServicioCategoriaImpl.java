package com.bcadaval.memefinder3020.modelo.servicios.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.excepciones.NotFoundException;
import com.bcadaval.memefinder3020.modelo.beans.Categoria;
import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.beans.temp.CategoriaBusqueda;
import com.bcadaval.memefinder3020.modelo.servicios.Servicio;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioCategoria;

@Service
public class ServicioCategoriaImpl extends Servicio implements ServicioCategoria {
	
	private static final Logger log = LogManager.getLogger(ServicioCategoriaImpl.class);

	@PersistenceContext private EntityManager em;
	
	@Override
	public List<Categoria> getAll(){
		
		log.debug(".getAll() - Iniciando recuperación de todas las categorías");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Categoria> cq = cb.createQuery(Categoria.class);
        Root<Categoria> rootEntry = cq.from(Categoria.class);
        cq.select(rootEntry);
        
        List<Categoria> resultados = em.createQuery(cq).getResultList();
        log.debug(".getAll() - Finalizada recuperación de todas las categorías: " + resultados.size());
        return resultados;
	}
	
	/*
SELECT * FROM CATEGORIA
WHERE CATEGORIA.ID IN (
	SELECT CATEGORIA.ID
	FROM CATEGORIA
	LEFT JOIN IMAGEN
	ON CATEGORIA.ID = IMAGEN.CATEGORIA
	GROUP BY CATEGORIA.ID
	HAVING COUNT(IMAGEN.CATEGORIA) [</>/=] [num]
)
	*/
	@Override
	public List<Categoria> getBusqueda(CategoriaBusqueda busqueda) throws ConstraintViolationException{
		
		log.debug(".getBusqueda() - Iniciando recuperación de categorías por búsqueda");
		
		if(busqueda==null) {
			throw new ConstraintViolationException("Objeto de búsqueda nulo");
		}
		
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Categoria> cq = cb.createQuery(Categoria.class);
		final Root<Categoria> root = cq.from(Categoria.class);
		
		List<Predicate> predicados = new ArrayList<Predicate>();
		
		log.debug(".getBusqueda() - ================ CONDICIONES ================");
		
		//Condición nombre
		if(busqueda.getNombre()!=null && !busqueda.getNombre().trim().isEmpty()) {
			predicados.add(cb.like(cb.upper(root.get("nombre")), '%'+busqueda.getNombre().trim().toUpperCase()+'%'));
			log.debug(".getBusqueda() - NOMBRE: " + busqueda.getNombre().trim().toUpperCase());
		}
		
		//Condición número
		if(busqueda.isConNum()) {
			
			Subquery<Categoria> subQCategoria = cq.subquery(Categoria.class);
			Root<Categoria> rootSub = subQCategoria.from(Categoria.class);
			
			//left join
			Join<Categoria, Imagen> joinSub = rootSub.join("imagenes",JoinType.LEFT);
			
			//group by
			subQCategoria.groupBy(rootSub.get("id"));
			
			//having
			Predicate condicion = null;
			Expression<? extends Long> expCount = cb.count(joinSub.get("categoria"));
			switch(busqueda.getComparador()) {
			case IGUAL:
				condicion = cb.equal(expCount, busqueda.getNum());
				break;
			case MAYOR:
				condicion = cb.greaterThan(expCount, (long)busqueda.getNum());
				break;
			case MENOR:
				condicion = cb.lessThan(expCount, (long)busqueda.getNum());
				break;
			}
			subQCategoria.having(condicion);
			
			subQCategoria.select(rootSub);
			predicados.add(cb.in(root).value(subQCategoria));
			
			log.debug(".getBusqueda() - NÚM IMG: " + busqueda.getComparador().toString() + " " + busqueda.getNum());
			
		}
		
		log.debug(".getBusqueda() - =============================================");
		
		Predicate[] predicadosArray = predicados.toArray(new Predicate[predicados.size()]);
		cq.select(root).where(predicadosArray);
		
		List<Categoria> resultados = em.createQuery(cq).getResultList();
		log.debug(".getBusqueda() - Finalizada recuperación de categorías por búsqueda: " + resultados.size());
		return resultados;
		
	}
	
	@Override
	public Categoria getPorNombre(String nombre) throws ConstraintViolationException, NotFoundException {
		
		log.debug(".getPorNombre() - Iniciando recuperación de categoría por nombre");
		
		nombre = validarNombre(nombre);
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Categoria> cq = cb.createQuery(Categoria.class);
		Root<Categoria> root = cq.from(Categoria.class);
		
		Predicate p = cb.equal(root.get("nombre"), nombre);
		
		cq.select(root);
		cq.where(p);
		
		try {
			Categoria result = em.createQuery(cq).getSingleResult();
			log.debug(".getPorNombre() - Finalizada recuperación de categoría por nombre: " + result.getNombre());
			return result;
		} catch (NoResultException e) {
			log.debug(".getPorNombre() - Finalizada recuperación de categoría por nombre (no encontrada)");
			throw new NotFoundException();
		}catch (NonUniqueResultException e) {
			log.error(".getPorNombre() - Hay más de una categoría con el mismo nombre");
			throw new ConstraintViolationException("Hay más de una categoría con el mismo nombre");
		}
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Categoria getOCrear(String nombre) throws ConstraintViolationException {
		
		log.debug(".getOCrear() - Iniciado");
		
		Categoria cat;
		try {
			cat = getPorNombre(nombre);
		} catch (NotFoundException e) {
			cat = anadir(nombre);
		}
		
		log.debug(".getOCrear() - Finalizado");
		return cat;
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Categoria anadir(String nombre) throws ConstraintViolationException {
		
		log.debug(".anadir() - Iniciando adición de categoría");
		
		nombre = validarNombre(nombre);
		
		try {
			getPorNombre(nombre);
			log.error(".anadir() - Ya hay una categoría con este nombre: " + nombre);
			throw new ConstraintViolationException("Esta categoría ya existe");
		} catch (NotFoundException e) {
			Categoria cat = new Categoria();
			cat.setNombre(nombre.trim().toUpperCase());
			em.persist(cat);
			
			//TODO quitar?
			em.flush();
			
			Categoria anadida = em.merge(cat);
			log.debug(".anadir() - Finalizada adición de categoría");
			return anadida;
		}
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Categoria editar(Categoria categoria, String nuevoNombre) throws ConstraintViolationException {
		
		log.debug(".editar() - Iniciando edición");
		
		if(categoria==null) {
			log.error(".editar() - Categoría nula");
			throw new ConstraintViolationException("Categoría nula");
		}
		
		nuevoNombre = validarNombre(nuevoNombre);
		
		Categoria cat = em.contains(categoria) ? categoria : em.merge(categoria);
		
		if(cat.getNombre().equals(nuevoNombre)) {
			log.debug(".editar() - Finalizada edición (nuevo nombre coincide con antiguo)");
			return cat;
		}
		
		try {
			getPorNombre(nuevoNombre);
			log.error(".editar() - Ya hay otra categoría con el nuevo nombre");
			throw new ConstraintViolationException("Hay otra categoría con ese nombre");
		} catch (NotFoundException e) {
		
			cat.setNombre(nuevoNombre);
			Categoria retorna = em.merge(cat);
			log.debug(".editar() - Finalizada edición: " + nuevoNombre);
			return retorna;
			
		}
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public void eliminar(Categoria cat){
		
		log.debug(".eliminar() - Iniciando eliminación de categoría");
		
		if(cat==null) {
			log.debug(".eliminar() - Finalizada eliminación de categoría (categoría nula)");
			return;
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Imagen> cu = cb.createCriteriaUpdate(Imagen.class);
		Root<Imagen> root = cu.from(Imagen.class);
		
		//Categoría nula, el método no permite poner directamente null
		Categoria proxyCat = null;
		
		cu.set(root.get("categoria"), proxyCat);
		
		cu.where(cb.equal(root.get("categoria"), cat));
		
		int alteradas = em.createQuery(cu).executeUpdate();
		log.debug(".eliminar() - Imágenes alteradas: " + alteradas);
		
		em.remove(em.contains(cat) ? cat : em.merge(cat));
		
		log.debug(".eliminar() - Finalizada eliminación de categoría");
		
	}

}
