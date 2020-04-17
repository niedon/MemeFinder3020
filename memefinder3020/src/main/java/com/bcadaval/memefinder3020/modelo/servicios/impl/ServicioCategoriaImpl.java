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

	@PersistenceContext EntityManager em;
	
	@Override
	public List<Categoria> getAll(){
		CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Categoria> cq = cb.createQuery(Categoria.class);
        Root<Categoria> rootEntry = cq.from(Categoria.class);
        cq.select(rootEntry);
        return em.createQuery(cq).getResultList();
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
	public List<Categoria> getBusqueda(CategoriaBusqueda busqueda){
		
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Categoria> cq = cb.createQuery(Categoria.class);
		final Root<Categoria> root = cq.from(Categoria.class);
		
		List<Predicate> predicados = new ArrayList<Predicate>();
		
		//Condición nombre
		if(busqueda.getNombre()!=null && !busqueda.getNombre().isEmpty()) {
			predicados.add(cb.like(cb.upper(root.get("nombre")), '%'+busqueda.getNombre().toUpperCase()+'%'));
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
		}
		
		Predicate[] predicadosArray = predicados.toArray(new Predicate[predicados.size()]);
		cq.select(root).where(predicadosArray);
		
		return em.createQuery(cq).getResultList();
		
	}
	
	@Override
	public Categoria getPorNombre(String nombre) throws ConstraintViolationException, NotFoundException {
		
		nombre = validarNombre(nombre);
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Categoria> cq = cb.createQuery(Categoria.class);
		Root<Categoria> root = cq.from(Categoria.class);
		
		Predicate p = cb.equal(root.get("nombre"), nombre);
		
		cq.select(root);
		cq.where(p);
		
		try {
			Categoria result = em.createQuery(cq).getSingleResult();
			return result;
		} catch (NoResultException e) {
			throw new NotFoundException();
		}catch (NonUniqueResultException e) {
			throw new ConstraintViolationException("Hay más de una categoría con el mismo nombre");
			//TODO al log pero ya
		}
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Categoria getOCrear(String nombre) throws ConstraintViolationException {
		
		try {
			return getPorNombre(nombre);
		} catch (NotFoundException e) {
			return anadir(nombre);
		}
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Categoria anadir(String nombre) throws ConstraintViolationException {
		
		nombre = validarNombre(nombre);
		
		try {
			getPorNombre(nombre);
			throw new ConstraintViolationException("Esta categoría ya existe");
		} catch (NotFoundException e) {
			Categoria et = new Categoria();
			et.setNombre(nombre.trim().toUpperCase());
			em.persist(et);
			
			//TODO quitar?
			em.flush();
			
			return et;
		}
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Categoria editar(Categoria categoria, String nuevoNombre) throws ConstraintViolationException {
		
		if(categoria==null) {
			throw new ConstraintViolationException("Categoría nula");
		}
		
		nuevoNombre = validarNombre(nuevoNombre);
		
		Categoria cat = em.contains(categoria) ? categoria : em.merge(categoria);
		
		if(cat.getNombre().equals(nuevoNombre)) {
			return cat;
		}
		
		try {
			getPorNombre(nuevoNombre);
			throw new ConstraintViolationException("Hay otra categoría con ese nombre");
		} catch (NotFoundException e) {
		
			cat.setNombre(nuevoNombre);
			return em.merge(cat);
			
		}
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public void eliminar(Categoria cat){
		
		if(cat==null) {
			return;
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Imagen> cu = cb.createCriteriaUpdate(Imagen.class);
		Root<Imagen> root = cu.from(Imagen.class);
		
		
		Categoria proxyCat = null;
		
		cu.set(root.get("categoria"), proxyCat);
		
		cu.where(cb.equal(root.get("categoria"), cat));
		
		em.createQuery(cu).executeUpdate();
		//TODO añadir al log imágenes afectadas
		
		
		em.remove(em.contains(cat) ? cat : em.merge(cat));
	}

}
