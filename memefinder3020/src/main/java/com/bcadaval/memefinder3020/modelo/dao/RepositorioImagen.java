package com.bcadaval.memefinder3020.modelo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bcadaval.memefinder3020.modelo.beans.Imagen;

@Repository
public interface RepositorioImagen extends JpaRepository<Imagen,Integer> {

	@Query(value="SELECT ID FROM IMAGEN",
			nativeQuery = true)
	List<Integer> getAllIds();
	
}
