package com.bcadaval.memefinder3020;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.bcadaval.memefinder3020.modelo.servicios.ServicioImagen;
import com.bcadaval.memefinder3020.utils.RutasUtils;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AppTest {
	
	@Autowired private ServicioImagen servicioImagen;
	@Autowired private RutasUtils ru;
	
	@Test
	public void test1() {
		assertTrue(servicioImagen!=null);
	}
	
	@Test
	public void test2() {
		System.out.println(ru.DIRECTORIO_BASE_AC);
		System.out.println(ru.RUTA_BD_AA);
		System.out.println(ru.RUTA_IMAGENES_AC);
		assertTrue(true);
	}
	
}
