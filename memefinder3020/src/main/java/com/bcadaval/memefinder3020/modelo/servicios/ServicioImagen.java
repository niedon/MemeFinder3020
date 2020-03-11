package com.bcadaval.memefinder3020.modelo.servicios;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;
import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenTemp;
import com.bcadaval.memefinder3020.modelo.dao.RepositorioImagen;
import com.bcadaval.memefinder3020.utils.Constantes;

@Service
public class ServicioImagen {

	@Autowired RepositorioImagen repo;
	
	@Autowired ServicioEtiqueta servicioEtiqueta;
	
	public List<Imagen> getAll(){
		return repo.findAll();
	}
	
	public List<Imagen> getAllPorId(Iterable<Integer> listaId){
		return repo.findAllById(listaId);
	}
	
	@Transactional(rollbackOn = Exception.class)
	public void anadirImagen(ImagenTemp iTemp) {
		
		File archivoImagen = iTemp.getImagen();
		
		Imagen img = new Imagen();
		String nombre = iTemp.getNombre();
		img.setNombre(nombre.length()>64 ? nombre.substring(0,64) : nombre);
		img.setExtension(archivoImagen.getName().substring(archivoImagen.getName().lastIndexOf('.')+1));
		//TODO img.setCategoria(categoria);
		img.setFecha(LocalDateTime.now());
		
		Imagen imgInsertada = repo.save(img);
		
		File copiada = new File(Constantes.RUTA_IMAGENES + "\\" + imgInsertada.getId() + '.' + imgInsertada.getExtension());
		
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
}
