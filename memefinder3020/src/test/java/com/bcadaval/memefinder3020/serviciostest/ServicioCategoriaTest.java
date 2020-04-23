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
import com.bcadaval.memefinder3020.modelo.beans.Categoria;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioCategoria;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = {"classpath:config/application-test.properties", "classpath:config/hibernate-test.properties"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServicioCategoriaTest {

	private final static String NOMBRECATEGORIA1 = "test1";
	private final static String NOMBRECATEGORIA2 = "test2";
	private final static String NOMBRECATEGORIA3 = "test3";
	
	@Autowired ServicioCategoria serv;
	
	@Test
	public void t01_servicioNoNull() {
		assertTrue(serv!=null);
	}
	
	@Test
	public void t02_testAnadir() {
		
		List<Categoria> primeraVez = serv.getAll();
		assertTrue("La tabla de categorías no está vacía", primeraVez.isEmpty());
		
		try {
			serv.anadir(NOMBRECATEGORIA1);
		} catch (ConstraintViolationException e) {
			fail("Se ha lanzado una excepción al crear la primera categoría");
		}
		
		List<Categoria> lista = serv.getAll();
		if(lista.isEmpty()) {
			fail("La primera categoría no se ha creado");
		}
		
		try {
			serv.anadir(null);
			fail("Se ha permitido crear una categoría null");
		} catch (ConstraintViolationException e) {
		}
		
		try {
			serv.anadir("");
			fail("Se ha permitido crear una categoría con nombre vacío");
		} catch (ConstraintViolationException e) {
		}
		
		try {
			serv.anadir("    ");
			fail("Se ha permitido crear una categoría con nombre vacío (espacios en blanco)");
		} catch (ConstraintViolationException e) {
		}
		
		try {
			serv.anadir(NOMBRECATEGORIA1);
			fail("Se ha permitido crear una categoría con nombre repetido");
		} catch (ConstraintViolationException e) {
		}
		
		try {
			serv.anadir("   test1 ");
			fail("El servicio no trimea la categoría");
		} catch (ConstraintViolationException e) {
		}
		
		try {
			serv.anadir("TEST1");
			fail("El servicio no convierte a mayúsculas la categoría");
		} catch (ConstraintViolationException e) {
		}
		
		try {
			serv.anadir(" tEsT1     ");
			fail("El servicio no convierte a mayúsculas o no trimea la categoría");
		} catch (ConstraintViolationException e) {
		}
		
		try {
			Categoria cat = serv.anadir(NOMBRECATEGORIA2);
			assertTrue(cat!=null);
		} catch (ConstraintViolationException e) {
			fail("Error desconocido");
		}
		
		List<Categoria> lista2 = serv.getAll();
		
		if(lista2.size()!=2) {
			fail("No se han registrado las dos categorías creadas");
		}
		
	}
	
	@Test
	public void t03_testGetPorNombre() {
		
		try {
			serv.getPorNombre(null);
			fail("El método ha aceptado una categoría nula");
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
			Categoria cat = serv.getPorNombre("nombretotalmenteválido");
			if(cat==null) {
				fail("El método devuelve null en vez de lanzar NotFound");
			}else {
				fail("El método devuelve una categoría inexistente");
			}
		} catch (ConstraintViolationException e) {
			fail("El método no ha aceptado un nombre válido");
		} catch (NotFoundException e) {
			
		}
		
		try {
			Categoria et = serv.getPorNombre(NOMBRECATEGORIA1);
			if(et==null) {
				fail("El método devuelve null para una categoría existente");
			}
		} catch (ConstraintViolationException e) {
			fail("El método no ha aceptado un nombre válido");
		} catch (NotFoundException e) {
			fail("El método no devuelve una categoría existente");
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
		
		Categoria cat = null;
		
		try {
			cat = serv.getPorNombre(NOMBRECATEGORIA1);
		} catch (ConstraintViolationException e) {
			fail("Error trayendo categoría de BD");
		} catch (NotFoundException e) {
			fail("No se ha encontrado una categoría que debería estar en BD");
		}
		
		try {
			cat = serv.editar(cat, NOMBRECATEGORIA2);
			fail("Se ha permitido cambiar el nombre al de otra categoría existente");
		} catch (ConstraintViolationException e1) {
			
		}
		
		try {
			cat = serv.editar(cat, "nuevoNombre");
		} catch (ConstraintViolationException e) {
			fail("No se ha podido cambiar el nombre");
		}
		
		try {
			cat = serv.editar(cat, NOMBRECATEGORIA1);
		} catch (ConstraintViolationException e) {
			fail("Fallo al cambiar el nombre al anterior");
		}
		
		if(!cat.getNombre().equals(NOMBRECATEGORIA1.toUpperCase())) {
			fail("No se ha editado al mismo nombre String que el anterior");
		}
		
	}
	
	@Test
	public void t06_testEliminar() {
		
		Categoria cat = null;
		
		try {
			cat = serv.anadir(NOMBRECATEGORIA3);
		} catch (ConstraintViolationException e1) {
			fail("No se ha podido añadir una categoría para el test");
		}
		
		try {
			serv.eliminar(cat);
		}catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		
		try {
			cat = serv.getPorNombre(NOMBRECATEGORIA3);
			fail("Se ha rescatado una categoría eliminada");
		} catch (ConstraintViolationException e) {
			fail("Error rescatando una categoría que NO existe");
		} catch (NotFoundException e) {
			
		}
		
	}
	
	@Test
	public void t07_testIntegracionCategorias_1() {
		
		try {
			
			Categoria original = serv.getPorNombre(NOMBRECATEGORIA1);
			Categoria et1 = serv.getOCrear(NOMBRECATEGORIA1);
			
			if(!et1.getId().equals(original.getId())) {
				fail("El método no ha devuelto una categoría con la misma id que getPorNombre()");
			}
			
			if(!et1.getNombre().equals(original.getNombre())) {
				fail("El método no ha devuelto una categoría con el mismo nombre que getPorNombre()");
			}
			
		} catch (ConstraintViolationException e) {
			fail("No se ha podido rescatar una categoría existente (ConstraintViolation)");
		} catch (NotFoundException e) {
			fail("No se ha encontrado una categoría existente (NotFound)");
		}
		
	}
	
	@Test
	public void t08_testIntegracionCategorias_2() {
		
		try {
			serv.getPorNombre(NOMBRECATEGORIA3);
			fail("Se ha podido rescatar una categoría inexistente");
		} catch (ConstraintViolationException e) {
			fail("Se ha devuelto ConstraintViolation con un método válido");
		} catch (NotFoundException e) {
			
		}
		
		Categoria nueva = null;
		
		try {
			nueva = serv.getOCrear(NOMBRECATEGORIA3);
		} catch (ConstraintViolationException e) {
			fail("No se ha permitido crear/rescatar una categoría con nombre válido");
		}
		
		try {
			Categoria rescatada = serv.getPorNombre(NOMBRECATEGORIA3);
			
			if(!nueva.getId().equals(rescatada.getId())) {
				fail("Las categorías rescatada y creada no coinciden en ID");
			}
			
			if(!nueva.getNombre().equals(rescatada.getNombre())) {
				fail("Las categorías rescatada y creada no coinciden en NOMBRE");
			}
			
			
		} catch (ConstraintViolationException e) {
			fail("Se ha devuelto ConstraintViolation con un método válido");
		} catch (NotFoundException e) {
			fail("No se ha podido encontrar una categoría que sí existe");
		}
		
	}

}
