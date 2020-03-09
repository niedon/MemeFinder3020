package com.bcadaval.memefinder3020.modelo.servicios;

import java.io.File;
import java.io.IOException;
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
	
	@Transactional(rollbackOn = Exception.class)
	public void anadirImagen(ImagenTemp iTemp) {
		
		File archivoImagen = iTemp.getImagen();
		
		Imagen img = new Imagen();
		img.setNombre(iTemp.getNombre());
		img.setExtension(archivoImagen.getName().substring(archivoImagen.getName().lastIndexOf('.')+1));
		//TODO img.setCategoria(categoria);
		
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
				System.out.println("id etiqueta nueva: " + et1.getId());
				imgInsertada.getEtiquetas().add(et1);
			}else {
				Etiqueta et2 = servicioEtiqueta.getPorNombre(e.getNombre());
				System.out.println("id etiqueta repe: " + et2.getId());
				imgInsertada.getEtiquetas().add(et2);
			}
		});
		
		repo.saveAndFlush(imgInsertada);
		
	}
}
