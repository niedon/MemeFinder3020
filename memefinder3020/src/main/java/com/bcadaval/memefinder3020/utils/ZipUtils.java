package com.bcadaval.memefinder3020.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcadaval.memefinder3020.excepciones.MemeFinderException;
import com.bcadaval.memefinder3020.modelo.beans.xml.ImagenXml;
import com.bcadaval.memefinder3020.modelo.beans.xml.Paquete;

@Component
public class ZipUtils {
	
	private static final Logger log = LogManager.getLogger(ZipUtils.class);
	
	@Autowired
	private RutasUtils rutasUtils;
	@Autowired
	private JAXBContext jaxbCtx;
	
	public void exportar(String rutaExportar, Paquete paquete) throws MemeFinderException {
		
		log.debug(".exportar() - Iniciando transformación a zip");
		
		if(paquete.getImagenes().isEmpty()) {
			throw new MemeFinderException("No se puede exportar un paquete sin imágenes");
		}
		
		File exportar = new File(rutaExportar);
		if( ! exportar.getName().endsWith(".mf3")) {
			exportar = new File(exportar.getAbsolutePath() + ".mf3");
		}
		
		
		if(exportar.exists()) {
			log.error(".exportar() - El archivo a exportar ya existe");
			throw new MemeFinderException("El archivo a exportar ya existe");
		}
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(exportar);
		} catch (FileNotFoundException e) {
			log.error(".exportar() - Error instanciando FileOutputStream: " + e.getMessage(), e);
			throw new MemeFinderException("Error interno");
		}
		
		ZipOutputStream zos = new ZipOutputStream(fos);
		
		
		try {
			
			File tempFile = File.createTempFile("xmlexport", ".xml");
			
			jaxbCtx.createMarshaller().marshal(paquete, tempFile);
			
			FileInputStream fisXml = new FileInputStream(tempFile);
			
			ZipEntry zipXML = new ZipEntry("info.xml");
			zos.putNextEntry(zipXML);
			
			byte[] bytes = new byte[1024];
			int length;
			while((length = fisXml.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
            fisXml.close();
            
            
            String subfolder = "img" + File.separator;
            
            for(ImagenXml imgXml : paquete.getImagenes()) {
            	
            	File img = rutasUtils.getFileRutaImagen(imgXml.getNombrearchivo(), imgXml.getExtension());
            	FileInputStream fisImg = new FileInputStream(img);
            	ZipEntry zipImg = new ZipEntry(subfolder + imgXml.getNombre() + "." + imgXml.getExtension());
            	zos.putNextEntry(zipImg);
            	
            	while((length = fisImg.read(bytes)) >= 0) {
                    zos.write(bytes, 0, length);
                }
                fisImg.close();
            	
            }
            
		} catch (JAXBException e) {
			log.error(".exportar() - Error transformando el archivo a XML: " + e.getMessage(), e);
			throw new MemeFinderException("Error en el proceso de exportado");
		} catch (IOException e) {
			log.error(".exportar() - Error escribiendo el archivo: " + e.getMessage(), e);
			throw new MemeFinderException("Error en el proceso de exportado");
		}finally {
			try {
				zos.close();
			} catch (IOException e1) {
			}
			try {
				fos.close();
			} catch (IOException e) {
			}
			log.debug(".exportar() - Transformación a zip finalizada");
		}
		
	}
	
}