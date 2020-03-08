package com.bcadaval.memefinder3020.modelo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcadaval.memefinder3020.modelo.beans.Imagen;

@Repository
public interface RepositorioImagen extends JpaRepository<Imagen,Integer> {

}
