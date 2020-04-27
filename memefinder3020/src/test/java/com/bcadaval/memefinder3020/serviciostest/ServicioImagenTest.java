package com.bcadaval.memefinder3020.serviciostest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.bcadaval.memefinder3020.AppTest;
import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.excepciones.NotFoundException;
import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenTemp;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioCategoria;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioEtiqueta;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioImagen;
import com.bcadaval.memefinder3020.utils.RutasUtils;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = {"classpath:application-test.properties", "classpath:hibernate-test.properties"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServicioImagenTest {
	
	@Autowired private ServicioEtiqueta tServEt;
	@Autowired private ServicioCategoria tServCat;
	
	private ImagenTemp iTemp;
	private File imagen1, imagen2, imagen3, imagenBmp, imgInexistente;
	private String nombreArchivo1, nombreArchivo2, nombreArchivoBmp, nombreArchivo3;
	private String nombre1, nombre2, nombre3;
	private List<String> etiquetas1, etiquetas2, etiquetas3;
	
	@Autowired private ServicioImagen serv;
	@Autowired private RutasUtils ru;
	
	@Before
	public void preTest() {
		
		tServEt.getAll().forEach(tServEt::eliminar);
		tServCat.getAll().forEach(tServCat::eliminar);
		serv.getAll().forEach(serv::eliminar);
		
		iTemp = new ImagenTemp();
		
		File carpetaImagenes = new File(ru.RUTA_IMAGENES_AC);
		if(!carpetaImagenes.exists()) {
			carpetaImagenes.mkdirs();
		}
		for (File f : carpetaImagenes.listFiles()) {
			f.delete();
		}
		
		
		String nombreInexistente = "archivoInexistente";
		String extensionInexistente = ".jpg";
		while(imgInexistente!=null && !imgInexistente.exists()) {
			imgInexistente = new File(String.format(ru.RUTA_IMAGENES_AC, nombreInexistente+extensionInexistente));
			nombreInexistente += "0";
		}
		
		nombre1 = "dat boi";
		nombreArchivo1 = "waddup.jpg";
		etiquetas1 = Arrays.asList(new String[] {"test1","test2"});
		
		nombre2 = "dogeee";
		nombreArchivo2 = "ouch.png";
		etiquetas2 = Arrays.asList(new String[] {"etiqueta distinta","test1"});
		
		nombre3 = "El fin de la commedia";
		nombreArchivo3 = "succ.gif";
		etiquetas3 = Arrays.asList();
		
		nombreArchivoBmp = "hmmm.bmp";
		try {
			imagen1 = new File(getClass().getResource(String.format(AppTest.RUTA_TESTIMAGENES_RF, nombreArchivo1)).toURI());
			imagen2 = new File(getClass().getResource(String.format(AppTest.RUTA_TESTIMAGENES_RF, nombreArchivo2)).toURI());
			imagen3 = new File(getClass().getResource(String.format(AppTest.RUTA_TESTIMAGENES_RF, nombreArchivo3)).toURI());
			imagenBmp = new File(getClass().getResource(String.format(AppTest.RUTA_TESTIMAGENES_RF, nombreArchivoBmp)).toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException("No se ha podido cargar alguna imagen");
		}
		
		
		
	}
	
	private void rellenarITemp(int numImagen) {
		
		switch(numImagen) {
		case 1:
			iTemp.setImagen(imagen1);
			iTemp.setNombre(nombre1);
			iTemp.getEtiquetasString().clear();
			iTemp.getEtiquetasString().addAll(etiquetas1);
			break;
		case 2:
			iTemp.setImagen(imagen2);
			iTemp.setNombre(nombre2);
			iTemp.getEtiquetasString().clear();
			iTemp.getEtiquetasString().addAll(etiquetas2);
			break;
		case 3:
			iTemp.setImagen(imagen3);
			iTemp.setNombre(nombre3);
			iTemp.getEtiquetasString().clear();
			iTemp.getEtiquetasString().addAll(etiquetas3);
			break;
		}
		
	}
	
	@Test
	public void t01_servicioNoNull() {
		assertTrue(serv!=null);
	}
	
	@Test
	public void t02_testGets() {
		
		//getAll()
		
		if(!serv.getAll().isEmpty()) {
			fail("Se han rescatado imágenes con la BD vacía");
		}
		Imagen im1 = null, im2 = null, im3 = null;
		
		try {
			rellenarITemp(1);
			im1 = serv.anadir(iTemp);
			rellenarITemp(2);
			im2 = serv.anadir(iTemp);
			rellenarITemp(3);
			im3 = serv.anadir(iTemp);
		} catch (ConstraintViolationException e) {
			fail("No se han podido añadir las imágenes para el test");
		}
		
		if(serv.getAll().size() != 3) {
			fail("El método no ha rescatado las 3 imágenes añadidas");
		}
		
		//getAllIds()
		
		List<Integer> listaIds = Arrays.asList(new Integer[] {im1.getId(), im2.getId(), im3.getId()});
		List<Integer> listaIdsRescatadas = serv.getAllIds();
		
		if( ! listaIds.containsAll(listaIdsRescatadas)) {
			fail("Las IDs rescatadas no coinciden con las de las imágenes añadidas");
		}
		
		//getAllPorId()
		
		
		
		try {
			serv.getAllPorId(null);
			fail("El método no lanza excepción al pasar lista nula");
		} catch (ConstraintViolationException e) {
		}
		
		try {
			if( ! serv.getAllPorId(Arrays.asList()).isEmpty()) {
				fail("El método no devuelve una lista vacía");
			}
		} catch (ConstraintViolationException e) {
			fail("El método lanza excepción con una lista vacía");
		}
		
		try {
			List<Imagen> listaRescatadas = serv.getAllPorId(listaIds); 
			if(listaRescatadas.size() != 3) {
				fail("El método no devuelve las imágenes de las ids pasadas por parámetro");
			}
			
			for(Imagen imgRes : listaRescatadas) {
				if( ! listaIds.contains(imgRes.getId())){
					fail("No se ha rescatado la imagen con id: " + imgRes.getId());
				}
			}
		} catch (ConstraintViolationException e) {
			fail("El método lanza excepción con un parámetro válido");
		}
		
		
		Integer noExiste = 1;
		while(listaIds.contains(noExiste)) {
			noExiste++;
		}
		
		
		try {
			if(serv.getAllPorId(Arrays.asList(noExiste)).size()!=0) {
				fail("Se ha rescatado de BD una imagen inexistente");
			}
		} catch (ConstraintViolationException e) {
			fail("Se ha lanzado excepción al buscar una imagen inexistente");
		}
		
		//getPorId()
		
		try {
			serv.getPorId(null);
			fail("No se ha lanzado excepción para parámetro null");
		} catch (ConstraintViolationException e) {
			
		} catch (NotFoundException e) {
			fail("Se ha lanzado NotFound para un parámetro inváludo (null)");
		}
		
		try {
			if(serv.getPorId(im1.getId())==null) {
				fail("El método ha devuelto null para imagen existente");
			}
		} catch (ConstraintViolationException e) {
			fail("Se ha lanzado ConstraintViolation para parámetro válido");
		} catch (NotFoundException e) {
			fail("Se ha lanzado NotFound para parámetro válido e imagen existente");
		}
		
		try {
			serv.getPorId(noExiste);
			fail("No se ha lanzado excepción para imagen inexistente");
		} catch (ConstraintViolationException e) {
			fail("Se ha lanzado ConstraintViolation para parámetro válido");
		} catch (NotFoundException e) {
			
		}
		
		//getUltimas()
		
		try {
			serv.getUltimas(-1);
			fail("No se ha lanzado excepción para parámetro inválido");
		} catch (ConstraintViolationException e) {
			
		}
		
		try {
			serv.getUltimas(0);
			fail("No se ha lanzado excepción para parámetro inválido");
		} catch (ConstraintViolationException e) {
			
		}
		
		try {
			List<Imagen> temp = serv.getUltimas(1);
			if(temp.isEmpty()) {
				fail("No se ha recuperado 1 imagen pasada por parámetro");
			}else if(temp.size()!=1) {
				fail("Se ha recuperado más de una imagen");
			}else if( ! listaIds.contains(temp.get(0).getId())) {
				fail("La imagen recuperada no tiene la ID en la lista de IDs de más arriba (?)");
			}
		} catch (ConstraintViolationException e) {
			fail("Se ha lanzado ConstraintViolation para parámetro válido");
		}
		
		try {
			List<Imagen> temp = serv.getUltimas(4);
			if(temp.size()!=3) {
				fail("Se ha recuperado un número distinto a las imágenes en BD: " + temp.size());
			}
		} catch (ConstraintViolationException e) {
			fail("Se ha lanzado ConstraintViolation para parámetro válido");
		}
		
		//Fin del test, limpiar
		
		serv.getAll().forEach(serv::eliminar);
		if( ! serv.getAll().isEmpty()) {
			fail("Se han recuperado imágenes eliminadas");
		}
		
	}
	
	@Test
	public void t03_testAnadirEliminar_File() {
		
		try {
			serv.anadir(null);
			fail("El método ha permitido añadir al pasar ImagenTemp nulo");
		} catch (ConstraintViolationException e) {
			
		}
		
		rellenarITemp(1);
		iTemp.setImagen(null);
		try {
			serv.anadir(iTemp);
			fail("El método ha permitido añadir una imagen (File) nula");
		} catch (ConstraintViolationException e) {
			
		}
		
		rellenarITemp(1);
		iTemp.setImagen(imgInexistente);
		try {
			serv.anadir(iTemp);
			fail("El método ha permitido añadir una imagen inexistente");
		} catch (ConstraintViolationException e) {
			
		}
		
		rellenarITemp(1);
		iTemp.setImagen(imagen1.getParentFile());
		try {
			serv.anadir(iTemp);
			fail("El método ha permitido añadir un File directorio");
		} catch (ConstraintViolationException e) {
			
		}
		
		Imagen im1 = null, im2 = null, im3 = null;
		
		rellenarITemp(1);
		try {
			im1 = serv.anadir(iTemp);
		} catch (ConstraintViolationException e) {
			fail("El método no ha permitido añadir la imagen 1");
		}
		
		rellenarITemp(2);
		try {
			im2 = serv.anadir(iTemp);
		} catch (ConstraintViolationException e) {
			fail("El método no ha permitido añadir la imagen 2");
		}
		
		rellenarITemp(3);
		try {
			im3 = serv.anadir(iTemp);
		} catch (ConstraintViolationException e) {
			fail("El método no ha permitido añadir la imagen 3");
		}
		
		File fileIm1 = ru.getFileDeImagen(im1),
				fileIm2 = ru.getFileDeImagen(im2),
				fileIm3 = ru.getFileDeImagen(im3); 
		
		if( ! fileIm1.exists()) {
			fail("No se ha copiado el archivo de imagen 1");
		}
		if( ! fileIm2.exists()) {
			fail("No se ha copiado el archivo de imagen 2");
		}
		if( ! fileIm3.exists()) {
			fail("No se ha copiado el archivo de imagen 3");
		}
		
		serv.getAll().forEach(serv::eliminar);
		
		if(fileIm1.exists()) {
			fail("No se ha eliminado el archivo de imagen 1");
		}
		if(fileIm2.exists()) {
			fail("No se ha eliminado el archivo de imagen 2");
		}
		if(fileIm3.exists()) {
			fail("No se ha eliminado el archivo de imagen 3");
		}
		
		rellenarITemp(1);
		iTemp.setImagen(imagenBmp);
		try {
			im1 = serv.anadir(iTemp);
		} catch (ConstraintViolationException e) {
			fail("No se ha permitido añadir una imagen BMP");
		}
		
		serv.getAll().forEach(serv::eliminar);
		
	}
	
	@Test
	public void t04_testAnadirEliminar_Nombre() {
		
		Imagen im1=null, im2=null, im3=null;
		
		rellenarITemp(1);		
		try {
			im1 = serv.anadir(iTemp);
			if( ! nombre1.equals(im1.getNombre())) {
				fail("La imagen no se ha guardado con el nombre asignado");
			}
		} catch (ConstraintViolationException e) {
			fail("El método no ha permitido añadir la imagen 1");
		}
		
		rellenarITemp(2);
		iTemp.setNombre(null);
		try {
			im2 = serv.anadir(iTemp);
			if( ! nombreArchivo2.substring(0,nombreArchivo2.indexOf('.')).equals(im2.getNombre())) {
				fail("La imagen no se ha guardado con el nombre por defecto");
			}
		} catch (ConstraintViolationException e) {
			fail("El método no ha permitido añadir la imagen 2");
		}
		String sesentaYCuatroAes = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		rellenarITemp(3);
		iTemp.setNombre(sesentaYCuatroAes + "B");
		try {
			im3 = serv.anadir(iTemp);
			if(im3.getNombre().length() != 64) {
				fail("El nombre de la imagen no se ha limitado a 64 caracteres");
			}
		} catch (ConstraintViolationException e) {
			fail("El método no ha permitido añadir la imagen 3");
		}
		
		serv.getAll().forEach(serv::eliminar);
		
		rellenarITemp(1);
		iTemp.setNombre(" " + iTemp.getNombre());
		try {
			im1 = serv.anadir(iTemp);
			if( ! im1.getNombre().equals(nombre1)) {
				fail("El método no trimea las cadenas (espacio al principio)");
			}
		} catch (ConstraintViolationException e) {
			fail("El método no ha permitido añadir la imagen 1");
		}
		
		rellenarITemp(2);
		iTemp.setNombre(iTemp.getNombre() + " ");
		try {
			im2 = serv.anadir(iTemp);
			if( ! im2.getNombre().equals(nombre2)) {
				fail("El método no trimea las cadenas (espacio al final)");
			}
		} catch (ConstraintViolationException e) {
			fail("El método no ha permitido añadir la imagen 2");
		}
		
		rellenarITemp(3);
		iTemp.setNombre(" " + sesentaYCuatroAes);
		try {
			im1 = serv.anadir(iTemp);
			if( ! im1.getNombre().equals(sesentaYCuatroAes)) {
				fail("El método trimea de forma incorrecta las cadenas (quizás hace substring antes de trimear)");
			}
		} catch (ConstraintViolationException e) {
			fail("El método no ha permitido añadir la imagen 3");
		}
		
		serv.getAll().forEach(serv::eliminar);
		
		String nombreConPunto = "nombrecon.punto";
		rellenarITemp(1);
		iTemp.setNombre(nombreConPunto);
		try {
			im1 = serv.anadir(iTemp);
			if( ! nombreConPunto.equals(im1.getNombre())) {
				fail("El método no acepta nombres con puntos");
			}
		} catch (ConstraintViolationException e) {
			fail("El método no ha permitido añadir la imagen 1");
		}
		
		serv.getAll().forEach(serv::eliminar);
		
	}
	
	@Test
	public void t05_testAnadirEliminar_Etiquetas() {
		
		Imagen im1=null, im2=null;
		
		rellenarITemp(1);
		try {
			im1 = serv.anadir(iTemp);
			if(etiquetas1.size() != im1.getEtiquetas().size()) {
				fail("La imagen 1 no tiene el número de etiquetas añadido (" + etiquetas1.size() + ")");
			}
			
		} catch (ConstraintViolationException e) {
			fail("El método no ha permitido añadir la imagen 1");
		}
		
		rellenarITemp(2);
		try {
			im2 = serv.anadir(iTemp);
			if(etiquetas2.size() != im2.getEtiquetas().size()) {
				fail("La imagen 2 no tiene el número de etiquetas añadido (" + etiquetas2.size() + ")");
			}
		} catch (ConstraintViolationException e) {
			fail("El método no ha permitido añadir la imagen 2");
		}
		
		serv.eliminar(im2);
		
		im1 = serv.editar(im1);
		if(etiquetas1.size() != im1.getEtiquetas().size()) {
			fail("Se han eliminado una etiqueta común al eliminar una imagen que la contenía");
		}
		
		serv.getAll().forEach(serv::eliminar);
		
	}
	
	@Test
	public void t06_testAnadirEliminar_Categorias() {
		
		Imagen im1=null, im2=null;
		
		String categoria = "CATEGORÍA CUALQUIERA";
		
		rellenarITemp(1);
		iTemp.setCategoria(categoria);
		try {
			im1 = serv.anadir(iTemp);
		} catch (ConstraintViolationException e) {
			fail("El método no ha permitido añadir la imagen 1");
		}
		
		rellenarITemp(2);
		iTemp.setCategoria(categoria);
		try {
			im2 = serv.anadir(iTemp);
		} catch (ConstraintViolationException e) {
			fail("El método no ha permitido añadir la imagen 2");
		}
		
		if(im1.getCategoria().getId() != im2.getCategoria().getId()) {
			fail("Se han creado dos categorías distintas para una misma entrada de texto en ImagenTemp");
		}
		
		serv.eliminar(im1);
		
		try {
			im2 = serv.getPorId(im2.getId());
			if(im2.getCategoria()==null) {
				fail("Se ha eliminado una categoría al eliminar una imagen con dicha categoría");
			}
		} catch (ConstraintViolationException e) {
			fail("ConstraintViolation con parámetro válido");
		} catch (NotFoundException e) {
			fail("El método no ha permitido rescatar una imagen existente");
		}
		
		serv.getAll().forEach(serv::eliminar);
		
	}
	
	@Test
	public void t06_testEliminarSobrecargado() {
		
		Integer estaVariableSoloExisteParaImpedirQueElEditorMeDeUnFalloDeCompilacionDiciendoQueLaLlamadaAlMetodoEsAmbigua = null;
		try {
			serv.eliminar(estaVariableSoloExisteParaImpedirQueElEditorMeDeUnFalloDeCompilacionDiciendoQueLaLlamadaAlMetodoEsAmbigua);
			fail("El método no lanza excepción ante un valor nulo como parámetro");
		} catch (ConstraintViolationException e1) {
			
		}
		
		Imagen im1 = null;
		
		rellenarITemp(1);
		try {
			im1 = serv.anadir(iTemp);
		} catch (ConstraintViolationException e) {
			fail("El método no ha permitido rescatar una imagen existente");
		}
		
		try {
			serv.eliminar(im1.getId());
			
			if( ! serv.getAll().isEmpty()) {
				fail("No se ha eliminado la imagen");
			}
			
		} catch (ConstraintViolationException e) {
			fail("No se ha permitido eliminar una imagen existente");
		}
		
		try {
			serv.eliminar(10);
			fail("El método no lanza una excepción ante una ID que no existe");
		} catch (ConstraintViolationException e) {
			
		}
		
	}
	
}
