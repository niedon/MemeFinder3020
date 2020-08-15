package com.bcadaval.memefinder3020.utils;

import java.io.File;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bcadaval.memefinder3020.excepciones.MemeFinderException;
import com.bcadaval.memefinder3020.modelo.beans.Categoria;
import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;
import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.beans.xml.ImagenXml;
import com.bcadaval.memefinder3020.modelo.beans.xml.Paquete;

@Component
public class XmlUtils {
	
	@Value("${build.version}")
	private String version;
	
	public Paquete convertir(List<Imagen> listaImagenes) throws MemeFinderException {
		
		if(listaImagenes==null) {
			throw new MemeFinderException("Se ha pasado una referencia nula");
		}
		
		if(listaImagenes.isEmpty()) {
			throw new MemeFinderException("No se pueden exportar listas vacías");
		}
		
		Paquete retorna = new Paquete();
		retorna.setVersion(version);
		retorna.setNombre("nombreTemp");
		
		ArrayList<ImagenXml> insertImagenes = new ArrayList<ImagenXml>(listaImagenes.size());
		
		for (int i=0; i < listaImagenes.size(); i++) {
			
			Imagen img = listaImagenes.get(i);
			
			ImagenXml imgXml = new ImagenXml();
			
			imgXml.setNum(i);
			imgXml.setFecha(Date.from(img.getFecha().atZone(ZoneId.systemDefault()).toInstant()));
			imgXml.setNombre(img.getNombre());
			imgXml.setNombrearchivo(img.getId()+"");
			imgXml.setExtension(img.getExtension());
			
			Categoria cat = img.getCategoria();
			if(cat!=null) {
				imgXml.setCategoria(cat.getNombre());
			}
			Set<Etiqueta> ets = img.getEtiquetas();
			if(ets != null && ! ets.isEmpty()) {
				imgXml.setEtiquetas(ets.stream().map(Etiqueta::getNombre).collect(Collectors.joining(",")));
			}
			
			insertImagenes.add(imgXml);
			
		}
		
		if( ! insertImagenes.isEmpty()) {
			retorna.setImagenes(insertImagenes);
		}
		
		return retorna;
		
	}
	
	public void validar(File mf3) throws MemeFinderException {
		//TODO hacer cuando cambie la estructura del XML
	}

}
