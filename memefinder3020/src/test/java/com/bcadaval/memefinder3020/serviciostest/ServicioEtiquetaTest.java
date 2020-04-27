package com.bcadaval.memefinder3020.serviciostest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.excepciones.NotFoundException;
import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioCategoria;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioEtiqueta;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioImagen;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = {"classpath:application-test.properties", "classpath:hibernate-test.properties"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServicioEtiquetaTest {

	private final static String NOMBREETIQUETA1 = "test1";
	private final static String NOMBREETIQUETA2 = "test2";
	private final static String NOMBREETIQUETA3 = "test3";
	
	@Autowired private ServicioEtiqueta serv;
	
	@Autowired private ServicioCategoria tServCat;
	@Autowired private ServicioImagen tServIm;
	
	@Test
	public void t01_servicioNoNull() {
		assertTrue(serv!=null);
		serv.getAll().forEach(serv::eliminar);
		tServCat.getAll().forEach(tServCat::eliminar);
		tServIm.getAll().forEach(tServIm::eliminar);
	}
	
	@Test
	public void t02_testAnadir() {
		
		List<Etiqueta> primeraVez = serv.getAll();
		assertTrue("La tabla de etiquetas no está vacía", primeraVez.isEmpty());
		
		try {
			serv.anadir(NOMBREETIQUETA1);
		} catch (ConstraintViolationException e) {
			fail("Se ha lanzado una excepción al crear la primera etiqueta");
		}
		
		List<Etiqueta> lista = serv.getAll();
		if(lista.isEmpty()) {
			fail("La primera etiqueta no se ha creado");
		}
		
		try {
			serv.anadir(null);
			fail("Se ha permitido crear una etiqueta null");
		} catch (ConstraintViolationException e) {
		}
		
		try {
			serv.anadir("");
			fail("Se ha permitido crear una etiqueta con nombre vacío");
		} catch (ConstraintViolationException e) {
		}
		
		try {
			serv.anadir("    ");
			fail("Se ha permitido crear una etiqueta con nombre vacío (espacios en blanco)");
		} catch (ConstraintViolationException e) {
		}
		
		try {
			serv.anadir(NOMBREETIQUETA1);
			fail("Se ha permitido crear una etiqueta con nombre repetido");
		} catch (ConstraintViolationException e) {
		}
		
		try {
			serv.anadir("   test1 ");
			fail("El servicio no trimea la etiqueta");
		} catch (ConstraintViolationException e) {
		}
		
		try {
			serv.anadir("TEST1");
			fail("El servicio no convierte a mayúsculas la etiqueta");
		} catch (ConstraintViolationException e) {
		}
		
		try {
			serv.anadir(" tEsT1     ");
			fail("El servicio no convierte a mayúsculas o no trimea la etiqueta");
		} catch (ConstraintViolationException e) {
		}
		
		try {
			Etiqueta et = serv.anadir(NOMBREETIQUETA2);
			assertTrue(et!=null);
		} catch (ConstraintViolationException e) {
			fail("Error desconocido");
		}
		
		List<Etiqueta> lista2 = serv.getAll();
		
		if(lista2.size()!=2) {
			fail("No se han registrado las dos etiquetas creadas");
		}
		
	}
	
	@Test
	public void t03_testGetPorNombre() {
		
		try {
			serv.getPorNombre(null);
			fail("El método ha aceptado una etiqueta nula");
		} catch (ConstraintViolationException e) {
		} catch (NotFoundException e) {
			fail("El método no ha validado el parámetro");
		}
		
		try {
			serv.getPorNombre("");
			fail("El método ha aceptado un string vacío");
		} catch (ConstraintViolationException e) {
		} catch (NotFoundException e) {
			fail("El método no ha validado el parámetro");
		}
		
		try {
			Etiqueta et = serv.getPorNombre("nombretotalmenteválido");
			if(et==null) {
				fail("El método devuelve null en vez de lanzar NotFound");
			}else {
				fail("El método devuelve una etiqueta inexistente");
			}
		} catch (ConstraintViolationException e) {
			fail("El método no ha aceptado un nombre válido");
		} catch (NotFoundException e) {
			
		}
		
		try {
			Etiqueta et = serv.getPorNombre(NOMBREETIQUETA1);
			if(et==null) {
				fail("El método devuelve null para una etiqueta existente");
			}
		} catch (ConstraintViolationException e) {
			fail("El método no ha aceptado un nombre válido");
		} catch (NotFoundException e) {
			fail("El método no devuelve una etiqueta existente");
		}
		
	}
	
	@Test
	public void t04_testGetOCrear() {
		
		try {
			serv.getOCrear(null);
			fail("El método no ha lanzado excepción con un parámetro nulo");
		} catch (ConstraintViolationException e) {
			
		}
		
		try {
			serv.getOCrear("");
			fail("El método no ha lanzado excepción con un parámetro vacío");
		} catch (ConstraintViolationException e) {
			
		}
		
		try {
			serv.getOCrear("      ");
			fail("El método no ha lanzado excepción con un parámetro vacío (espacios en blanco)");
		} catch (ConstraintViolationException e) {
			
		}
		
	}
	
	@Test
	public void t05_testEditar() {
		
		Etiqueta et = null;
		
		try {
			et = serv.getPorNombre(NOMBREETIQUETA1);
		} catch (ConstraintViolationException e) {
			fail("Error trayendo etiqueta de BD");
		} catch (NotFoundException e) {
			fail("No se ha encontrado una etiqueta que debería estar en BD");
		}
		
		try {
			et = serv.editar(et, NOMBREETIQUETA2);
			fail("Se ha permitido cambiar el nombre al de otra etiqueta existente");
		} catch (ConstraintViolationException e1) {
			
		}
		
		try {
			et = serv.editar(et, "nuevoNombre");
		} catch (ConstraintViolationException e) {
			e.printStackTrace();
			fail("No se ha podido cambiar el nombre");
		}
		
		try {
			et = serv.editar(et, NOMBREETIQUETA1);
		} catch (ConstraintViolationException e) {
			fail("Fallo al cambiar el nombre al anterior");
		}
		
		if(!et.getNombre().equals(NOMBREETIQUETA1.toUpperCase())) {
			fail("No se ha editado al mismo nombre String que el anterior");
		}
		
	}
	
	@Test
	public void t06_testEliminar() {
		
		Etiqueta et = null;
		
		try {
			et = serv.anadir(NOMBREETIQUETA3);
		} catch (ConstraintViolationException e1) {
			fail("No se ha podido añadir una etiqueta para el test");
		}
		
		serv.eliminar(et);
		
		try {
			et = serv.getPorNombre(NOMBREETIQUETA3);
			fail("Se ha rescatado una etiqueta eliminada");
		} catch (ConstraintViolationException e) {
			fail("Error rescatando una etiqueta que NO existe");
		} catch (NotFoundException e) {
			
		}
		
	}
	
	@Test
	public void t07_testIntegracionEtiquetas_1() {
		
		try {
			
			Etiqueta original = serv.getPorNombre(NOMBREETIQUETA1);
			Etiqueta et1 = serv.getOCrear(NOMBREETIQUETA1);
			
			if(!et1.getId().equals(original.getId())) {
				fail("El método no ha devuelto una etiqueta con la misma id que getPorNombre()");
			}
			
			if(!et1.getNombre().equals(original.getNombre())) {
				fail("El método no ha devuelto una etiqueta con el mismo nombre que getPorNombre()");
			}
			
		} catch (ConstraintViolationException e) {
			fail("No se ha podido rescatar una etiqueta existente (ConstraintViolation)");
		} catch (NotFoundException e) {
			e.printStackTrace();
			fail("No se ha encontrado una etiqueta existente (NotFound)");
		}
		
	}
	
	@Test
	public void t08_testIntegracionEtiquetas_2() {
		
		try {
			serv.getPorNombre(NOMBREETIQUETA3);
			fail("Se ha podido rescatar una etiqueta inexistente");
		} catch (ConstraintViolationException e) {
			fail("Se ha devuelto ConstraintViolation con un método válido");
		} catch (NotFoundException e) {
			
		}
		
		Etiqueta nueva = null;
		
		try {
			nueva = serv.getOCrear(NOMBREETIQUETA3);
		} catch (ConstraintViolationException e) {
			fail("No se ha permitido crear/rescatar una etiqueta con nombre válido");
		}
		
		try {
			Etiqueta rescatada = serv.getPorNombre(NOMBREETIQUETA3);
			
			if(!nueva.getId().equals(rescatada.getId())) {
				fail("Las etiquetas rescatada y creada no coinciden en ID");
			}
			
			if(!nueva.getNombre().equals(rescatada.getNombre())) {
				fail("Las etiquetas rescatada y creada no coinciden en NOMBRE");
			}
			
			
		} catch (ConstraintViolationException e) {
			fail("Se ha devuelto ConstraintViolation con un método válido");
		} catch (NotFoundException e) {
			fail("No se ha podido encontrar una etiqueta que sí existe");
		}
		
	}
	
}
