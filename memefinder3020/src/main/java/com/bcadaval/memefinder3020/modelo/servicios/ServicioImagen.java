package com.bcadaval.memefinder3020.modelo.servicios;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;
import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenBusqueda;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenTemp;
import com.bcadaval.memefinder3020.modelo.dao.RepositorioImagen;
import com.bcadaval.memefinder3020.utils.Constantes;
import com.bcadaval.memefinder3020.utils.IOUtils;

@Service
public class ServicioImagen {

	@Autowired RepositorioImagen repo;
	@PersistenceContext EntityManager em;
	
	@Autowired ServicioEtiqueta servicioEtiqueta;
	@Autowired ServicioCategoria servicioCategoria;
	
	public List<Imagen> getAll(){
		return repo.findAll();
	}
	
	public List<Imagen> getAllPorId(Iterable<Integer> listaId){
		return repo.findAllById(listaId);
	}
	
	public Imagen getPorId(Integer id) {
		return repo.findById(id).get();
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Imagen save(Imagen imagen) {
		return repo.saveAndFlush(imagen);
	}
	
	@Transactional(rollbackOn = Exception.class)
	public void anadirImagen(ImagenTemp iTemp) {
		
		File archivoImagen = iTemp.getImagen();
		
		Imagen img = new Imagen();
		String nombre = iTemp.getNombre();
		img.setNombre(nombre.length()>64 ? nombre.substring(0,64) : nombre);
		img.setExtension(archivoImagen.getName().substring(archivoImagen.getName().lastIndexOf('.')+1));
		if(iTemp.getCategoria()!=null &&  ! iTemp.getCategoria().isEmpty()) {
			img.setCategoria(servicioCategoria.getOCrear(iTemp.getCategoria()));
		}
		img.setFecha(LocalDateTime.now());
		
		Imagen imgInsertada = repo.save(img);
		
		File copiada = IOUtils.getFileDeImagen(imgInsertada);
		try {
			copiada.getParentFile().mkdirs();
			FileSystemUtils.copyRecursively(archivoImagen, copiada);
		} catch (IOException e1) {
			throw new RuntimeException("Archivo no copiado");
		}
		
		iTemp.getEtiquetas().forEach(e -> {
			Etiqueta et = servicioEtiqueta.getPorNombre(e.getNombre());
			
			if(et==null) {
				Etiqueta et1 = servicioEtiqueta.anadir(e.getNombre());
				imgInsertada.getEtiquetas().add(et1);
			}else {
				Etiqueta et2 = servicioEtiqueta.getPorNombre(e.getNombre());
				imgInsertada.getEtiquetas().add(et2);
			}
		});
		
		repo.saveAndFlush(imgInsertada);
		
	}
	
	public void borrarImagen(Imagen imagen) {
		repo.delete(imagen);
	}
	
	public void sustituirImagen(File fNueva, Imagen imgOriginal) throws IOException {
		Files.copy(fNueva.toPath(), FileSystems.getDefault().getPath(
				Constantes.RUTA_IMAGENES.toString(),
				String.format("%d.%s", imgOriginal.getId(),imgOriginal.getExtension())),
				StandardCopyOption.REPLACE_EXISTING);
	}
	
	public Page<Imagen> getBusqueda(ImagenBusqueda busqueda, Pageable pageable){
		
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Imagen> cq = cb.createQuery(Imagen.class);
		final Root<Imagen> root = cq.from(Imagen.class);
		
		List<Predicate> predicados = new ArrayList<Predicate>();
		
		if(busqueda.getNombre() != null) {
			predicados.add(cb.like(cb.upper(root.get("nombre")), '%'+busqueda.getNombre().toUpperCase()+'%'));
		}
		
		if(busqueda.isBuscarSinCategoria()) {
			predicados.add(cb.isNull(root.get("categoria")));
		}else if(busqueda.getCategoria()!=null) {
			predicados.add(cb.equal(root.get("categoria"), busqueda.getCategoria().getId()));
		}
		
		if(busqueda.getFechaDespues()!=null) {
			predicados.add(cb.greaterThan(root.get("fecha"), busqueda.getFechaDespues()));
		}
		
		if(busqueda.getFechaAntes()!=null) {
			predicados.add(cb.lessThan(root.get("fecha"), busqueda.getFechaAntes()));
		}
		
		if(busqueda.isBuscarSinEtiquetas()) {
			predicados.add(cb.isEmpty(root.get("etiquetas")));
		}else {
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
	
	@Transactional(rollbackOn = Exception.class)
	public void borrarPorId(Integer id) {
		Imagen imgBorrar = repo.findById(id).get();
		//TODO si no existe lanzar excepción
		repo.deleteById(id);
		try {
			Files.deleteIfExists(FileSystems.getDefault().getPath(Constantes.RUTA_IMAGENES.toString(),
					String.format("%d.%s", imgBorrar.getId(),imgBorrar.getExtension())));
		} catch (IOException e) {
			e.printStackTrace();
			//TODO logger archivo no existe
			throw new RuntimeException("No se ha podido borrar el archivo");
		}
		
	}
	
}
