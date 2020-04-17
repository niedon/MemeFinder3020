package com.bcadaval.memefinder3020.serviciostest;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import com.bcadaval.memefinder3020.modelo.beans.Categoria;
import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenTemp;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioCategoria;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioEtiqueta;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioImagen;
import com.bcadaval.memefinder3020.utils.RutasUtils;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = {"classpath:config/application-test.properties", "classpath:config/hibernate-test.properties"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntegracionImagenCategoria {

	@Autowired private ServicioImagen servImagen;
	@Autowired private ServicioEtiqueta servEtiqueta;
	@Autowired private ServicioCategoria servCategoria;
	@Autowired private RutasUtils ru;
	
	private ImagenTemp iTemp;
	private File imagen1, imagen2, imagen3, imagen4;
	private String nombreArchivo1, nombreArchivo2, nombreArchivo3, nombreArchivo4;
	private String nombre1, nombre2, nombre3, nombre4;
	private String categoria1, categoria2, categoria3, categoria4;
	private List<String> etiquetas1, etiquetas2, etiquetas3, etiquetas4;
	
	@Before
	public void preTest() {
		
		servImagen.getAll().forEach(servImagen::eliminar);
		servEtiqueta.getAll().forEach(servEtiqueta::eliminar);
		servCategoria.getAll().forEach(servCategoria::eliminar);
		
		iTemp = new ImagenTemp();
		
		File carpetaImagenes = new File(ru.RUTA_IMAGENES_AC);
		if(!carpetaImagenes.exists()) {
			carpetaImagenes.mkdirs();
		}
		for (File f : carpetaImagenes.listFiles()) {
			f.delete();
		}
		
		nombre1 = "dat boi";
		nombreArchivo1 = "waddup.jpg";
		etiquetas1 = Arrays.asList(new String[] {"test1","test2"});
		categoria1 = "DANK";
		
		nombre2 = "dogeee";
		nombreArchivo2 = "ouch.png";
		etiquetas2 = Arrays.asList(new String[] {"etiqueta distinta","test1"});
		categoria2 = null;
		
		nombre3 = "El fin de la commedia";
		nombreArchivo3 = "succ.gif";
		etiquetas3 = Arrays.asList(new String[] {"test2"});
		categoria3 = "DANK";
		
		nombre4 = "pensante";
		nombreArchivo4 = "hmmm.bmp";
		etiquetas4 = Arrays.asList();
		categoria4 = "emoji";
		try {
			imagen1 = new File(getClass().getResource(String.format(AppTest.RUTA_TESTIMAGENES_RF, nombreArchivo1)).toURI());
			imagen2 = new File(getClass().getResource(String.format(AppTest.RUTA_TESTIMAGENES_RF, nombreArchivo2)).toURI());
			imagen3 = new File(getClass().getResource(String.format(AppTest.RUTA_TESTIMAGENES_RF, nombreArchivo3)).toURI());
			imagen4 = new File(getClass().getResource(String.format(AppTest.RUTA_TESTIMAGENES_RF, nombreArchivo4)).toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException("No se ha podido cargar alguna imagen");
		}
		
		
		try {
			rellenarITemp(1);
			servImagen.anadir(iTemp);
			rellenarITemp(2);
			servImagen.anadir(iTemp);
			rellenarITemp(3);
			servImagen.anadir(iTemp);
			rellenarITemp(4);
			servImagen.anadir(iTemp);
		} catch (ConstraintViolationException e) {
			e.printStackTrace();
			fail("No se han podido añadir las imágenes a la BD");
		}
		
	}
	
	private void rellenarITemp(int numImagen) {
		
		switch(numImagen) {
		case 1:
			iTemp.setImagen(imagen1);
			iTemp.setNombre(nombre1);
			iTemp.getEtiquetasString().clear();
			iTemp.getEtiquetasString().addAll(etiquetas1);
			iTemp.setCategoria(categoria1);
			break;
		case 2:
			iTemp.setImagen(imagen2);
			iTemp.setNombre(nombre2);
			iTemp.getEtiquetasString().clear();
			iTemp.getEtiquetasString().addAll(etiquetas2);
			iTemp.setCategoria(categoria2);
			break;
		case 3:
			iTemp.setImagen(imagen3);
			iTemp.setNombre(nombre3);
			iTemp.getEtiquetasString().clear();
			iTemp.getEtiquetasString().addAll(etiquetas3);
			iTemp.setCategoria(categoria3);
			break;
		case 4:
			iTemp.setImagen(imagen4);
			iTemp.setNombre(nombre4);
			iTemp.getEtiquetasString().clear();
			iTemp.getEtiquetasString().addAll(etiquetas4);
			iTemp.setCategoria(categoria4);
			break;
		}
		
	}
	
	@Test
	public void t01_testNoNull() {
		if(servCategoria==null || servEtiqueta==null || servImagen==null) {
			fail("Algún servicio es nulo");
		}
	}
	
	@Test
	public void t02_fusionarExcepciones() {
		
		try {
			servImagen.fusionarCategorias(null, "nombreCualquiera");
			fail("Se ha aceptado una lista nula");
		} catch (ConstraintViolationException e) {
			
		}
		
		try {
			servImagen.fusionarCategorias(new ArrayList<Categoria>(), "nombreCualquiera");
			fail("Se ha aceptado una lista vacía");
		} catch (ConstraintViolationException e) {
			
		}
		
	}
	
	@Test
	public void t03_fusionarSinNombre() {
		
		Categoria dank=null, emoji=null;
		
		try {
			dank = servCategoria.getPorNombre("dank");
			emoji = servCategoria.getPorNombre("emoji");
		} catch (ConstraintViolationException e) {
			fail("Error recuperando una categoría existente");
		} catch (NotFoundException e) {
			fail("No se ha rescatado una categoría existente");
		}
	
		//Dank es la categoría más antigua
		try {
			servImagen.fusionarCategorias(Arrays.asList(new Categoria[] {dank, emoji}), null);
		} catch (ConstraintViolationException e) {
			fail("Se ha lanzado excepción con parámetros válidos");
		}
		
		try {
			servCategoria.getPorNombre("emoji");
			fail("Se ha encontrado una categoría que debería haberse eliminado");
		} catch (ConstraintViolationException e) {
			fail("Se ha lanzado excepción con parámetros válidos");
		} catch (NotFoundException e) {
			
		}
		
		servImagen.getAll().forEach(el -> {
			if(el.getNombre().equals("pensante")) {
				Categoria cat = el.getCategoria();
				
				if(cat==null) {
					fail("Se ha asignado categoría nula cuando debería haberse asignado categoría dank");
				}else if( ! cat.getNombre().equals("DANK")) {
					fail("No se ha cambiado la categoría de la imagen \"pensante\"");
				}
			}
		});
		
	}
	
	@Test
	public void t04_fusionarConNombre() {
		
		Categoria dank=null, emoji=null;
		String nuevoNombre = "nombre poderoso";
		Categoria nueva = null;
		
		try {
			dank = servCategoria.getPorNombre("dank");
			emoji = servCategoria.getPorNombre("emoji");
		} catch (ConstraintViolationException e) {
			fail("Error recuperando una categoría existente");
		} catch (NotFoundException e) {
			fail("No se ha rescatado una categoría existente");
		}
	
		//Dank es la categoría más antigua
		try {
			servImagen.fusionarCategorias(Arrays.asList(new Categoria[] {dank, emoji}), nuevoNombre);
		} catch (ConstraintViolationException e) {
			fail("Se ha lanzado excepción con parámetros válidos");
		}
		
		try {
			servCategoria.getPorNombre("emoji");
			fail("Se ha encontrado una categoría que debería haberse eliminado");
		} catch (ConstraintViolationException e) {
			fail("Se ha lanzado excepción con parámetros válidos");
		} catch (NotFoundException e) {
			
		}
		
		try {
			nueva = servCategoria.getPorNombre(nuevoNombre);
		} catch (ConstraintViolationException e) {
			fail("Se ha lanzado excepción con parámetros válidos");
		} catch (NotFoundException e) {
			fail("La categoría no se ha renombrado");
		}
		
		final Categoria nuevaFinal = nueva;
		
		servImagen.getAll().forEach(el -> {
			if(el.getNombre().equals("pensante")) {
				Categoria cat = el.getCategoria();
				
				if(cat==null) {
					fail("Se ha asignado categoría nula cuando debería haberse asignado categoría renombrada");
				}else if( ! cat.getNombre().equals(nuevaFinal.getNombre())) {
					fail("No se ha cambiado la categoría de la imagen \"pensante\"");
				}
			}else if(el.getNombre().equals("dat boi")) {
				Categoria cat = el.getCategoria();
				
				if(cat==null) {
					fail("Se ha asignado categoría nula cuando debería haberse asignado categoría renombrada");
				}else if( ! cat.getNombre().equals(nuevaFinal.getNombre())) {
					fail("No se ha cambiado la categoría de la imagen \"dat boi\"");
				}
			}
		});
		
	}
	
	@Test
	public void t05_borrar() {
		
		try {
			servImagen.borrarPorCategoria(null);
			fail("No se ha lanzado excepción con parámetro inválido");
		} catch (ConstraintViolationException e1) {
			
		}
		
		Categoria dank = null;
		
		try {
			dank = servCategoria.getPorNombre("dank");
		} catch (ConstraintViolationException e) {
			fail("Error recuperando una categoría existente");
		} catch (NotFoundException e) {
			fail("No se ha rescatado una categoría existente");
		}
		
		try {
			servImagen.borrarPorCategoria(dank);
		} catch (ConstraintViolationException e) {
			fail("Se ha lanzado excepción con parámetros válidos");
		}
		
		List<Imagen> lista = servImagen.getAll();
		if(lista.size()!=2) {
			fail("Tras el borrado deberían haber 2 imágenes y hay: " + lista.size());
		}
		
	}

}
