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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.bcadaval.memefinder3020.AppTest;
import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.excepciones.NotFoundException;
import com.bcadaval.memefinder3020.modelo.beans.Categoria;
import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;
import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenBusqueda;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenTemp;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioCategoria;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioEtiqueta;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioImagen;
import com.bcadaval.memefinder3020.utils.RutasUtils;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = {"classpath:config/application-test.properties", "classpath:config/hibernate-test.properties"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntegracionImagenBusqueda {

	@Autowired private ServicioImagen servImagen;
	@Autowired private ServicioEtiqueta servEtiqueta;
	@Autowired private ServicioCategoria servCategoria;
	@Autowired private RutasUtils ru;
	
	private ImagenTemp iTemp;
	private ImagenBusqueda bus;
	private File imagen1, imagen2, imagen3, imagen4;
	private String nombreArchivo1, nombreArchivo2, nombreArchivo3, nombreArchivo4;
	private String nombre1, nombre2, nombre3, nombre4;
	private String categoria1, categoria2, categoria3, categoria4;
	private List<String> etiquetas1, etiquetas2, etiquetas3, etiquetas4;
	
	private Pageable pageable;
	private Page<Imagen> resultados;
	
	
	@Before
	public void preTest() {
		
		pageable = PageRequest.of(0, 5, Direction.DESC, "id");
		resultados = null;
		
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
	public void t02_testBusquedaCategoria() {
		
		Categoria dank=null, emoji=null, vacia=null;
		try {
			dank = servCategoria.getPorNombre("dank");
			emoji = servCategoria.getPorNombre("emoji");
			vacia = servCategoria.anadir("categoría vacía");
		} catch (ConstraintViolationException e) {
			fail("No se ha podido rescatar alguna categoría con parámetros válidos");
		} catch (NotFoundException e) {
			fail("No se ha encontrado alguna categoría existente");
		}
		
		
		bus = new ImagenBusqueda();
		
		try {
			
			//2 imágenes tienen la categoría dank
			bus.setBuscarSinCategoria(false);
			bus.setBuscarSinEtiquetas(false);
			bus.setCategoria(dank);
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=2) {
				fail("Se esperaban 2 resultados y se han obtenido: " + resultados.getNumberOfElements());
			}
			
			//1 imagen tiene la categoría emoji
			bus.setCategoria(emoji);
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=1) {
				fail("Se esperaban 1 resultados y se han obtenido: " + resultados.getNumberOfElements());
			}
			
			//Ninguna imagen tiene la categoría recién añadida "vacía"
			bus.setCategoria(vacia);
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=0) {
				fail("Se esperabann 0 resultados y se han obtenido: " + resultados.getNumberOfElements());
			}
			
			//1 imagen no tiene categoría
			bus.setBuscarSinCategoria(true);
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=1) {
				fail("Se esperaban 1 resultados y se han obtenido: " + resultados.getNumberOfElements());
			}
			
		}catch(ConstraintViolationException e) {
			fail("Se ha lanzado una excepción con parámetros válidos: " + e.getMensaje());
		}
		
	}
	
	@Test
	public void t03_testBusquedaEtiqueta() {
		
		Etiqueta test1=null,test2=null,etiquetaDistinta=null;
		
		try {
			test1 = servEtiqueta.getPorNombre("test1");
			test2 = servEtiqueta.getPorNombre("test2");
			etiquetaDistinta = servEtiqueta.getPorNombre("etiqueta distinta");
		} catch (ConstraintViolationException e) {
			fail("No se ha podido rescatar alguna etiqueta con parámetros válidos");
		} catch (NotFoundException e) {
			fail("No se ha encontrado alguna categoría etiqueta");
		}
		
		bus = new ImagenBusqueda();
		bus.setEtiquetas(new ArrayList<Etiqueta>());
		bus.setBuscarSinCategoria(false);
		bus.setBuscarSinEtiquetas(false);
		
		try {
			
			//2 imágenes tienen la etiqueta test2
			bus.getEtiquetas().clear();
			bus.getEtiquetas().add(test2);
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=2) {
				fail("Se esperaban 2 resultados y se han obtenido: " + resultados.getNumberOfElements());
			}
			
			//1 imagen tiene las etiquetas test2 y test1
			bus.getEtiquetas().add(test1);
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=1) {
				fail("Se esperaban 1 resultados y se han obtenido: " + resultados.getNumberOfElements());
			}
			
			//Ninguna imagen tiene las dos anteriores y etiquetaDistinta 
			bus.getEtiquetas().add(etiquetaDistinta);
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=0) {
				fail("Se esperaban 0 resultados y se han obtenido: " + resultados.getNumberOfElements());
			}
			
			//1 imagen no tiene etiquetas (sin borrar lista de etiquetas en ImagenBusqueda)
			bus.setBuscarSinEtiquetas(true);
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=1) {
				fail("Se esperaban 1 resultados y se han obtenido: " + resultados.getNumberOfElements());
			}
			
			//1 imagen no tiene etiquetas (borrando lista de etiquetas en ImagenBusqueda)
			bus.getEtiquetas().clear();
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=1) {
				fail("Se esperaban 1 resultados y se han obtenido: " + resultados.getNumberOfElements());
			}
			
			//Al buscar con etiquetas pero ninguna etiqueta en la lista, debería devolver las 4 imágenes
			bus.setBuscarSinEtiquetas(false);
			bus.getEtiquetas().clear();
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=4) {
				fail("Se esperaban 4 resultados y se han obtenido: " + resultados.getNumberOfElements());
			}
			
		}catch(ConstraintViolationException e) {
			fail("Se ha lanzado una excepción con parámetros válidos: " + e.getMensaje());
		}
		
	}
	
	@Test
	public void t04_testBusquedaResto() {
		
		bus = new ImagenBusqueda();
		bus.setBuscarSinCategoria(false);
		bus.setBuscarSinEtiquetas(false);
		bus.setEtiquetas(new ArrayList<Etiqueta>());
		
		try {
			
			//Una cadena vacía debe devolver lo mismo que buscar sin nombre
			bus.setNombre("");
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=4) {
				fail("Se esperaban 4 resultados y se han obtenido: " + resultados.getNumberOfElements());
			}
			
			//Una cadena de espacios debe devolver lo mismo que buscar sin nombre
			bus.setNombre("  ");
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=4) {
				fail("Se esperaban 4 resultados y se han obtenido: " + resultados.getNumberOfElements());
			}
			
			//Se debe buscar independientemente de mayúsculas-minúsculas
			bus.setNombre("dAt BoI");
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=1) {
				fail("Se esperaban 1 resultados y se han obtenido: " + resultados.getNumberOfElements());
			}
			
			//Se debe buscar por fragmentos del nombre
			bus.setNombre("pensa");
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=1) {
				fail("Se esperaban 1 resultados y se han obtenido: " + resultados.getNumberOfElements());
			}
			
			//Se debe buscar trimeando el nombre
			bus.setNombre(" pensa   ");
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=1) {
				fail("Se esperaban 1 resultados y se han obtenido: " + resultados.getNumberOfElements());
			}
			
			//Los tres anteriores combinados
			bus.setNombre("  eNsA ");
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=1) {
				fail("Se esperaban 1 resultados y se han obtenido: " + resultados.getNumberOfElements());
			}
			
		}catch(ConstraintViolationException e) {
			fail("Se ha lanzado una excepción con parámetros válidos: " + e.getMensaje());
		}
		
	}
	
	@Test
	public void t05_testPerogrullo() {
		
		String nuevoNombre = "te la mamaste wey";
		
		bus = new ImagenBusqueda();
		bus.setBuscarSinCategoria(false);
		bus.setBuscarSinEtiquetas(false);
		bus.setEtiquetas(new ArrayList<Etiqueta>());
		
		try {
			
			bus.setNombre("A%';UPDATE IMAGEN SET NOMBRE='" + nuevoNombre + "' WHERE 1=1;SELECT * FROM IMAGEN WHERE NOMBRE LIKE '%A");
			resultados = servImagen.getBusqueda(bus, pageable);
			bus.setNombre(nuevoNombre);
			resultados = servImagen.getBusqueda(bus, pageable);
			if(resultados.getNumberOfElements()!=0) {
				fail("Es usted susceptible a una inyección SQL, comunique con el DBA de este proyecto y cántele las cuarenta");
			}
			
		}catch(ConstraintViolationException e) {
			fail("Se ha lanzado una excepción con parámetros válidos: " + e.getMensaje());
		}
		
	}
	

}
