package com.bcadaval.memefinder3020.modelo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;

@Repository
public interface RepositorioEtiqueta extends JpaRepository<Etiqueta,Integer>{

	@Query(
			value="SELECT COUNT(*) FROM IMAGEN_ETIQUETA WHERE IDETIQUETA = ?1",
			nativeQuery=true)
	Integer countUsosDeEtiqueta(Integer idEtiqueta);
	
}
