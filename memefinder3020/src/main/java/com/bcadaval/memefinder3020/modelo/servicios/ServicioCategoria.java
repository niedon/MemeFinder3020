package com.bcadaval.memefinder3020.modelo.servicios;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.bcadaval.memefinder3020.modelo.beans.Categoria;
import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.beans.temp.CategoriaBusqueda;
import com.bcadaval.memefinder3020.modelo.dao.RepositorioCategoria;


@Service
public class ServicioCategoria {

	@Autowired RepositorioCategoria repo;
	@PersistenceContext EntityManager em;
	
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
	
	@Transactional(rollbackOn = Exception.class)
	public void crear(String nombre) {
		Categoria cat = new Categoria();
		cat.setNombre(nombre.toUpperCase());
		repo.saveAndFlush(cat);
	}
	
	@Transactional(rollbackOn = Exception.class)
	public void borrar(Categoria cat) {
		em.remove(em.contains(cat) ? cat : em.merge(cat));
	}
	
	public void renombrar(Categoria cat, String nuevoNombre) {
		
		if(nuevoNombre==null || nuevoNombre.isEmpty()) {
			throw new RuntimeException("El nuevo nombre no puede estar vacío");
		}
		
		cat.setNombre(nuevoNombre.trim().toUpperCase());
		repo.saveAndFlush(cat);
	}
	
	/*
SELECT *
FROM CATEGORIA
WHERE
CATEGORIA.ID IN (
	SELECT CATEGORIA.ID
	FROM CATEGORIA
	LEFT JOIN IMAGEN
	ON CATEGORIA.ID = IMAGEN.CATEGORIA
	GROUP BY CATEGORIA.ID
	HAVING COUNT(IMAGEN.CATEGORIA) [</>/=] [num]
)
	*/
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

}
